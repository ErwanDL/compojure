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

(defprotocol Statement
  (execute [this state]))


(defrecord Int [value]
  Expr
  (evaluate [this state] value))

(defrecord Identifier [name]
  Expr
  (evaluate [this state]
    (let [value (get state name)]
      (if (nil? value)
        (throw (ex-info (str "Variable '" name "' does not exist") {:type :error}))
        value))))

(defrecord BinaryExpr [binary-op expr-1 expr-2]
  Expr
  (evaluate [this state] ((get binary-ops binary-op)
                          (evaluate expr-1 state)
                          (evaluate expr-2 state))))

(defrecord Assignment [var-ident expr]
  Statement
  (execute
    [this state]
    (assoc state (:name var-ident) (evaluate expr state))))

(defn truthy? [val]
  (and (not= 0 val) val))

(defrecord IfThenElse [condition then-statement opt-else-statement]
  Statement
  (execute [this state]
    (if (truthy? (evaluate condition state))
      (execute then-statement state)
      (if (nil? opt-else-statement)
        state
        (execute opt-else-statement state)))))

(defrecord WhileLoop [condition statement]
  Statement
  (execute [this state]
    (loop [s state]
      (if (not (truthy? (evaluate condition s)))
        s
        (recur (execute statement s))))))

(defrecord Block [statements]
  Statement
  (execute [this state]
    (reduce #(execute %2 %1) state statements)))

(defrecord Return [expr]
  Statement
  (execute [this state]
    (let [return-val (evaluate expr state)]
      (throw (ex-info "Return statement encountered"
                      {:type :return-statement
                       :return-value return-val
                       :final-state state})))))

(defrecord Print [expr]
  Statement
  (execute [this state]
    (do
      (println (evaluate expr state))
      state)))

(defrecord FunctionDef [name params body])


(defrecord Program [functions])

(defn get-main-fn [prog]
  (first
   (filter #(= (:name %) (->Identifier "main")) (:functions prog))))