(ns compojure.e-lang.e-exec-test
  (:require [clojure.test :refer [deftest is  testing]]
            [compojure.e-lang.e-lang :as e]
            [compojure.test-utils] ;; for ex-info-thrown-with-data?
            [compojure.e-lang.e-exec :refer [evaluate execute truthy?]]))


(deftest evaluate-int-test
  (is (= 10
         (evaluate (e/->Int 10) {}))))

(deftest evaluate-identifier-test
  (is (= 5
         (evaluate (e/->Variable "a") {"a" 5})))
  (is (thrown? clojure.lang.ExceptionInfo
               (evaluate (e/->Variable "a") {"b" 4}))))

(deftest evaluate-binary-expr-test
  (is (= 6
         (evaluate
          (e/->BinaryExpr :SUM (e/->Variable "a") (e/->Int 1))
          {"a" 5})))
  (is (= 50
         (evaluate
          (e/->BinaryExpr :MULTIPLICATION (e/->Variable "a") (e/->Variable "b"))
          {"a" 5 "b" 10})))
  (is (= 4
         (evaluate
          (e/->BinaryExpr :DIVISION (e/->Int 9) (e/->Int 2))
          {})))
  (is (= true
         (evaluate
          (e/->BinaryExpr :NOT_EQ (e/->Int -5) (e/->Int 1))
          {})))
  (is (= true
         (evaluate
          (e/->BinaryExpr :EQ (e/->Variable "a") (e/->Variable "b"))
          {"a" 5 "b" 5})))
  (is (= false
         (evaluate
          (e/->BinaryExpr :LESSER (e/->Variable "a") (e/->Int 1))
          {"a" 6}))))

(deftest execute-assignment-test
  (is (= {"a" 2 "b" 4}
         (->> {}
              (execute (e/->Assignment
                        "a"
                        (e/->Int  2)))
              (execute (e/->Assignment
                        "b"
                        (e/->BinaryExpr :MULTIPLICATION
                                        (e/->Variable "a")
                                        (e/->Int 2))))))))

(deftest truthy?-test
  (is (truthy? 5))
  (is (truthy? true))
  (is (not (truthy? 0)))
  (is (not (truthy? false)))
  (is (not (truthy? nil))))

(deftest execute-if-then-else-test
  (testing "With both then and else branches"
    (is (= {"a" 1}
           (execute (e/->IfThenElse
                     (e/->BinaryExpr
                      :LESSER (e/->Int 0) (e/->Int 1))
                     (e/->Assignment "a" (e/->Int 1))
                     (e/->Assignment "a" (e/->Int -1)))
                    {})))
    (is (= {"a" -1}
           (execute (e/->IfThenElse
                     (e/->BinaryExpr
                      :GREATER (e/->Int 0) (e/->Int 1))
                     (e/->Assignment "a" (e/->Int 1))
                     (e/->Assignment "a" (e/->Int -1)))
                    {}))))
  (testing "Without an else branch"
    (is (= {}
           (execute (e/->IfThenElse
                     (e/->Int 0)
                     (e/->Assignment "a" (e/->Int 1))
                     nil)
                    {})))))

(defn generate-test-while-loop [cond-expr]
  (e/->WhileLoop
   cond-expr
   (e/->Assignment
    "a"
    (e/->BinaryExpr :SUM (e/->Variable "a") (e/->Int 1)))))
(deftest execute-while-loop-test
  (is (= {"a" 5}
         (execute (generate-test-while-loop
                   (e/->BinaryExpr :LESSER
                                   (e/->Variable "a")
                                   (e/->Int 5)))
                  {"a" 0})))
  (is (= {"a" 0}
         (execute (generate-test-while-loop
                   (e/->Variable "a"))
                  {"a" 0}))))

(deftest execute-block-test
  (is (= {"a" 2, "b" -8}
         (execute (e/->Block
                   [(e/->Assignment "a" (e/->Int 2))
                    (e/->Assignment "b" (e/->Int -8))])
                  {}))))



(deftest execute-return-test
  (is (ex-info-thrown-with-data? {:type :return-encountered
                                  :retval 4
                                  :final-state {"a" 5}}
                                 (execute (e/->Return
                                           (e/->Int 4))
                                          {"a" 5})))

  (testing "Nested return statement within if-else clause and block"
    (is (ex-info-thrown-with-data?
         {:type :return-encountered
          :retval 0
          :final-state {"a" 0}}
         (execute (e/->IfThenElse
                   (e/->Int 0)
                   (e/->Assignment
                    "a"
                    (e/->Int 1))
                   (e/->Block
                    [(e/->Assignment
                      "a"
                      (e/->Int 0))
                     (e/->Return
                      (e/->Variable "a"))]))
                  {}))))

  (testing "Doesn't execute other statements after a Return"
    (is (ex-info-thrown-with-data?
         {:type :return-encountered
          :retval 0
          :final-state {}}
         (->> {}
              (execute (e/->Return (e/->Int 0)))
              (execute (e/->Assignment  "a" (e/->Int 4))))))))

(deftest execute-print-test
  (binding [*out* (java.io.StringWriter.)]
    (is (= {"a" 5}
           (execute (e/->Print
                     (e/->Variable "a"))
                    {"a" 5})))
    (is (= "5\n" (str *out*)))))

(def test-main-fn (e/->FunctionDef
                   "main"
                   ["a" "b"]
                   (e/->Block [(e/->Assignment
                                "b"
                                (e/->BinaryExpr :SUM (e/->Variable "a") (e/->Variable "b")))
                               (e/->Return (e/->Variable "b"))])))
