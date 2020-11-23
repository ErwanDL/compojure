(ns compojure.cfg.cfg-liveness-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.cfg.cfg-lang :as cfgl]
            [compojure.e-lang.e-lang :as e]
            [compojure.cfg.cfg-liveness :refer [predecessors
                                                vars-used-in-expr
                                                live-vars-after-node]]))

(deftest cfg-predecessors-test
  (is (= #{2 4}
; this CFG corresponds to the following code snippet :
; "a = 3; print(a); if (a) { a = 5; print(a); } return a;"
         (predecessors
          5
          [(cfgl/->Assignment
            "a"
            (e/->Int 3)
            1)
           (cfgl/->Print
            (e/->Variable "a")
            2)
           (cfgl/->Condition
            (e/->Variable "a")
            3
            5)
           (cfgl/->Assignment
            "a"
            (e/->Int 5)
            4)
           (cfgl/->Print
            (e/->Variable "a")
            5)
           (cfgl/->Return
            (e/->Variable "a"))]))))

(deftest vars-used-in-expr-test
  (is (= #{}
         (vars-used-in-expr (e/->Int 5))))
  (is (= #{"a"}
         (vars-used-in-expr (e/->Variable "a"))))
  (is (= #{"a" "b"}
         (vars-used-in-expr (e/->BinaryExpr
                             :SUM
                             (e/->BinaryExpr :MULTIPLICATION
                                             (e/->Variable "a")
                                             (e/->Int 4))
                             (e/->Variable "b"))))))

(deftest live-vars-after-node-test
  (is (= #{"a" "b"}
         (live-vars-after-node (cfgl/->Condition
                                (e/->Int 0) 1 2)
                               {0 #{}, 1 #{"a"}, 2 #{"b"}}))))
