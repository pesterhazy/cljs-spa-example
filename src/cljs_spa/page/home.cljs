(ns cljs-spa.page.home
  (:require [react-select :as react-select]))

(def options
  [{:value "simplicity" :label "simplicity"}
   {:value "data" :label "data"}
   {:value "lazy sequences" :label "lazy sequences"}])

(defn selector-ui []
  [:> (.-default react-select)
   {:isMulti true
    :options (clj->js options)}])

(defn page-ui []
  [:h3 "Home"
   [:div {:style {:margin-top 20
                  :max-width 400}}
    [:div {:style {:margin-bottom 20}} "What do you like?"]
    [selector-ui]
    ]])
