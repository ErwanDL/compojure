(ns compojure.cfg.liveness-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.cfg.cfg-lang :as cfgl]
            [compojure.e-lang.e-lang :as e]
            [compojure.cfg.liveness :refer [predecessors
                                            vars-used-in-expr
                                            live-vars-after-node
                                            live-vars-before-node
                                            compute-cfg-liveness]]))

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

(deftest live-vars-before-node-test
  (is (= #{"a" "b"}
         (live-vars-before-node (cfgl/->Print (e/->Variable "a") 0)
                                #{"b"})))
  (is (= #{"b" "c"}
         (live-vars-before-node (cfgl/->Assignment "a" (e/->Variable "b") 0)
                                #{"c"})))
  (is (= #{"b"}
         (live-vars-before-node (cfgl/->Assignment "a" (e/->Variable "b") 0)
                                #{"a"}))))

(def mock-cfg-1
  "a = 2;
   while (a > 0) {
     b = a;
     a = b - 1;
     print(b);
   } 
   return a;"
  {1 (cfgl/->Return (e/->Variable "a"))
   3 (cfgl/->Print (e/->Variable "b") 2)
   4 (cfgl/->Assignment "a"
                        (e/->BinaryExpr :DIFFERENCE
                                        (e/->Variable "b")
                                        (e/->Int 1))
                        2)
   5 (cfgl/->Assignment "b" (e/->Variable "a") 4)
   2 (cfgl/->Condition (e/->BinaryExpr :GREATER
                                       (e/->Variable "a")
                                       (e/->Int 0))
                       5
                       1)
   6 (cfgl/->Assignment "a" (e/->Int 2) 2)})

(def mock-cfg-2
  "a = 2;
   b = a;
   c = b;
   return a;"
  {4 (cfgl/->Assignment "a" (e/->Int 2) 3)
   3 (cfgl/->Assignment "b" (e/->Variable "a") 2)
   2 (cfgl/->Assignment "c" (e/->Variable "b") 1)
   1 (cfgl/->Return (e/->Variable "a"))})

(deftest compute-cfg-liveness-test
  (is (= {1 #{"a"}, 2 #{"a"}, 3 #{"a" "b"}
          4 #{"b"}, 5 #{"a"}, 6 #{}}
         (compute-cfg-liveness mock-cfg-1)))
  (is (= {1 #{"a"}, 2 #{"a" "b"}, 3 #{"a"}, 4 #{}}
         (compute-cfg-liveness mock-cfg-2))))
