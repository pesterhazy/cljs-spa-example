(ns cljs-spa.core
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [accountant.core :as accountant]
            [bidi.bidi :as bidi]))

(defonce !state (r/atom nil))

(declare get-users)
(declare get-user)

(defn default-action []
  (js/Promise. (fn [resolve]
                 (resolve (swap! !state assoc :page-state :loaded)))))

(defn go-to [route]
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
                  (throw e))))))

;; --- helpers ---

(defn inspector-ui []
  [:pre [:code (with-out-str (pprint @!state))]])

(defn check-fetch [r]
  (if-not (.-ok r)
    (throw (ex-info "Could not get user" {:status (.-status r)
                                          :load-error true}))
    r))

(defn safe-fetch [& args]
  (-> (.apply js/fetch js/window (into-array args))
      (.catch (fn [e]
                (throw (ex-info "Generic error while fetching" {:cause e
                                                                :load-error true}))))
      (.then check-fetch)))

;; ---

(def my-routes ["/" {"" :home
                     "users" :users
                     "users/" {[:id] :user}}])

(defn path-for [& args]
  (str "#" (apply bidi/path-for my-routes args)))

(defonce !router
  (delay (let [opts {:nav-handler
                     (fn [path]
                       (go-to (bidi/match-route my-routes
                                                (-> path (str/split #"#" 2) last))))
                     :path-exists?
                     (fn [path]
                       (boolean (bidi/match-route my-routes path)))}]
           (accountant/configure-navigation! opts))))

(defn setup-router []
  (deref !router))

;; ---

(defn get-users []
  (-> (safe-fetch "https://reqres.in/api/users?page=1")
      (.then (fn [r] (.json r)))
      (.then (fn [js-data]
               (swap! !state (fn [state] (-> state
                                             (assoc :users-data (js->clj js-data :keywordize-keys true))
                                             (assoc :page-state :loaded))))))))

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
  (-> (safe-fetch (str "https://reqres.in/api/users/" id "?page=1"))
      (.then (fn [r] (.json r)))
      (.then (fn [js-data]
               (swap! !state (fn [state] (-> state
                                             (assoc :user-data (js->clj js-data :keywordize-keys true))
                                             (assoc :page-state :loaded))))))))

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
    [:a {:href "#/users/1"} "User 1"]
    [:span " "]
    [:a {:href "#/users/999"} "Non-existant user"]
    ]
   [:main
    [:article
     (case (:page-state @!state)
       :loading
       [:div.loading]
       :failed
       [:div "Failed"]
       (let [{:keys [handler route-params]} (-> @!state :route)]
         (case handler
           :home
           [home-ui]
           :users
           [users-ui]
           :user
           [user-ui route-params]
           [not-found-ui])))]]
   [:footer
    #_[inspector-ui]]])

(defn render []
  (r/render [main] (.getElementById js/document "app")))

(defn init []
  (setup-router)
  (render)
  (accountant/dispatch-current!))

(init)
