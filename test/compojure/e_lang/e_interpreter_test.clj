(ns compojure.e-lang.e-interpreter-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.test-utils] ;; for ex-info-thrown-with-data?
            [compojure.e-lang.e-interpreter :refer [interpret]]
            [compojure.snippets-framework :refer [snippets-and-expects-in-folder]]))

(deftest interpret-minimal-test
  (is (= {:output "", :error nil, :retval -2}
         (interpret "main(a, b) { return a + b; }" [3 -5])))
  (is (= {:output "", :error "Unknown identifier : b", :retval nil}
         (interpret "main(a) { return b; }" [1]))))

(deftest interpret-snippets-test
  (dorun (map (fn [[fp args expected-res]]
                (is (= expected-res (interpret (slurp fp) args))
                    (str "Interpreter error on file " fp " with args " (seq args))))
              (snippets-and-expects-in-folder "basic"))))

