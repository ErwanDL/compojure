(ns compojure.parser
  (:require [instaparse.core :as insta]
            [clojure.java.io :as io]))

(def whitespace-and-comments-parser
  (insta/parser
   (io/resource "whitespace_comments.bnf")))

(def e-parser
  (insta/parser
   (io/resource "grammar.bnf")
   :auto-whitespace whitespace-and-comments-parser))

(defn parse [source-code]
  (insta/parse e-parser source-code))

(defn success? [parse-result]
  (not (insta/failure? parse-result)))