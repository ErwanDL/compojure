(ns compojure.cfg.cfg-lang)

;; At the moment, CFG expressions are just E expressions,
;; as defined in e-lang namespace.
;; However, CFG statements (nodes) differ from E statements.


(defrecord Assignment [var-ident expr successor])

(defrecord Condition [expr succ-true succ-false])

(defrecord Print [expr successor])

(defrecord Return [expr])

(defrecord Nop [successor])


(defrecord FunctionDef [name params cfg entry-node])

(defrecord Program [functions])