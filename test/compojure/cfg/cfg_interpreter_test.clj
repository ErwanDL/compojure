(ns compojure.cfg.cfg-interpreter-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.test-utils] ;; for ex-info-thrown-with-data?
            [compojure.cfg.cfg-interpreter :refer [interpret]]
            [compojure.snippets-framework :refer [snippets-and-expects-in-folder]]))

;; At the moment these tests are copy-pasted from one interpreter
;; to the other : writing a macro could help make the tests more DRY
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

