(ns cljs-spa.page.home
  (:require ;; See https://github.com/pesterhazy/cljs-spa-example/issues/13
            [react-select :refer [default] :rename {default react-select}]
            [cljs-spa.state :refer [!state]]))

(def options
  [{:value "simplicity", :label "simplicity"}
   {:value "immutable data", :label "immutable data"}
   {:value "lazy sequences", :label "lazy sequences"}])

(defn selector-ui
  []
  [:> react-select
   {:is-multi true,
    :options (clj->js options),
    :on-change (fn [xs]
                 (swap! !state assoc
                   :selection
                   (->> (js->clj xs :keywordize-keys true)
                        (map :label)
                        (into #{}))))}])

(defn result-ui
  []
  [:div [:h3 "So you like"]
   (let [selection (:selection @!state)]
     (if (seq selection) [:div (pr-str selection)] "Nothing yet"))])

(defn page-ui
  []
  [:div {:style {:max-width 400}} [:h3 "cljs-spa-example"]
   [:div {:style {:margin-top 20, :margin-bottom 20}} "What do you like?"]
   [selector-ui] [result-ui]])
