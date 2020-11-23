(ns compojure.cfg.cfg-exec-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.cfg.cfg-lang :as cfgl]
            [compojure.test-utils]
            [compojure.e-lang.e-lang :as e]
            [compojure.cfg.cfg-exec :refer [execute]]))

(deftest execute-assignment-test
  (is (= [1 {"a" 5}]
         (execute (cfgl/->Assignment "a"
                                     (e/->BinaryExpr :SUM
                                                     (e/->Variable "a")
                                                     (e/->Int 2))
                                     1)
                  {"a" 3}))))

(deftest execute-condition-test
  (is (= [1 {"a" 3}]
         (execute (cfgl/->Condition (e/->Int 1)
                                    1
                                    3)
                  {"a" 3})))
  (is (= [3 {"a" 3}]
         (execute (cfgl/->Condition (e/->Int 0)
                                    1
                                    3)
                  {"a" 3}))))

(deftest execute-print-test
  (binding [*out* (java.io.StringWriter.)]
    (is (= [1 {}]
           (execute (cfgl/->Print (e/->Int 5) 1) {})))
    (is (= "5\n" (str *out*)))))

(deftest execute-return-test
  (is (ex-info-thrown-with-data?
       {:type :return-encountered
        :retval 0
        :final-state {"a" 0}}
       (execute (cfgl/->Return (e/->Variable "a")) {"a" 0}))))

(deftest execute-nop-test
  (is (= [1 {"a" 5}]
         (execute (cfgl/->Nop 1) {"a" 5}))))