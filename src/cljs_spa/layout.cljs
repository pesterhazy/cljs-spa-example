(ns cljs-spa.layout
  (:require [reagent.core :as r]
            [cljs-spa.state :refer [!state]]))

(defn loading-ui [] [:div.loading])

(defn page-state-ui []
  (case (:page-state @!state)
    :loading [loading-ui]
    :loaded (let [children (r/children (r/current-component))]
              (assert (= 1 (count children)))
              (first children))
    :failed [:div ":-("]
    nil nil))

(defn not-found-ui [] [:div "Not Found"])

;;!zprint {:format :next :style :keyword-respect-nl}
(defn nav-ui []
  [:nav
   [:a {:href "#/"} "Home"]
   [:span " "]
   [:a {:href "#/users"} "Users"]
   [:span " "]
   [:a {:href "#/users/1"} "User #1"]
   [:span " "]
   [:a {:href "#/users/999"} "Invalid user"]
   [:span " "]
   [:a {:href "#/clock"} "Clock"]])

(defn layout-ui []
  [:div [nav-ui]
   [:main
    [:article
     (let [children (r/children (r/current-component))]
       (assert (= 1 (count children)))
       (first children))]]])
