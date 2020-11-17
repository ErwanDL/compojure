(ns compojure.e-lang.e-interpreter
  (:require [compojure.e-lang.e-exec :refer [execute]]
            [compojure.parser :refer [parse]]
            [compojure.e-lang.e-prog :refer [ast-to-program]]
            [compojure.exceptions :refer [arity-exception
                                          no-return-exception]]))


(defn get-main-fn [prog]
  (first
   (filter #(= (:name (:ident %)) "main") (:functions prog))))

(defn validate-args-count [main-fn provided-args]
  (let [expected (count (:params main-fn))
        actual (count provided-args)]
    (when (> expected actual)
      (throw (arity-exception (:name (:ident main-fn))
                              expected actual)))))

(defn load-fn-args [fn args state]
  (into state
        (map #(vector (:name %1) %2) (:params fn) args)))

(defn execute-fn [fn args state]
  (execute (:body fn) (load-fn-args fn args state))
  (throw (no-return-exception fn)))

(defn interpret
  "Returns a map {:output ..., :error ..., :retval ...}"
  [source-code args]
  (let [e-prog (ast-to-program (parse source-code))
        main-fn (get-main-fn e-prog) initial-state {}]
    (validate-args-count main-fn args)
    (binding [*out* (java.io.StringWriter.)]
      (try
        (execute-fn main-fn args initial-state)
        (catch clojure.lang.ExceptionInfo e
          (let [data (ex-data e)]
            (if (= (:type data)  :return-encountered)
              {:output (str *out*), :error nil, :retval (:retval data)}
              {:output (str *out*), :error (ex-message e), :retval nil})))))))