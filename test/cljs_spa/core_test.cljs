(ns cljs-spa.core-test
  (:require [clojure.test :refer-macros [deftest testing is async
                                         use-fixtures]]))

(deftest hello-test
  (testing "hello"
    (is (= :foo :foo))))

(deftest async-test
  (async done
         (-> (js/Promise.all [(js/Promise. (fn [_ reject]
                                             (js/setTimeout #(reject (js/Error. "Timeout")) 500)))
                              (js/Promise. (fn [resolve]
                                             (js/setTimeout #(resolve (is (= 1 1))) 1000)))])
             (.catch (fn [e]
                       (js/console.error e)
                       (is false (.-message e))))
             (.finally done))))
