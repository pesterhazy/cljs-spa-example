(ns cljs-spa.core
  (:require [reagent.core :as r]
            [secretary.core :as secretary :refer-macros [defroute]]
            [goog.events :as events]
            [goog.history.EventType :as EventType])
  (:import goog.History))

(defonce !state (r/atom nil))

(defn go-to [ky]
  (swap! !state assoc :ky ky))

(secretary/set-config! :prefix "#")

(defroute home-path "/" []
  (go-to :home))

;; /#/users
(defroute users-path "/users" []
  (go-to :users))

(defroute "*" []
  (go-to :notfound))

(defn inspector []
  [:pre [:code (pr-str @!state)]])

(defn main []
  [:main
   [:article
    [:h3 "Demo"]
    [inspector]]])

(defn render []
  (r/render [main] (.getElementById js/document "app")))

(def !history (delay (let [h (History.)]
                       (goog.events/listen h EventType/NAVIGATE #(secretary/dispatch! (.-token %)))
                       (doto h (.setEnabled true)))))

(defn init []
  (deref !history)
  (render))

(init)
