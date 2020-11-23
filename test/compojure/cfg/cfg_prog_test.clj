(ns compojure.cfg.cfg-prog-test
  (:require [clojure.test :refer [deftest is testing]]
            [compojure.cfg.cfg-prog :refer [to-cfg-node
                                            to-cfg-fn-def
                                            to-cfg-prog]]
            [compojure.cfg.cfg-lang :as cfgl]
            [compojure.e-lang.e-lang :as e]))

(deftest to-cfg-node-test
  (testing "Assignment statement"
    (is (= [{1 (cfgl/->Assignment
                "a"
                (e/->Int 4)
                0)}
            1
            2]
           (to-cfg-node
            (e/->Assignment "a"
                            (e/->Int 4))
            {}
            1
            0))))

  (testing "Print statement"
    (is (= [{1 (cfgl/->Print
                (e/->Variable "a")
                0)}
            1
            2]
           (to-cfg-node
            (e/->Print (e/->Variable "a"))
            {}
            1
            0))))

  (testing "Return statement"
    (is (= [{1 (cfgl/->Return
                (e/->Variable "a"))}
            1
            2]
           (to-cfg-node
            (e/->Return (e/->Variable "a"))
            {}
            1
            0))))

  (testing "Block statement"
    (is (= [{1 (cfgl/->Print
                (e/->Variable "a")
                0)
             2 (cfgl/->Assignment
                "a"
                (e/->Int 8)
                1)}
            2
            3]
           (to-cfg-node
            (e/->Block
             [(e/->Assignment "a" (e/->Int 8))
              (e/->Print (e/->Variable "a"))])
            {}
            1
            0))))

  (testing "If/else statements"
    (testing "With both then and else branches"
      (is (= [{1 (cfgl/->Print (e/->Int 1) 0)
               2 (cfgl/->Print (e/->Int -1) 0)
               3 (cfgl/->Condition (e/->Variable "a") 1 2)}
              3
              4]
             (to-cfg-node
              (e/->IfThenElse (e/->Variable "a")
                              (e/->Print (e/->Int 1))
                              (e/->Print (e/->Int -1)))
              {}
              1
              0)))))
  (testing "Without an else branch"
    (is (= [{1 (cfgl/->Print (e/->Int 1) 0)
             2 (cfgl/->Condition (e/->Variable "a") 1 0)}
            2
            3]
           (to-cfg-node
            (e/->IfThenElse (e/->Variable "a")
                            (e/->Print (e/->Int 1))
                            nil)
            {}
            1
            0))))

  (testing "While loop statement"
    (is (= [{1 (cfgl/->Condition (e/->Variable "a") 2 0)
             2 (cfgl/->Assignment
                "a"
                (e/->BinaryExpr :SUM
                                (e/->Variable "a")
                                (e/->Int 1))
                1)}
            1
            3]
           (to-cfg-node
            (e/->WhileLoop (e/->Variable "a")
                           (e/->Assignment
                            "a"
                            (e/->BinaryExpr :SUM
                                            (e/->Variable "a")
                                            (e/->Int 1))))
            {}
            1
            0)))))


(def mock-e-fn (e/->FunctionDef
                "myTestFun"
                ["a"]
                (e/->Block [(e/->Assignment "a"
                                            (e/->Int 8))
                            (e/->Return (e/->Variable "a"))])))
(def expected-cfg-fn (cfgl/->FunctionDef
                      "myTestFun"
                      ["a"]
                      {1 (cfgl/->Return (e/->Variable "a"))
                       2 (cfgl/->Assignment "a"
                                            (e/->Int 8)
                                            1)}
                      2))

(deftest to-cfg-fn-def-test
  (is (= expected-cfg-fn
         (to-cfg-fn-def mock-e-fn))))

(deftest to-cfg-prog-test
  (is (= (cfgl/->Program [expected-cfg-fn])
         (to-cfg-prog (e/->Program [mock-e-fn])))))