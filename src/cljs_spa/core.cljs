(ns cljs-spa.core
  (:require [reagent.core :as r]
            [cljs-spa.util :as util]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [accountant.core :as accountant]
            [bidi.bidi :as bidi]))

(defonce !state (r/atom nil))

(declare get-users)
(declare get-user)

(defn default-action []
  (js/Promise.resolve))

(defn nav-handler [route]
  (swap! !state (fn [state] (-> state
                                (assoc :route route)
                                (assoc :page-state :loading))))
  (let [{:keys [handler route-params]} route]
    (-> (case handler
          :users
          (get-users)
          :user
          (get-user route-params)
          (default-action))
        (.catch (fn [e]
                  (when (-> e ex-data :load-error)
                    (swap! !state assoc :page-state :failed))
                  (throw e)))
        (.then (fn []
                 (swap! !state assoc :page-state :loaded))))))

;; --- helpers ---

(defn inspector-ui []
  [:pre [:code (with-out-str (pprint @!state))]])

;; ---

(def my-routes ["/" {"" :home
                     "users" :users
                     "users/" {[:id] :user}}])

(defonce !router
  (delay (util/create-routes my-routes nav-handler)))

(defn setup-router []
  (deref !router))

;; ---

(defn get-users []
  (-> (util/safe-fetch "https://reqres.in/api/users?page=1")
      (.then (fn [r] (.json r)))
      (.then (fn [js-data]
               (swap! !state assoc :users-data (js->clj js-data :keywordize-keys true))))))

(defn users-ui []
  [:div
   [:h3 "Users"]
   (when-let [users-data (:users-data @!state)]
     (->> users-data
          :data
          (map (fn [user]
                 [:li [:a {:href (str "#/users/" (:id user))} (:first_name user)]]))
          (into [:ul])))])

;; ---

(defn get-user [{:keys [id]}]
  (-> (util/safe-fetch (str "https://reqres.in/api/users/" id "?page=1"))
      (.then (fn [r] (.json r)))
      (.then (fn [js-data]
               (swap! !state assoc :user-data (js->clj js-data :keywordize-keys true))))))

(defn user-ui [route-params]
  [:div
   [:h3 "User"]
   (when-let [user-data (:user-data @!state)]
     [:div
      [:img {:src (-> user-data :data :avatar)}]
      (-> user-data :data :first_name)])])

;; ---

(defn home-ui []
  [:div "Home"])

;; ---

(defn not-found-ui []
  [:div "Not Found"])

;; ---


(defn main []
  [:div
   [:nav
    [:a {:href "#/"} "Home"]
    [:span " "]
    [:a {:href "#/users"} "Users"]
    [:span " "]
    [:a {:href "#/users/1"} "User #1"]
    [:span " "]
    [:a {:href "#/users/999"} "Invalid user"]]
   [:main
    [:article
     (case (:page-state @!state)
       :loading
       [:div.loading]
       :failed
       [:div ":-("]
       :loaded
       (let [{:keys [handler route-params]} (-> @!state :route)]
         (case handler
           :home
           [home-ui]
           :users
           [users-ui]
           :user
           [user-ui route-params]
           [not-found-ui]))
       nil
       [:div])]]
   [:footer
    #_[inspector-ui]]])

(defn render []
  (r/render [main] (.getElementById js/document "app")))

(defn init []
  (setup-router)
  (render)
  (accountant/dispatch-current!))

(init)
