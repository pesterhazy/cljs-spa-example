(ns cljs-spa.core
  (:require [reagent.core :as r]
            [accountant.core :as accountant]
            [bidi.bidi :as bidi]))

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
  (js/console.log "setup-router")
  (accountant/configure-navigation! {:nav-handler (fn [path] (js/console.warn "nav-handler" path))
                                     :path-exists? (fn [path] (boolean (bidi/match-route my-routes path)))}))
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
