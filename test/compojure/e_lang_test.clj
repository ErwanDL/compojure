(ns compojure.e-lang-test
  (:require [clojure.test :refer [deftest is assert-expr
                                  do-report testing]]
            [compojure.e-lang :as e :refer [evaluate]]))


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
         (->> {}
              (evaluate (e/->Int 10))))))

(deftest evaluate-identifier-test
  (is (= 5
         (->> {"a" 5}
              (evaluate (e/->Identifier "a")))))
  (is (thrown? clojure.lang.ExceptionInfo
               (->> {"b" 4}
                    (evaluate (e/->Identifier "a"))))))

(deftest evaluate-binary-expr-test
  (is (= 6
         (->> {"a" 5}
              (evaluate
               (e/->BinaryExpr :SUM (e/->Identifier "a") (e/->Int 1))))))
  (is (= 50
         (->> {"a" 5 "b" 10}
              (evaluate
               (e/->BinaryExpr :MULTIPLICATION (e/->Identifier "a") (e/->Identifier "b"))))))
  (is (= 4
         (->> {}
              (evaluate
               (e/->BinaryExpr :DIVISION (e/->Int 9) (e/->Int 2))))))
  (is (= true
         (->> {}
              (evaluate
               (e/->BinaryExpr :NOT_EQ (e/->Int -5) (e/->Int 1))))))
  (is (= true
         (->> {"a" 5 "b" 5}
              (evaluate
               (e/->BinaryExpr :EQ (e/->Identifier "a") (e/->Identifier "b"))))))
  (is (= false
         (->> {"a" 6}
              (evaluate
               (e/->BinaryExpr :LESSER (e/->Identifier "a") (e/->Int 1)))))))

(deftest execute-assignment-test
  (is (= {"a" 2 "b" 4}
         (->> {}
              (.execute (e/->Assignment
                         (e/->Identifier "a")
                         (e/->Int  2)))
              (.execute (e/->Assignment
                         (e/->Identifier "b")
                         (e/->BinaryExpr :MULTIPLICATION
                                         (e/->Identifier "a")
                                         (e/->Int 2))))))))

(deftest truthy?-test
  (is (e/truthy? 5))
  (is (e/truthy? true))
  (is (not (e/truthy? 0)))
  (is (not (e/truthy? false)))
  (is (not (e/truthy? nil))))

(defn generate-test-if-then-else [cond-expr]
  (e/->IfThenElse
   cond-expr
   (e/->Assignment (e/->Identifier "a") (e/->Int 1))
   (e/->Assignment (e/->Identifier "a") (e/->Int -1))))
(deftest execute-if-then-else-test
  (is (= {"a" 1}
         (->> {}
              (.execute (generate-test-if-then-else
                         (e/->BinaryExpr
                          :LESSER (e/->Int 0) (e/->Int 1)))))))
  (is (= {"a" -1}
         (->> {}
              (.execute (generate-test-if-then-else
                         (e/->BinaryExpr
                          :GREATER (e/->Int 0) (e/->Int 1))))))))

(defn generate-test-while-loop [cond-expr]
  (e/->WhileLoop
   cond-expr
   (e/->Assignment
    (e/->Identifier "a")
    (e/->BinaryExpr :SUM (e/->Identifier "a") (e/->Int 1)))))
(deftest execute-while-loop-test
  (is (= {"a" 5}
         (->> {"a" 0}
              (.execute (generate-test-while-loop
                         (e/->BinaryExpr :LESSER
                                         (e/->Identifier "a")
                                         (e/->Int 5)))))))
  (is (= {"a" 0}
         (->> {"a" 0}
              (.execute (generate-test-while-loop
                         (e/->Identifier "a")))))))

(deftest execute-block-test
  (is (= {"a" 2, "b" -8}
         (->> {}
              (.execute (e/->Block
                         [(e/->Assignment (e/->Identifier "a") (e/->Int 2))
                          (e/->Assignment (e/->Identifier "b") (e/->Int -8))]))))))

(defmethod assert-expr 'thrown-ex-info-with-data?
  ;; Taken from https://clojureverse.org/t/testing-thrown-ex-info-exceptions/6146/3
  [msg form]
  (let [data (second form)
        body (nthnext form 2)]
    `(try ~@body
          (do-report {:type :fail, :message ~msg
                      :expected '~form, :actual nil})
          (catch clojure.lang.ExceptionInfo e#
            (let [expected# ~data
                  actual# (ex-data e#)]
              (if (= expected# actual#)
                (do-report {:type :pass, :message ~msg
                            :expected expected#, :actual actual#})
                (do-report {:type :fail, :message ~msg
                            :expected expected#, :actual actual#})))
            e#))))

(deftest execute-return-test
  #_{:clj-kondo/ignore [:unresolved-symbol]}
  (is (thrown-ex-info-with-data? {:type :return-statement
                                  :return-value 4
                                  :final-state {"a" 5}}
                                 (->> {"a" 5}
                                      (.execute (e/->Return
                                                 (e/->Int 4))))))

  (testing "Nested return statement within if-else clause and block"
    (is (thrown-ex-info-with-data?
         {:type :return-statement
          :return-value 0
          :final-state {"a" 0}}
         (->> {}
              (.execute (e/->IfThenElse
                         (e/->Int 0)
                         (e/->Assignment
                          (e/->Identifier "a")
                          (e/->Int 1))
                         (e/->Block
                          [(e/->Assignment
                            (e/->Identifier "a")
                            (e/->Int 0))
                           (e/->Return
                            (e/->Identifier "a"))])))))))

  (testing "Doesn't execute other statements after a Return"
    (is (thrown-ex-info-with-data?
         {:type :return-statement
          :return-value 0
          :final-state {}}
         (->> {}
              (.execute (e/->Return (e/->Int 0)))
              (.execute (e/->Assignment (e/->Identifier "a") (e/->Int 4))))))))

(deftest execute-print-test
  (is (= "5\n"
         (binding [*out* (java.io.StringWriter.)]
           (->> {"a" 5}
                (.execute (e/->Print
                           (e/->Identifier "a"))))
           (str *out*)))))
