(ns compojure.snippets-framework
  "These are helper functions for testing the parser
   and the various interpreters against the code snippets
   in resources/e_snippets/"
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [cheshire.core :refer [parse-string]]))


(defn files-in [folder-name]
  (rest (file-seq (io/file
                   (io/resource
                    (str "e_snippets/" folder-name))))))

(defn snippet-files-in [folder-name]
  (filter #(string/ends-with? (.getName %) ".e") (files-in folder-name)))

(defn expect-files-in [folder-name]
  (filter #(string/includes? (.getName %) ".e.expect") (files-in folder-name)))

(defn make-snippet-file-expect-file-pairs [folder-name]
  (for [expect-file (expect-files-in folder-name)]
    [(first (string/split (str expect-file) #".expect"))
     (str expect-file)]))


(defn parse-expect-file-args [filepath]
  (let [split-name (string/split filepath #".e.expect_")]
    (if (= 1 (count split-name))
      []
      (for [x (string/split (second split-name) #"_")]
        (Integer/parseInt x)))))

(defn parse-expect-file [filepath]
  (let [args (parse-expect-file-args filepath)
        expected-res (parse-string (slurp filepath) true)]
    [args expected-res]))

(defn snippets-and-expects-in-folder [folder-name]
  (for [[snippet-fp expect-fp] (make-snippet-file-expect-file-pairs folder-name)]
    (let [[args expected-res] (parse-expect-file expect-fp)]
      [snippet-fp args expected-res])))