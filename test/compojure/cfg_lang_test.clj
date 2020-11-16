(ns compojure.cfg-lang-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.cfg-lang :as cfgl]
            [compojure.e-lang :as e]))

(deftest cfg-predecessors-test
  (is (= #{2 4}
; this CFG corresponds to the following code snippet :
; "a = 3; print(a); if (a) { a = 5; print(a); } return a;"
         (set (cfgl/predecessors
               5
               [(cfgl/->Assignment
                 (e/->Identifier "a")
                 (e/->Int 3)
                 1)
                (cfgl/->Print
                 (e/->Identifier "a")
                 2)
                (cfgl/->Comparison
                 (e/->Identifier "a")
                 3
                 5)
                (cfgl/->Assignment
                 (e/->Identifier
                  "a")
                 (e/->Int 5)
                 4)
                (cfgl/->Print
                 (e/->Identifier "a")
                 5)
                (cfgl/->Return
                 (e/->Identifier "a"))])))))