(ns compojure.e-lang)

(def binary-ops #{:SUM :DIFFERENCE :MULTIPLICATION
                  :DIVISION :REMAINDER :NOT_EQ
                  :EQ :GREATER :GREATER_EQ
                  :LESSER :LESSER_EQ})

(defprotocol Expr)

(defprotocol Statement)


(defrecord Int [value]
  Expr)

(defrecord Identifier [name]
  Expr)

(defrecord BinaryExpr [binary-op expr-1 expr-2]
  Expr)

(defrecord Assignment [var-name expr]
  Statement)

(defrecord IfThenElse [condition then-statement else-statement]
  Statement)

(defrecord WhileLoop [condition statement]
  Statement)

(defrecord Block [statements]
  Statement)

(defrecord Return [expr]
  Statement)

(defrecord Print [expr]
  Statement)

(defrecord FunctionDef [name params body])

(defrecord Program [functions])