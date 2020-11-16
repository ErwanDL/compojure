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

(def invalid-snippet-names
  #{"lexerror.e"
    "syntaxerror1.e"
    "syntaxerror2.e"})

(def group-invalid-valid-basic-files
  (group-by (fn [file]
              (some #(= % (.getName file)) invalid-snippet-names))
            (snippet-files-in "basic")))
(def valid-basic-files
  (get group-invalid-valid-basic-files nil))

(def invalid-basic-files
  (get group-invalid-valid-basic-files true))


(deftest parser-basic-test
  (testing "On syntactically valid programs"
    (dorun (map #(is (successful-unambiguous-parse? %)) valid-basic-files)))
  (testing "On syntactically invalid programs"
    (dorun (map #(is (not (successful-unambiguous-parse? %))) invalid-basic-files))))

(deftest parser-if-else-test
  (dorun (map #(is (successful-unambiguous-parse? %)) (snippet-files-in "if_else")))
  (testing "Dangling else is correctly paired with closest if"
    (is (= [:S
            [:FUNDEF
             [:SYM_IDENTIFIER "main"]
             [:PARAMS]
             [:BLOCK
              [:IF_NO_ELSE
               [:SYM_INTEGER "5"]
               [:IF_ELSE [:SYM_INTEGER "4"] [:PRINT [:SYM_INTEGER "4"]] [:PRINT [:SYM_INTEGER "5"]]]]]]]
           (parse "main() { if (5) if (4) print(4); else print(5); }")))))