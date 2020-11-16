(ns compojure.e-lang)

(def binary-ops {:SUM +
                 :DIFFERENCE -
                 :MULTIPLICATION *
                 :REMAINDER mod
                 :DIVISION quot
                 :NOT_EQ not=
                 :EQ =
                 :GREATER >
                 :GREATER_EQ >=
                 :LESSER <
                 :LESSER_EQ <=})



(defrecord Int [value])

(defrecord Identifier [name])

(defrecord BinaryExpr [binary-op expr-1 expr-2])

(defrecord Assignment [var-ident expr])

(defrecord IfThenElse [condition then-statement opt-else-statement])

(defrecord WhileLoop [condition statement])

(defrecord Block [statements])

(defrecord Return [expr])

(defrecord Print [expr])

(defrecord FunctionDef [name params body])


(defrecord Program [functions])

(defn get-main-fn [prog]
  (first
   (filter #(= (:name %) (->Identifier "main")) (:functions prog))))