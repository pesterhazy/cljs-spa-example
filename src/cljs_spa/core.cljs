(ns cljs-spa.core
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [accountant.core :as accountant]
            [bidi.bidi :as bidi]))

(defonce !state (r/atom nil))

(defn go-to [ky]
  (swap! !state assoc :route ky))

;; ---

(def my-routes ["/" {"" :home
                     "asdf" :asdf}])

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

(defn inspector []
  [:pre [:code (pr-str @!state)]])

(defn main []
  [:main
   [:article
    [:h3 "Demo"]
    [inspector]]])

(defn render []
  (r/render [main] (.getElementById js/document "app")))

(defn init []
  (setup-router)
  (render)
  (accountant/dispatch-current!))

(init)
