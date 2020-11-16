(ns compojure.cfg-lang)

;; At the moment, CFG expressions are just E expressions,
;; as defined in e-lang namespace.
;; However, CFG nodes (nodes) differ from E nodes.

(defprotocol Node
  (successors [this]))

(defrecord Assignment [var-ident expr successor]
  Node
  (successors [this] [successor]))

(defrecord Comparison [expr succ-true succ-false]
  Node
  (successors [this] [succ-true succ-false]))

(defrecord Print [expr successor]
  Node
  (successors [this] [successor]))

(defrecord Return [expr]
  Node
  (successors [this] []))

(defrecord Nop [successor]
  Node
  (successors [this] [successor]))

(defn predecessors [node-id all-nodes]
  (reduce-kv
   (fn [prev-res current-index current-node]
     (if (some #(= node-id %) (.successors current-node))
       (conj prev-res current-index)
       prev-res))
   []
   all-nodes))

(defrecord FunctionDef [name params body-nodes entry-node])
