(ns compojure.cfg.dead-assign-elim
  (:require [compojure.cfg.liveness :refer [compute-cfg-liveness]]
            [compojure.cfg.cfg-lang :as cfgl])
  (:import [compojure.cfg.cfg_lang Assignment]))

(defn dead? [assign-node cfg-liveness-map]
  (not (contains?
        (get cfg-liveness-map (:successor assign-node))
        (:var-name assign-node))))

(defn replace-node-if-dead-assignment [node cfg-liveness-map]
  (if (and (instance? Assignment node) (dead? node cfg-liveness-map))
    (cfgl/->Nop (:successor node))
    node))

(defn elim-dead-assign-iteration [cfg]
  (let [liveness-map (compute-cfg-liveness cfg)]
    (reduce (fn [new-cfg [id node]]
              (assoc new-cfg id (replace-node-if-dead-assignment
                                 node
                                 liveness-map)))
            {}
            cfg)))

(defn eliminate-dead-assignments [cfg]
  (loop [g cfg]
    (let [simpl-g (elim-dead-assign-iteration g)]
      (if (= simpl-g g)
        simpl-g
        (recur simpl-g)))))
