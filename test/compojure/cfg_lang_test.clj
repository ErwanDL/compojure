(ns compojure.cfg-lang-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.cfg-lang :as cfg]
            [compojure.e-lang :as e]))

(deftest cfg-predecessors-test
  (is (= #{2 4}
; this CFG corresponds to the following code snippet :
; "a = 3; print(a); if (a) { a = 5; print(a); } return a;"
         (set (cfg/predecessors
               5
               [(cfg/->Assignment
                 (e/->Identifier "a")
                 (e/->Int 3)
                 1)
                (cfg/->Print
                 (e/->Identifier "a")
                 2)
                (cfg/->Comparison
                 (e/->Identifier "a")
                 3
                 5)
                (cfg/->Assignment
                 (e/->Identifier
                  "a")
                 (e/->Int 5)
                 4)
                (cfg/->Print
                 (e/->Identifier "a")
                 5)
                (cfg/->Return
                 (e/->Identifier "a"))])))))