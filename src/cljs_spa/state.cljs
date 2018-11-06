(ns cljs-spa.state
  (:require [reagent.core :as r]))

(defonce !state (r/atom nil))

(defn loaded! [] (swap! !state assoc :page-state :loaded))
