(ns cljs-spa.core-test
  (:require [clojure.test :refer-macros [deftest testing is run-tests]]))

(deftest hello-test
  (testing "hello"
    (is (= :foo :bar2))))

(run-tests)
