(ns compojure.e-lang.e-interpreter-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.e-lang.e-lang :as e]
            [compojure.e-lang.e-interpreter :refer [enter-function
                                                    validate-args-count
                                                    interpret-e-prog
                                                    get-main-fn]]))


(def example-main-fn (e/->FunctionDef
                      (e/->Identifier "main")
                      [(e/->Identifier "a") (e/->Identifier "b")]
                      (e/->Block [(e/->Assignment
                                   (e/->Identifier "b")
                                   (e/->BinaryExpr :SUM (e/->Identifier "a") (e/->Identifier "b")))
                                  (e/->Return (e/->Identifier "b"))])))

(deftest validate-args-count-test
  (is (nil? (validate-args-count example-main-fn [1 2])))
  (is (nil? (validate-args-count example-main-fn [1 2 4 5])))
  (is (thrown? java.lang.IllegalArgumentException
               (validate-args-count example-main-fn [1]))))

(deftest enter-function-test
  (is (= {"a" 2 "b" 6}
         (->> {}
              (enter-function example-main-fn [2 6])))))

(deftest interpret-e-prog-test
  (is (= -2
         (interpret-e-prog (e/->Program [example-main-fn]) [3 -5]))))


(def other-fn (e/->FunctionDef
               (e/->Identifier "other")
               [(e/->Identifier "n")]
               (e/->Block [])))

(deftest get-main-fn-test
  (is (= example-main-fn
         (get-main-fn (e/->Program [other-fn example-main-fn])))))

