(ns compojure.interpreter-framework
  "This framework assumes the structure of Program and FunctionDef
   records is similar across different IRs (which holds true so far)."
  (:require [compojure.exceptions :refer [arity-exception]]
            [compojure.parser :refer [parse]]))


(defn get-main-fn
  [prog]
  (first
   (filter #(= (:name %) "main") (:functions prog))))

(defn validate-args-count [main-fn provided-args]
  (let [expected (count (:params main-fn))
        actual (count provided-args)]
    (when (> expected actual)
      (throw (arity-exception (:name main-fn)
                              expected actual)))))

(defn load-fn-args [fn args state]
  (into state
        (map #(vector %1 %2) (:params fn) args)))


(defn interpreter-maker
  "Takes as input a function that converts a parsed AST
   to some IR program, and a function that executes the 
   'main' of the IR program (assuming its arguments have 
   already been loaded into state).
   
   Returns an interpreter function, that takes as input
   a source code string and a coll of arguments, and returns
   the execution result as a map with keys 
   :output, :error, :retval, and :final-state if the program
   successfully returned."
  [converter main-fn-executer]
  (fn [source-code args]
    (let [e-prog (converter (parse source-code))
          main-fn (get-main-fn e-prog)
          initial-state (load-fn-args main-fn args {})]
      (validate-args-count main-fn args)
      (binding [*out* (java.io.StringWriter.)]
        (try
          (main-fn-executer main-fn initial-state)
          (catch clojure.lang.ExceptionInfo e
            (let [data (ex-data e)]
              (if (= (:type data)  :return-encountered)
                {:output (str *out*), :error nil
                 :retval (:retval data), :final-state (:final-state data)}
                {:output (str *out*), :error (ex-message e), :retval nil}))))))))
