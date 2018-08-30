(ns cljs-spa.test-runner
  (:require [clojure.test :refer-macros [deftest testing is run-tests]]
            [cljs-spa.core-test] ;; for side-effects
            [cljs-test-display.core :as td]))

#_(defmethod clojure.test/report [:cljs-test-display.core/default :begin-test-var] [m]
    (js/console.warn (clojure.test/testing-vars-str m))
    (cljs-test-display.core/add-var-node m))

(defn test-run []
  (run-tests (cljs-test-display.core/init! "app")
             'cljs-spa.core-test))

(test-run)
