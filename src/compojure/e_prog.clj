(ns compojure.e-prog
  (:require [compojure.e-lang :as e]))

(defn sub-ast-to-expr [sub-ast]
  (let [parent (first sub-ast)]
    (cond
      (= :SYM_IDENTIFIER parent) (e/->Identifier (second sub-ast))
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
                   (sub-ast-to-expr (second sub-ast))
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