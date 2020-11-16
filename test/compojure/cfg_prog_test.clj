(ns compojure.cfg-prog-test
  (:require [clojure.test :refer [deftest is testing]]
            [compojure.cfg-prog :refer [to-cfg-node]]
            [compojure.cfg-lang :as cfgl]
            [compojure.e-lang :as e]))

(deftest assignment-to-cfg-node-test
  (is (= [{1 (cfgl/->Assignment
              (e/->Identifier "a")
              (e/->Int 4)
              0)}
          1
          2]
         (to-cfg-node
          (e/->Assignment (e/->Identifier "a")
                          (e/->Int 4))
          {}
          1
          0))))

(deftest print-to-cfg-node-test
  (is (= [{1 (cfgl/->Print
              (e/->Identifier "a")
              0)}
          1
          2]
         (to-cfg-node
          (e/->Print (e/->Identifier "a"))
          {}
          1
          0))))

(deftest return-to-cfg-node-test
  (is (= [{1 (cfgl/->Return
              (e/->Identifier "a"))}
          1
          2]
         (to-cfg-node
          (e/->Return (e/->Identifier "a"))
          {}
          1
          0))))

(deftest block-to-cfg-node-test
  (is (= [{1 (cfgl/->Print
              (e/->Identifier "a")
              0)
           2 (cfgl/->Assignment
              (e/->Identifier "a")
              (e/->Int 8)
              1)}
          2
          3]
         (to-cfg-node
          (e/->Block
           [(e/->Assignment (e/->Identifier "a") (e/->Int 8))
            (e/->Print (e/->Identifier "a"))])
          {}
          1
          0))))

(deftest if-then-else-to-cfg-node-test
  (testing "With both then and else branches"
    (is (= [{1 (cfgl/->Print (e/->Int 1) 0)
             2 (cfgl/->Print (e/->Int -1) 0)
             3 (cfgl/->Comparison (e/->Identifier "a") 1 2)}
            3
            4]
           (to-cfg-node
            (e/->IfThenElse (e/->Identifier "a")
                            (e/->Print (e/->Int 1))
                            (e/->Print (e/->Int -1)))
            {}
            1
            0))))
  (testing "Without an else branch"
    (is (= [{1 (cfgl/->Print (e/->Int 1) 0)
             2 (cfgl/->Comparison (e/->Identifier "a") 1 0)}
            2
            3]
           (to-cfg-node
            (e/->IfThenElse (e/->Identifier "a")
                            (e/->Print (e/->Int 1))
                            nil)
            {}
            1
            0)))))