(ns compojure.cfg.cfg-interpreter
  (:require [compojure.cfg.cfg-exec :refer [execute]]
            [compojure.interpreter-framework :refer [interpreter-maker]]
            [compojure.e-lang.e-prog :refer [ast-to-e-program]]
            [compojure.cfg.cfg-prog :refer [to-cfg-prog]]
            [compojure.cfg.dead-assign-elim :refer [eliminate-program-dead-assignments]]
            [compojure.exceptions :refer [no-return-exception]]))

(defn execute-cfg-fn [fn state]
  (let [cfg (:cfg fn)]
    (loop [node-id (:entry-node fn)
           s state]
      (let [[next-node new-s] (execute (get cfg node-id) s)]
        (recur next-node new-s)))
    (throw (no-return-exception fn))))

(def interpret (interpreter-maker (comp eliminate-program-dead-assignments
                                        to-cfg-prog
                                        ast-to-e-program)
                                  execute-cfg-fn))