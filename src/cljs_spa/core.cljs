(ns cljs-spa.core
  (:require [reagent.core :as r]
            [bidi.router]
            [bidi.bidi]))

(defonce !state (r/atom nil))

(defn go-to [ky]
  (js/console.warn "go-to" ky)
  (swap! !state assoc :ky ky))

;; ---

(def my-routes ["/" {"" :home
                     "asdf" :asdf}])

(defn path-for [& args]
  (str "#" (apply bidi.bidi/path-for my-routes args)))

(defn setup-router []
  (bidi.router/start-router! my-routes
                             {:on-navigate (fn [route]
                                             (go-to route))
                              :default-location {:handler :hello}}))
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
  (render))

(init)
