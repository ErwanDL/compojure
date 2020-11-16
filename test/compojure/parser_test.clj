(ns compojure.parser-test
  (:require [clojure.test :refer [deftest is testing]]
            [compojure.parser :refer [e-parser parse]]
            [instaparse.core :as insta]
            [compojure.test-framework :refer [snippet-files-in]]))


(defn successful-unambiguous-parse? [snippet-file]
  ;; If insta/parses produces 0 parse tree,
  ;; it means it failed to parse the input.
  ;; If insta/parses produces strictly more than 1 parse tree,
  ;; it means that the grammar is ambiguous.
  (= 1 (count (insta/parses e-parser
                            (slurp snippet-file)))))



(deftest successful-parse-test
  (testing "On basic syntactically valid programs"
    (dorun (map #(is (successful-unambiguous-parse? %)) (snippet-files-in "basic"))))
  (testing "On syntactically invalid programs"
    (dorun (map #(is (not (successful-unambiguous-parse? %))) (snippet-files-in "invalid_syntax"))))
  (testing "On programs with tricky or nested if/else"
    (dorun (map #(is (successful-unambiguous-parse? %)) (snippet-files-in "if_else")))))

(deftest dangling-else-test
  (is (= [:S
          [:FUNDEF
           [:SYM_IDENTIFIER "main"]
           [:PARAMS]
           [:BLOCK
            [:IF_NO_ELSE
             [:SYM_INTEGER "5"]
             [:IF_ELSE [:SYM_INTEGER "4"] [:PRINT [:SYM_INTEGER "4"]] [:PRINT [:SYM_INTEGER "5"]]]]]]]
         (parse "main() { if (5) if (4) print(4); else print(5); }"))))