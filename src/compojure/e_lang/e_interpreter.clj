(ns compojure.e-lang.e-interpreter
  (:require [compojure.e-lang.e-exec :refer [execute]]
            [compojure.interpreter-framework :refer [interpreter-maker]]
            [compojure.e-lang.e-prog :refer [ast-to-e-program]]
            [compojure.exceptions :refer [no-return-exception]]))


(defn execute-fn [fn state]
  (execute (:body fn) state)
  (throw (no-return-exception fn)))

(def interpret (interpreter-maker ast-to-e-program execute-fn))