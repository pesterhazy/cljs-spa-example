(ns cljs-spa.core
  (:require [reagent.core :as r]))

(defn main []
  [:main
   [:article
    [:h3 "Demo"]
    [:p "Hello"]]])

(defn render []
  (r/render [main] (.getElementById js/document "app")))

(render)
