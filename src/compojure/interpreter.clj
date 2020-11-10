(ns compojure.interpreter
  (:require [compojure.e-lang :refer [get-main-fn]]))

(defn declare-var
  "Returns the new state after declaring the variable"
  [state name value]
  (assoc state name value))

(defn validate-args-count [main-fn provided-args]
  (when (< (count provided-args) (count (.params main-fn)))
    (throw (java.lang.IllegalArgumentException.
            "Wrong number of arguments passed to main"))))

(defn init-function-args [state fun args]
  (into state
        (map #(vector (.name %1) %2) (.params fun) args)))

(defn eval-e-prog
  "Returns the state of the program at the end of its execution"
  [prog args]
  (let [main-fn (get-main-fn prog)
        initial-state {}]
    (validate-args-count main-fn args)
    (init-function-args initial-state main-fn args)))