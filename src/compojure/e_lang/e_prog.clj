(ns compojure.e-lang.e-prog
  (:require [compojure.e-lang.e-lang :as e]))

(defn sub-ast-to-expr [sub-ast]
  (let [parent (first sub-ast)]
    (cond
      (= :SYM_IDENTIFIER parent) (e/->Variable (second sub-ast))
      (= :SYM_INTEGER parent) (e/->Int (Integer/parseInt (second sub-ast)))
      (contains? e/binary-ops parent) (e/->BinaryExpr
                                       (first sub-ast)
                                       (sub-ast-to-expr (second sub-ast))
                                       (sub-ast-to-expr (nth sub-ast 2)))
      :else (throw (java.lang.IllegalArgumentException. "Sub-AST is not a valid expression")))))

(defn sub-ast-to-statement [sub-ast]
  (let [parent (first sub-ast)]
    (case parent
      :ASSIGNMENT (e/->Assignment
                   (second (second sub-ast))
                   (sub-ast-to-expr (nth sub-ast 2)))
      :BLOCK (e/->Block (map sub-ast-to-statement (rest sub-ast)))
      :RETURN (e/->Return (sub-ast-to-expr (second sub-ast)))
      :PRINT (e/->Print (sub-ast-to-expr (second sub-ast)))
      :IF_ELSE (e/->IfThenElse (sub-ast-to-expr (second sub-ast))
                               (sub-ast-to-statement (nth sub-ast 2))
                               (sub-ast-to-statement (nth sub-ast 3)))
      :IF_NO_ELSE (e/->IfThenElse (sub-ast-to-expr (second sub-ast))
                                  (sub-ast-to-statement (nth sub-ast 2))
                                  nil)
      :WHILE_LOOP (e/->WhileLoop (sub-ast-to-expr (second sub-ast))
                                 (sub-ast-to-statement (nth sub-ast 2)))
      (throw (java.lang.IllegalArgumentException. "Sub-AST is not a valid statement")))))

(defn sub-ast-to-fundef [sub-ast]
  (if (= :FUNDEF (first sub-ast))
    (let [[_ [_ name] params body] sub-ast]
      (e/->FunctionDef name
                       (map second (rest params))
                       (sub-ast-to-statement body)))
    (throw (java.lang.IllegalArgumentException. "Sub-AST is not a valid function definition"))))

(defn ast-to-e-program [ast]
  (if (= :S (first ast))
    (e/->Program (map sub-ast-to-fundef (rest ast)))
    (throw (java.lang.IllegalArgumentException. "AST is not a valid program"))))
