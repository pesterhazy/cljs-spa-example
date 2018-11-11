(ns cljs-spa.test-runner
  (:require [clojure.test :refer-macros [deftest testing is run-tests]]
            [goog.object :as gobj]
            [figwheel.main.testing :refer-macros [run-tests-async]]
            [cljs-spa.core-test] ;; for side-effects
            [cljs-test-display.core :as td]))

(defn extra-main []
  (js/console.warn "extra-main")
  (run-tests (td/init! "app-tests") 'cljs-spa.core-test))

(defn -main [& args] (js/console.warn "-main") (run-tests-async 3000))

;; Only run this at NS init time when the user
;; is visiting the extra main page

(when (= "/figwheel-extra-main/tests"
         (gobj/getValueByKeys goog/global "location" "pathname"))
  (extra-main))
