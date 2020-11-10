(ns compojure.interpreter-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.e-lang :as e]
            [compojure.interpreter :refer [init-function-args
                                           validate-args-count
                                           eval-e-prog]]))

(def test-main-fn (e/->FunctionDef
                   (e/->Identifier "main")
                   [(e/->Identifier "a") (e/->Identifier "b")]
                   (e/->Block [])))

(deftest validate-args-count-test
  (is (nil? (validate-args-count test-main-fn [1 2])))
  (is (nil? (validate-args-count test-main-fn [1 2 4 5])))
  (is (thrown? java.lang.IllegalArgumentException
               (validate-args-count test-main-fn [1]))))

(deftest init-function-args-test
  (is (= {"a" 2 "b" 6}
         (-> {}
             (init-function-args test-main-fn [2 6])))))

(deftest eval-e-prog-test
  (is (= {"a" 3 "b" -5}
         (eval-e-prog (e/->Program [test-main-fn]) [3 -5]))))