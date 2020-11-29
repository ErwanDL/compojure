(ns compojure.e-lang.e-interpreter-test
  (:require [clojure.test :refer [deftest is]]
            [compojure.test-utils] ;; for ex-info-thrown-with-data?
            [compojure.e-lang.e-interpreter :refer [interpret]]
            [compojure.snippets-framework :refer [snippets-and-expects-in-folder
                                                  submap?]]))


(deftest interpret-snippets-test
  (dorun (map (fn [[fp args expected-res]]
                (is (submap? expected-res (interpret (slurp fp) args))
                    (str "Interpreter error on file " fp " with args " (seq args))))
              (snippets-and-expects-in-folder "basic"))))

