(ns compojure.grammar-regex-test
  (:require [clojure.test :refer [deftest is]]
            [clojure.java.io :as io]
            [clojure.string :as string]))

(defn extract-regexes-from-grammar [grammar-bnf]
  (let [lines (string/split grammar-bnf #"\n")]
    (->> (filter #(re-matches #"SYM_[A-Z_]+\s+=.*" %) lines)
         (map #(string/split % #" = "))
         (map (fn [[symbol-name re-as-str]]
                [symbol-name (load-string re-as-str)]))
         (into {}))))

(def e-regexes
  (merge
   (extract-regexes-from-grammar
    (slurp (io/resource "grammar.bnf")))
   (extract-regexes-from-grammar
    (slurp (io/resource "whitespace_comments.bnf")))))

(def identifier-re (get e-regexes "SYM_IDENTIFIER"))
(deftest identifier-re-test
  (is (re-matches identifier-re "myArray"))
  (is (re-matches identifier-re "my_array"))
  (is (re-matches identifier-re "HelloWorld"))
  (is (re-matches identifier-re "id2account"))
  (is (re-matches identifier-re "_private"))
  (is (not (re-matches identifier-re "3D")))
  (is (not (re-matches identifier-re "hypen-separated")))
  (is (not (re-matches identifier-re " hasLeadingSpace")))
  (is (not (re-matches identifier-re "hasTrailingSpace ")))
  (is (not (re-matches identifier-re "space separated"))))

(def sline-comment-re (get e-regexes "SYM_SLINE_COMMENT"))
(deftest sline-comment-re-test
  (is (re-matches sline-comment-re "// this is a comment !"))
  (is (re-matches sline-comment-re "// this is a * weird ! / */ comment !'-é_."))
  (is (not (re-matches sline-comment-re "this is not a comment at all")))
  (is (not (re-matches sline-comment-re "// this spans more than one
                                                 line"))))
(def mline-comment-re (get e-regexes "SYM_MLINE_COMMENT"))
(deftest mline-coment-re-test
  (is (re-matches mline-comment-re "/* on one line */"))
  (is (re-matches mline-comment-re "/* on two
                                         lines */"))
  (is (not (re-matches mline-comment-re "int delta = 0;")))
  (is (not (re-matches mline-comment-re "/* not terminated
                                                anywhere")))
  (is (not (re-matches mline-comment-re "// /* nope */"))))

(def char-re (get e-regexes "SYM_CHAR"))
(deftest char-re-test
  (is (re-matches char-re "'c'"))
  (is (re-matches char-re "'8'"))
  (is (re-matches char-re "'$'"))
  (is (re-matches char-re "' '"))
  (is (re-matches char-re "'\n'"))
  (is (not (re-matches char-re "'ca'")))
  (is (not (re-matches char-re "'a")))
  (is (not (re-matches char-re "''")))
  (is (not (re-matches char-re "\"a\"")))
  (is (not (re-matches char-re "hey!"))))

(def string-re (get e-regexes "SYM_STRING"))
(deftest string-re-test
  (is (re-matches string-re "\"hello it's me\""))
  (is (re-matches string-re "\"this is pretty \\\" meta\""))
  (is (not (re-matches string-re "\"non terminated")))
  (is (not (re-matches string-re "\"multiline strings
                                       are not accepted\"")))
  (is (not (re-matches string-re "you're not even trying"))))

(def int-re (get e-regexes "SYM_INTEGER"))
(deftest int-re-test
  (is (re-matches int-re "0"))
  (is (re-matches int-re "9283"))
  (is (re-matches int-re "-65"))
  (is (not (re-matches int-re "")))
  (is (not (re-matches int-re "- 4")))
  (is (not (re-matches int-re "4.56"))))