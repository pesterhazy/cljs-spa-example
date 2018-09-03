(ns cljs-spa.test-runner
  (:require [clojure.test :refer-macros [deftest testing is run-tests]]
            [cljs-spa.core-test] ;; for side-effects
            [cljs-test-display.core :as td]))

(defn test-run []
  (run-tests (cljs-test-display.core/init! "app-test")
             'cljs-spa.core-test))

(defn -main [& args]
  (println "ARGS:" (pr-str args)))

(test-run)
