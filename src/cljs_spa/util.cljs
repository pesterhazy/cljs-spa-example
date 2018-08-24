(ns cljs-spa.util
  (:require [reagent.core :as r]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [accountant.core :as accountant]
            [bidi.bidi :as bidi]))

(defn check-fetch [r]
  (if-not (.-ok r)
    (throw (ex-info "Could not get user" {:status (.-status r)
                                          :load-error true}))
    r))

(defn safe-fetch [& args]
  (-> (.apply js/fetch js/window (into-array args))
      (.catch (fn [e]
                (throw (ex-info "Could not fetch" {:cause e
                                                   :load-error true}))))
      (.then check-fetch)))

(defn create-routes [routes nav-handler]
  (let [opts {:nav-handler
              (fn [path]
                (nav-handler (bidi/match-route routes
                                               (-> path (str/split #"#" 2) last))))
              :path-exists?
              (fn [path]
                (boolean (bidi/match-route routes path)))}]
    (accountant/configure-navigation! opts)))
