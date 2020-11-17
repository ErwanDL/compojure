(ns compojure.interpreter-framework-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.interpreter-framework :refer [load-fn-args
                                                     get-main-fn
                                                     validate-args-count
                                                     load-fn-args]]))



(def mock-main-fn {:name "main", :params ["a" "b"]})

(deftest validate-args-count-test
  (is (nil? (validate-args-count mock-main-fn [1 2])))
  (is (nil? (validate-args-count mock-main-fn [1 2 4 5])))
  (is (ex-info-thrown-with-data? {:type :arity-ex
                                  :expected 2
                                  :actual 1}
                                 (validate-args-count mock-main-fn [1]))))

(deftest load-fn-args-test
  (is (= {"a" 2 "b" 6}
         (->> {}
              (load-fn-args mock-main-fn [2 6])))))

(def other-mock-fn {:name "other", :params ["n"]})

(deftest get-main-fn-test
  (is (= mock-main-fn
         (get-main-fn {:functions [other-mock-fn mock-main-fn]}))))

