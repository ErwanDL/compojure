(ns compojure.e-lang-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.e-lang :as e]))


(def test-main-fn (e/->FunctionDef
                   (e/->Identifier "main")
                   []
                   (e/->Block [])))

(def test-other-fn (e/->FunctionDef
                    (e/->Identifier "other")
                    [(e/->Identifier "n")]
                    (e/->Block [])))

(deftest get-main-fn-test
  (is (= test-main-fn
         (e/get-main-fn (e/->Program [test-other-fn test-main-fn])))))

(deftest evaluate-int-test
  (is (= 10
         (let [state (atom {})]
           (.evaluate (e/->Int 10) state)))))

(deftest evaluate-identifier-test
  (is (= 5
         (let [state (atom {"a" 5})]
           (.evaluate (e/->Identifier "a") state))))
  (is (thrown? java.lang.RuntimeException
               (let [state (atom "b" 4)]
                 (.evaluate (e/->Identifier "a") state)))))

(deftest evaluate-binary-expr-test
  (is (= 6
         (let [state (atom {"a" 5})]
           (.evaluate
            (e/->BinaryExpr :SUM (e/->Identifier "a") (e/->Int 1))
            state))))
  (is (= 50
         (let [state (atom {"a" 5 "b" 10})]
           (.evaluate
            (e/->BinaryExpr :MULTIPLICATION (e/->Identifier "a") (e/->Identifier "b"))
            state))))
  (is (= 4
         (let [state (atom {})]
           (.evaluate
            (e/->BinaryExpr :DIVISION (e/->Int 9) (e/->Int 2))
            state))))
  (is (= true
         (let [state (atom {})]
           (.evaluate
            (e/->BinaryExpr :NOT_EQ (e/->Int -5) (e/->Int 1))
            state))))
  (is (= true
         (let [state (atom {"a" 5 "b" 5})]
           (.evaluate
            (e/->BinaryExpr :EQ (e/->Identifier "a") (e/->Identifier "b"))
            state))))
  (is (= false
         (let [state (atom {"a" 6})]
           (.evaluate
            (e/->BinaryExpr :LESSER (e/->Identifier "a") (e/->Int 1))
            state)))))