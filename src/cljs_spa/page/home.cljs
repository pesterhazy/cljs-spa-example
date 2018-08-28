(ns cljs-spa.page.home
  (:require [react-select :as react-select]))

(defn page-ui []
  [:h3 "Home"
   [:> (.-default react-select)]])
