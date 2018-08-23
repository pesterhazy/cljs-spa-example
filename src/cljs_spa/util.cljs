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
                (throw (ex-info "Generic error while fetching" {:cause e
                                                                :load-error true}))))
      (.then check-fetch)))
