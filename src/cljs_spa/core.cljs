(ns cljs-spa.core
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [accountant.core :as accountant]
            [bidi.bidi :as bidi]))

(defonce !state (r/atom nil))

(declare get-users)
(declare get-user)

(defn go-to [route]
  (js/console.warn "go-to" route)
  (swap! !state assoc :route route)
  (let [{:keys [handler route-params]} route]
    (case handler
      :users
      (get-users)
      :user
      (get-user route-params)
      nil)))

;; ---

(defn inspector-ui []
  [:pre [:code (with-out-str (pprint @!state))]])

;; ---

(def my-routes ["/" {"" :home
                     "users" :users
                     "users/" {[:id] :user}}])

(defn path-for [& args]
  (str "#" (apply bidi/path-for my-routes args)))

(defn setup-router []
  (let [opts {:nav-handler
              (fn [path]
                (go-to (bidi/match-route my-routes
                                         (-> path (str/split #"#" 2) last))))
              :path-exists?
              (fn [path]
                (boolean (bidi/match-route my-routes path)))}]
    (accountant/configure-navigation! opts)))

;; ---

(defn get-users []
  (-> (js/fetch "https://reqres.in/api/users?page=1")
      (.then #(.json %))
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
  (-> (js/fetch (str "https://reqres.in/api/users/" id "?page=1"))
      (.then #(.json %))
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

(defn not-found-ui []
  [:div "Not Found"])


(defn main []
  [:div
   [:main
    [:article
     (let [{:keys [handler route-params]} (-> @!state :route)]
       (case handler
         :users
         [users-ui]
         :user
         [user-ui route-params]
         [not-found-ui]))]]
   [:footer
    [inspector-ui]]])

(defn render []
  (r/render [main] (.getElementById js/document "app")))

(defn init []
  (setup-router)
  (render)
  (accountant/dispatch-current!))

(init)
