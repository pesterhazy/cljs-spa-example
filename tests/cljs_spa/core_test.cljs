(ns cljs-spa.core-test
  (:require [clojure.core]
            [clojure.test :refer-macros
             [deftest testing is async use-fixtures]]))

(defn promise-test [p]
  (reify
    clojure.test/IAsyncTest
    clojure.core/IFn
      (-invoke [_ done]
        (-> p
            (.catch (fn [e]
                      (js/console.error e)
                      (is false (str "Promise rejected: " (.-message e)))))
            (.finally done)))))

(defn slowly+
  ([ms] (slowly+ nil ms))
  ([v ms] (js/Promise. (fn [resolve reject] (js/setTimeout #(resolve v) ms)))))

(defn with-timeout+
  ([p] (with-timeout+ p 50))
  ([p ms]
   (js/Promise.race
     [p
      (-> (slowly+ ms)
          (.then
            (fn []
              (throw (js/Error.
                       (str "Promise failed to resolve in " ms "ms"))))))])))

(deftest arithmetic-test-expected-to-fail (testing "hello" (is (= 3 (+ 1 7)))))

(deftest async-test-expected-to-fail-with-timeout
  (promise-test (-> (slowly+ 500)
                    with-timeout+
                    (.then (fn [] (is (= 1 2)))))))
