(ns compojure.e-lang.e-prog-test
  (:require [clojure.test :refer [deftest is testing]]
            [compojure.test-utils] ;; for ex-info-thrown-with-data?
            [compojure.e-lang.e-prog :refer
             [sub-ast-to-expr sub-ast-to-statement sub-ast-to-fundef ast-to-e-program]]
            [compojure.e-lang.e-lang :as e]))

(deftest sub-ast-to-expr-test
  (testing "With valid expr ASTs"
    (is (= (e/->Variable "a") (sub-ast-to-expr [:SYM_IDENTIFIER "a"])))
    (is (= (e/->Int 2) (sub-ast-to-expr [:SYM_INTEGER "2"])))
    (is (= (e/->BinaryExpr
            :SUM
            (e/->Int 3)
            (e/->Int 4))
           (sub-ast-to-expr
            [:SUM [:SYM_INTEGER "3"] [:SYM_INTEGER "4"]]))))
  (testing "With nested binary expressions"
    (is (= (e/->BinaryExpr
            :EQ
            (e/->BinaryExpr :SUM (e/->Int 5) (e/->Variable "a"))
            (e/->Int 4))
           (sub-ast-to-expr
            [:EQ [:SUM [:SYM_INTEGER "5"] [:SYM_IDENTIFIER "a"]] [:SYM_INTEGER "4"]]))))
  (testing "Throws with an AST that is not an expr"
    (is (thrown? java.lang.IllegalArgumentException
                 (sub-ast-to-expr
                  [:ASSIGNMENT [:SYM_IDENTIFIER "a"] [:SYM_INTEGER "4"]])))))

(deftest sub-ast-to-statement-test
  (testing "With valid statement ASTs"
    (is (= (e/->Assignment "a" (e/->Int 5))
           (sub-ast-to-statement
            [:ASSIGNMENT [:SYM_IDENTIFIER "a"] [:SYM_INTEGER "5"]])))
    (is (= (e/->Block [(e/->Assignment "a" (e/->Int 5))
                       (e/->Assignment "b" (e/->Variable "a"))])
           (sub-ast-to-statement [:BLOCK
                                  [:ASSIGNMENT [:SYM_IDENTIFIER "a"] [:SYM_INTEGER "5"]]
                                  [:ASSIGNMENT [:SYM_IDENTIFIER "b"] [:SYM_IDENTIFIER "a"]]])))
    (is (= (e/->Return (e/->Int 3))
           (sub-ast-to-statement [:RETURN [:SYM_INTEGER "3"]])))
    (is (= (e/->Print (e/->Variable "a"))
           (sub-ast-to-statement [:PRINT [:SYM_IDENTIFIER "a"]])))
    (is (= (e/->WhileLoop
            (e/->BinaryExpr :NOT_EQ (e/->Variable "a") (e/->Variable "b"))
            (e/->Assignment "a"
                            (e/->BinaryExpr :SUM (e/->Variable "a") (e/->Int 1))))
           (sub-ast-to-statement
            [:WHILE_LOOP
             [:NOT_EQ [:SYM_IDENTIFIER "a"] [:SYM_IDENTIFIER "b"]]
             [:ASSIGNMENT [:SYM_IDENTIFIER "a"] [:SUM [:SYM_IDENTIFIER "a"] [:SYM_INTEGER "1"]]]])))
    (testing "With various if/else statements"
      (is (= (e/->IfThenElse
              (e/->BinaryExpr :EQ (e/->Int 2) (e/->Int 2))
              (e/->Print (e/->Int 5))
              nil)
             (sub-ast-to-statement
              [:IF_NO_ELSE [:EQ [:SYM_INTEGER "2"] [:SYM_INTEGER "2"]] [:PRINT [:SYM_INTEGER "5"]]])))
      (is (= (e/->IfThenElse
              (e/->BinaryExpr :LESSER (e/->Variable "a") (e/->Int 5))
              (e/->Block [(e/->Print (e/->Variable "a"))])
              (e/->Print (e/->Int 0)))
             (sub-ast-to-statement
              [:IF_ELSE
               [:LESSER [:SYM_IDENTIFIER "a"] [:SYM_INTEGER "5"]]
               [:BLOCK [:PRINT [:SYM_IDENTIFIER "a"]]]
               [:PRINT [:SYM_INTEGER "0"]]])))))
  (testing "Throws with an AST that is not a statement"
    (is (thrown? java.lang.IllegalArgumentException
                 (sub-ast-to-statement [:SYM_INTEGER "2"])))))

(deftest sub-ast-to-fundef-test
  (testing "With valid function definition AST"
    (is (= (e/->FunctionDef
            "myFun"
            ["a" "b"]
            (e/->Block [(e/->Print (e/->Variable "a"))
                        (e/->Return (e/->Variable "b"))]))
           (sub-ast-to-fundef
            [:FUNDEF
             [:SYM_IDENTIFIER "myFun"]
             [:PARAMS [:SYM_IDENTIFIER "a"] [:SYM_IDENTIFIER "b"]]
             [:BLOCK [:PRINT [:SYM_IDENTIFIER "a"]] [:RETURN [:SYM_IDENTIFIER "b"]]]]))))
  (testing "Throws with an AST that is not a function definition"
    (is (thrown? java.lang.IllegalArgumentException
                 (sub-ast-to-fundef [:SUM [:SYM_INTEGER "4"] [:SYM_INTEGER "5"]])))))

(deftest ast-to-e-program-test
  (testing "With valid input program AST"
    (is (= (e/->Program
            [(e/->FunctionDef
              "main"
              []
              (e/->Block [(e/->Return (e/->Int 4))]))
             (e/->FunctionDef
              "identity"
              ["a"]
              (e/->Block [(e/->Return (e/->Variable "a"))]))])
           (ast-to-e-program
            [:S
             [:FUNDEF [:SYM_IDENTIFIER "main"] [:PARAMS]
              [:BLOCK [:RETURN [:SYM_INTEGER "4"]]]]
             [:FUNDEF [:SYM_IDENTIFIER "identity"] [:PARAMS [:SYM_IDENTIFIER "a"]]
              [:BLOCK [:RETURN [:SYM_IDENTIFIER "a"]]]]]))))
  (testing "Throws with invalid program AST"
    (is (thrown? java.lang.IllegalArgumentException
                 (ast-to-e-program
                  [:FUNDEF [:SYM_IDENTIFIER "main"] [:PARAMS]
                   [:BLOCK [:RETURN [:SYM_INTEGER "4"]]]])))))