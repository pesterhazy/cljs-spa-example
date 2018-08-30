(ns cljs-spa.core-test
  (:require [clojure.test :refer-macros [deftest testing is async
                                         use-fixtures]]))

(def default-timeout 500)

(defn slowly+
  ([ms]
   (slowly+ nil ms))
  ([v ms]
   (js/Promise. (fn [resolve reject] (js/setTimeout #(resolve v) ms)))))

(deftest hello-test
  (testing "hello"
    (is (= :foo :foo))))

(defn pro
  ([done f]
   (pro done f {}))
  ([done f opts]
   (-> (js/Promise.race [(js/Promise. (fn [_ reject]
                                        (js/setTimeout #(reject (js/Error. "Timeout"))
                                                       (:timeout opts default-timeout))))
                         f])
       (.catch (fn [e]
                 (js/console.error e)
                 (is false (.-message e))))
       (.finally done))))

(deftest async-test
  (async d (pro d
                (-> (slowly+ 100) ;; raise to 600ms to see failure
                    (.then (fn [] (is (= 1 1))))))))
