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

(defprotocol Expr
  (evaluate [this state]))

(defprotocol Statement)


(defrecord Int [value]
  Expr
  (evaluate [this state] value))

(defrecord Identifier [name]
  Expr
  (evaluate [this state]
    (let [value (get @state name)]
      (if (nil? value)
        (throw (java.lang.RuntimeException. (str "Variable '" name "' does not exist")))
        value))))

(defrecord BinaryExpr [binary-op expr-1 expr-2]
  Expr
  (evaluate [this state] ((get binary-ops binary-op)
                          (.evaluate expr-1 state)
                          (.evaluate expr-2 state))))

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