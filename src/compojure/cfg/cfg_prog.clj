(ns compojure.cfg.cfg-prog
  (:import [compojure.e_lang.e_lang Assignment Print
            Return Block IfThenElse WhileLoop])
  (:require [compojure.cfg.cfg-lang :as cfgl]))



(defprotocol ConvertibleToCfgNode
  "Nodes are typically inserted in reverse order in the CFG
   (the last statements in a block are inserted at the first
   IDs), as it makes the whole process a bit simpler."
  (to-cfg-node [this cfg next-id successor]
    "Returns a triplet [updated-cfg inserted-node-id next-available-id]"))

(defn- opt-else-to-cfg-node [opt-else cfg next-id successor]
  (if (nil? opt-else)
    [cfg successor next-id]
    (to-cfg-node opt-else cfg next-id successor)))

(extend-protocol ConvertibleToCfgNode
  Assignment
  (to-cfg-node [this cfg next-id successor]
    [(assoc cfg
            next-id
            (cfgl/->Assignment
             (:var-name this)
             (:expr this)
             successor))
     next-id
     (inc next-id)])

  Print
  (to-cfg-node [this cfg next-id successor]
    [(assoc cfg
            next-id
            (cfgl/->Print
             (:expr this)
             successor))
     next-id
     (inc next-id)])

  Return
  (to-cfg-node [this cfg next-id successor]
    [(assoc cfg
            next-id
            (cfgl/->Return
             (:expr this)))
     next-id
     (inc next-id)])

  Block
  ; We start by inserting the last statement of the block,
  ; that points to "successor" ;
  ; then we insert the statement just before, that points
  ; to the last statement of the block ; etc.
  ; The inserted-node-id that is returned is that of the first
  ; statement of the block (the entry point of the block).
  (to-cfg-node [this cfg next-id successor]
    (reduce
     (fn [[g succ next] statement]
       (to-cfg-node statement g next succ))
     [cfg successor next-id]
     (reverse (:statements this))))

  IfThenElse
  ; We start by inserting the "then" branch, that points to
  ; "successor" ; we then insert the "else" branch, that also
  ; points to "successor" ; finally we insert the condition,
  ; which points to the start of the "then" branch and the 
  ; start of the "else" branch.
  ; The inserted-node-id that is returned is that of the condition.
  (to-cfg-node [this cfg next-id successor]
    (let [[cfg-w-then then-id id-after-then] (to-cfg-node
                                              (:then-statement this)
                                              cfg
                                              next-id
                                              successor)
          [cfg-w-else else-id id-after-else] (opt-else-to-cfg-node
                                              (:opt-else-statement this)
                                              cfg-w-then
                                              id-after-then
                                              successor)
          cmp-node (cfgl/->Condition (:condition this) then-id else-id)]
      [(assoc cfg-w-else id-after-else cmp-node) id-after-else (inc id-after-else)]))

  WhileLoop
  ; We start by inserting the body statement at next-id + 1,
  ; which points to next-id ; then we insert the condition 
  ; statement at next-id, that points to the body statement
  ; if true or successor if false.
  (to-cfg-node [this cfg next-id successor]
    (let [[cfg-w-body body-id id-after-body] (to-cfg-node
                                              (:statement this)
                                              cfg
                                              (inc next-id)
                                              next-id)
          cmp-node (cfgl/->Condition (:condition this) body-id successor)]
      [(assoc cfg-w-body next-id cmp-node) next-id id-after-body])))

(defn to-cfg-fn-def [e-fn-def]
  (let [[cfg start-id _] (to-cfg-node (:body e-fn-def) {} 1 0)]
    (cfgl/->FunctionDef (:name e-fn-def)
                        (:params e-fn-def)
                        cfg
                        start-id)))

(defn to-cfg-prog [e-prog]
  (cfgl/->Program
   (map to-cfg-fn-def (:functions e-prog))))