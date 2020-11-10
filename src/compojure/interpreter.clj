(ns compojure.interpreter
  (:require [compojure.e-lang :refer [get-main-fn]]))

(defn validate-args-count [main-fn provided-args]
  (when (< (count provided-args) (count (.params main-fn)))
    (throw (java.lang.IllegalArgumentException.
            "Wrong number of arguments passed to main"))))

(defn enter-function [fun args state]
  (into state
        (map #(vector (.name %1) %2) (.params fun) args)))

(defn eval-e-prog
  "Returns the state of the program at the end of its execution"
  [prog args]
  (let [main-fn (get-main-fn prog)
        initial-state {}]
    (validate-args-count main-fn args)

    (try
      (.execute (.body main-fn)
                (enter-function main-fn args initial-state))
      "Error : program ended without returning a value"

      (catch clojure.lang.ExceptionInfo e
        (let [data (ex-data e)]
          (case (:type data)
            :return-statement (:return-value data)
            (str "Unkown error : " e)))))))