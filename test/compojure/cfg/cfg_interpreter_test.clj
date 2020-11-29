(ns compojure.cfg.cfg-interpreter-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.test-utils] ;; for ex-info-thrown-with-data?
            [compojure.cfg.cfg-interpreter :refer [interpret]]
            [compojure.snippets-framework :refer [snippets-and-expects-in-folder
                                                  submap?]]))

;; At the moment these tests are copy-pasted from one interpreter
;; to the other : writing a macro could help make the tests more DRY
(deftest interpret-snippets-test
  (dorun (map (fn [[fp args expected-res]]
                (is (submap? expected-res (interpret (slurp fp) args))
                    (str "Interpreter error on file " fp " with args " (seq args))))
              (snippets-and-expects-in-folder "basic"))))

(deftest dead-assignments-are-removed-test
  (is (= {"a" 3}
         (:final-state
          (interpret "main(){ b = 4; a = 3; c = a; return a; }" [])))))
