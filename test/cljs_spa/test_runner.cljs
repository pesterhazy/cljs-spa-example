(ns cljs-spa.test-runner
  (:require [clojure.test :refer-macros [deftest testing is run-tests]]
            [cljs-spa.core-test] ;; for side-effects
            ))

(js/console.log "NS cljs-spa.test-runner")

(defn test-run []
  (js/console.log "test-run")
  (run-tests 'cljs-spa.core-test))

(test-run)
