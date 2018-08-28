(ns cljs-spa.page.clock
  (:require [cljs-spa.util :as util]
            [cljs-spa.state :refer [!state]]))

(defn tick []
  (js/console.log "Tick")
  (swap! !state assoc :clock-time (js/Date.)))

(defn activate []
  (tick)
  (swap! !state update :clock
         (fn [interval] (util/set-interval interval tick 1000))))

(defn deactivate []
  (swap! !state update :clock util/clear-interval))

(defn page-ui []
  [:div
   [:h3 "Clock"]
   [:div "Seconds since epoch: " (some-> @!state :clock-time int)]
   [:div
    "This pages demonstrates acquiring and disposing of resources. When the
user enters the page, a setInterval timer is created. While active, you can see
Tick messages in the console log. When the user navigates away (e.g. to #/home)
the timer is cleared."]])
