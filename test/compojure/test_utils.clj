(ns compojure.test-utils
  (:require [clojure.test :refer [assert-expr
                                  do-report]]))

(defmethod assert-expr 'ex-info-thrown-with-data?
  ;; Taken from https://clojureverse.org/t/testing-thrown-ex-info-exceptions/6146/3
  [msg form]
  (let [data (second form)
        body (nthnext form 2)]
    `(try ~@body
          (do-report {:type :fail, :message ~msg
                      :expected '~form, :actual nil})
          (catch clojure.lang.ExceptionInfo e#
            (let [expected# ~data
                  actual# (ex-data e#)]
              (if (= expected# actual#)
                (do-report {:type :pass, :message ~msg
                            :expected expected#, :actual actual#})
                (do-report {:type :fail, :message ~msg
                            :expected expected#, :actual actual#})))
            e#))))