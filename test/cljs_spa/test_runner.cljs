(ns cljs-spa.test-runner
  (:require [clojure.test :refer-macros [deftest testing is run-tests]]
            [figwheel.main.testing :refer-macros [run-tests-async]]
            [cljs-spa.core-test] ;; for side-effects
            [cljs-test-display.core :as td]))

(defn -main [& args]
  (run-tests-async 10000))
