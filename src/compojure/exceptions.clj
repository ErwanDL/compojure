(ns compojure.exceptions)

(defn arity-exception [fn-name expected-nb actual-nb]
  (ex-info (str "Wrong number of arguments passed to " fn-name
                "\nExpected " expected-nb " arguments, received " actual-nb)
           {:type :arity-ex
            :expected expected-nb
            :actual actual-nb}))

(defn return-encountered [return-val state]
  (ex-info "Return statement encountered"
           {:type :return-encountered
            :retval return-val
            :final-state state}))

(defn unknown-ident-exception [ident-name]
  (ex-info (str "Unknown identifier : " ident-name)
           {:type :unknown-ident-ex
            :ident-name ident-name}))

(defn no-return-exception [fn]
  (ex-info (str "Reached end of execution of function "
                (:name fn)
                " without encountering a return statement")
           {:type :no-return-ex
            :function fn}))