(ns compojure.cfg.cfg-exec
  (:require [compojure.e-lang.e-exec :refer [evaluate truthy?]]
            [compojure.exceptions :refer [return-encountered]])
  (:import [compojure.cfg.cfg_lang Assignment Condition Print Return Nop]))

(defprotocol CFGStatement
  (execute [this state]
    "Returns a pair [next-node-id updated-state]"))

(extend-protocol CFGStatement
  Assignment
  (execute [this state]
    [(:successor this) (assoc state
                              (:name (:var-ident this))
                              (evaluate (:expr this) state))])

  Condition
  (execute [this state]
    (if (truthy? (evaluate (:expr this) state))
      [(:succ-true this) state]
      [(:succ-false this) state]))

  Print
  (execute [this state]
    (println (evaluate (:expr this) state))
    [(:successor this) state])

  Return
  (execute [this state]
    (let [return-val (evaluate (:expr this) state)]
      (throw (return-encountered return-val state))))

  Nop
  (execute [this state]
    [(:successor this) state]))