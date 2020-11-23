(ns compojure.cfg.cfg-liveness
  (:require [compojure.cfg.cfg-lang]
            [compojure.e-lang.e-lang]
            [clojure.set :refer [difference]])
  (:import [compojure.cfg.cfg_lang Assignment Condition
            Print Return Nop]
           [compojure.e_lang.e_lang BinaryExpr Identifier Int]))

(defprotocol Node
  (successors [this])
  (vars-used-in-node [this])
  (vars-defined-in-node [this]))

(extend-protocol Node
  Assignment
  (successors [this] [(:successor this)])
  (vars-used-in-node [this]
    (vars-used-in-expr (:expr this)))
  (vars-defined-in-node [this]
    #{(:var-ident this)})

  Condition
  (successors [this] #{(:succ-true this) (:succ-false this)})
  (vars-used-in-node [this]
    (vars-used-in-expr (:expr this)))
  (vars-defined-in-node [this]
    #{})

  Print
  (successors [this] #{(:successor this)})
  (vars-used-in-node [this]
    (vars-used-in-expr (:expr this)))
  (vars-defined-in-node [this]
    #{})

  Return
  (successors [this] #{})
  (vars-used-in-node [this]
    (vars-used-in-expr (:expr this)))
  (vars-defined-in-node [this]
    #{})

  Nop
  (successors [this] #{(:successor this)})
  (vars-used-in-node [this] {})
  (vars-defined-in-node [this]
    #{}))


(defn predecessors [node-id all-nodes]
  (reduce-kv
   (fn [prev-res current-index current-node]
     (if (contains? (successors current-node) node-id)
       (conj prev-res current-index)
       prev-res))
   #{}
   all-nodes))


(defprotocol CfgExpr
  (vars-used-in-expr [this]))

(extend-protocol CfgExpr
  Int
  (vars-used-in-expr [this] #{})

  Identifier
  (vars-used-in-expr [this] #{(:name this)})

  BinaryExpr
  (vars-used-in-expr [this] (into
                             (vars-used-in-expr (:expr-1 this))
                             (vars-used-in-expr (:expr-2 this)))))

(defn live-vars-after-node
  "The live vars after a node are just the union of the
   live vars before each of its successors"
  [node live-vars-before-map]
  (reduce #(into %1 (get live-vars-before-map %2))
          #{}
          (successors node)))

(defn live-vars-before-node
  "The live vars before a node are (the variables used by
   the node) plus (the vars live after the node except the
   vars defined in the node)"
  [node live-vars-after]
  (into (vars-used-in-node node)
        (difference live-vars-after (vars-defined-in-node node))))
