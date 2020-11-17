(ns compojure.e-lang.e-interpreter-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.e-lang.e-lang :as e]
            [compojure.test-utils] ;; for thrown-ex-info-with-data?
            [compojure.e-lang.e-interpreter :refer [load-fn-args
                                                    validate-args-count
                                                    interpret
                                                    get-main-fn]]
            [compojure.snippets-framework :refer [snippets-and-expects-in-folder]]))


(def example-main-fn (e/->FunctionDef
                      (e/->Identifier "main")
                      [(e/->Identifier "a") (e/->Identifier "b")]
                      (e/->Block [(e/->Assignment
                                   (e/->Identifier "b")
                                   (e/->BinaryExpr :SUM (e/->Identifier "a") (e/->Identifier "b")))
                                  (e/->Return (e/->Identifier "b"))])))

(deftest validate-args-count-test
  (is (nil? (validate-args-count example-main-fn [1 2])))
  (is (nil? (validate-args-count example-main-fn [1 2 4 5])))
  (is (thrown-ex-info-with-data? {:type :arity-ex
                                  :expected 2
                                  :actual 1}
                                 (validate-args-count example-main-fn [1]))))

(deftest load-fn-args-test
  (is (= {"a" 2 "b" 6}
         (->> {}
              (load-fn-args example-main-fn [2 6])))))

(def other-fn (e/->FunctionDef
               (e/->Identifier "other")
               [(e/->Identifier "n")]
               (e/->Block [])))

(deftest get-main-fn-test
  (is (= example-main-fn
         (get-main-fn (e/->Program [other-fn example-main-fn])))))


(deftest interpret-minimal-test
  (is (= {:output "", :error nil, :retval -2}
         (interpret "main(a, b) { return a + b; }" [3 -5])))
  (is (= {:output "", :error "Unknown identifier : b", :retval nil}
         (interpret "main(a) { return b; }" [1]))))

(deftest interpret-snippets-test
  (dorun (map (fn [[fp args expected-res]]
                (is (= expected-res (interpret (slurp fp) args))
                    (str "Interpreter error on file " fp " with args " (seq args))))
              (snippets-and-expects-in-folder "basic"))))

