(ns compojure.interpreter-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.e-lang :as e]
            [compojure.interpreter :refer [enter-function
                                           validate-args-count
                                           eval-e-prog]]))

(def test-main-fn (e/->FunctionDef
                   (e/->Identifier "main")
                   [(e/->Identifier "a") (e/->Identifier "b")]
                   (e/->Block [(e/->Assignment
                                (e/->Identifier "b")
                                (e/->BinaryExpr :SUM (e/->Identifier "a") (e/->Identifier "b")))
                               (e/->Return (e/->Identifier "b"))])))

(deftest validate-args-count-test
  (is (nil? (validate-args-count test-main-fn [1 2])))
  (is (nil? (validate-args-count test-main-fn [1 2 4 5])))
  (is (thrown? java.lang.IllegalArgumentException
               (validate-args-count test-main-fn [1]))))

(deftest enter-function-test
  (is (= {"a" 2 "b" 6}
         (->> {}
              (enter-function test-main-fn [2 6])))))

(deftest eval-e-prog-test
  (is (= -2
         (eval-e-prog (e/->Program [test-main-fn]) [3 -5]))))