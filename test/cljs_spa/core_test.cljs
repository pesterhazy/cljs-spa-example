(ns cljs-spa.core-test
  (:require [clojure.test :refer-macros [deftest testing is]]))

(deftest hello-test
  (testing "hello"
    (is (= :foo :bar))))
