(ns compojure.e-lang-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.e-lang :as e :refer [get-main-fn]]))


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
         (get-main-fn (e/->Program [test-other-fn test-main-fn])))))

