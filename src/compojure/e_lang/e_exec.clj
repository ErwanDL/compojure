(ns compojure.e-lang.e-exec
  (:require [compojure.e-lang.e-lang :refer [binary-ops]]
            [compojure.exceptions :refer [return-encountered
                                          unknown-ident-exception]])
  (:import [compojure.e_lang.e_lang Int Identifier BinaryExpr Assignment
            IfThenElse WhileLoop Block Print Return]))


(defprotocol EExpr
  (evaluate [this state]))

(extend-protocol EExpr
  Int
  (evaluate [this state] (:value this))

  Identifier
  (evaluate [this state]
    (let [value (get state (:name this))]
      (if (nil? value)
        (throw (unknown-ident-exception (:name this)))
        value)))

  BinaryExpr
  (evaluate [this state] ((get binary-ops (:binary-op this))
                          (evaluate (:expr-1 this) state)
                          (evaluate (:expr-2 this) state))))

(defn truthy? [val]
  (and (not= 0 val) val))

(defprotocol EStatement
  (execute [this state]))

(extend-protocol EStatement
  Assignment
  (execute
    [this state]
    (assoc state (:name (:var-ident this)) (evaluate (:expr this) state)))

  IfThenElse
  (execute [this state]
    (if (truthy? (evaluate (:condition this) state))
      (execute (:then-statement this) state)
      (if (nil? (:opt-else-statement this))
        state
        (execute (:opt-else-statement this) state))))

  WhileLoop
  (execute [this state]
    (loop [s state]
      (if (not (truthy? (evaluate (:condition this) s)))
        s
        (recur (execute (:statement this) s)))))

  Block
  (execute [this state]
    (reduce #(execute %2 %1) state (:statements this)))

  Return
  (execute [this state]
    (let [return-val (evaluate (:expr this) state)]
      (throw (return-encountered return-val state))))

  Print
  (execute [this state]
    (do
      (println (evaluate (:expr this) state))
      state)))