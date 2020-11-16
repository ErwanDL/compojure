(ns compojure.e-interpreter
  (:require [compojure.e-lang :refer [get-main-fn binary-ops]])
  (:import [compojure.e_lang Int Identifier BinaryExpr Assignment
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
        (throw (ex-info (str "Variable '" (:name this) "' does not exist") {:type :error}))
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
      (throw (ex-info "Return statement encountered"
                      {:type :return-statement
                       :return-value return-val
                       :final-state state}))))

  Print
  (execute [this state]
    (do
      (println (evaluate (:expr this) state))
      state)))

(defn validate-args-count [main-fn provided-args]
  (when (< (count provided-args) (count (:params main-fn)))
    (throw (java.lang.IllegalArgumentException.
            "Wrong number of arguments passed to main"))))

(defn enter-function [fun args state]
  (into state
        (map #(vector (:name %1) %2) (:params fun) args)))

(defn eval-e-prog
  "Returns the state of the program at the end of its execution"
  [prog args]
  (let [main-fn (get-main-fn prog)
        initial-state {}]
    (validate-args-count main-fn args)

    (try
      (execute (:body main-fn)
               (enter-function main-fn args initial-state))
      "Error : program ended without returning a value"

      (catch clojure.lang.ExceptionInfo e
        (let [data (ex-data e)]
          (case (:type data)
            :return-statement (:return-value data)
            (str "Unkown error : " e)))))))