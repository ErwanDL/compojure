(ns compojure.snippets-framework
  "These are helper functions for testing the parser
   and the various interpreters against the code snippets
   in resources/e_snippets/"
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.test :refer [is]]
            [cheshire.core :refer [parse-string]]))


(defn files-in [folder-name]
  (rest (file-seq (io/file
                   (io/resource
                    (str "e_snippets/" folder-name))))))

(defn snippet-files-in [folder-name]
  (filter #(string/ends-with? (.getName %) ".e") (files-in folder-name)))

(defn expect-files-in [folder-name]
  (filter #(string/includes? (.getName %) ".e.expect") (files-in folder-name)))


(defn file-names-map-from [files]
  (reduce #(assoc %1 (str %2) []) {} files))

(defn add-to-snippet-files-map [snippet-files-map expect-filepaths]
  (let [matching-file (first (string/split expect-filepaths #".expect"))]
    (update snippet-files-map matching-file conj expect-filepaths)))

(defn match-expect-files-with-snippet-files [folder-name]
  (let [snippet-files-map (file-names-map-from (snippet-files-in folder-name))
        expect-filepaths (map str
                              (expect-files-in folder-name))]
    (reduce add-to-snippet-files-map snippet-files-map expect-filepaths)))


(defn parse-expect-file-args [filepath]
  (let [split-name (string/split filepath #".e.expect_")]
    (if (= 1 (count split-name))
      []
      (for [x (string/split (second split-name) #"_")]
        (Integer/parseInt x)))))

(defn parse-expect-file [filepath]
  (let [args (parse-expect-file-args filepath)
        content (parse-string (slurp filepath) true)]
    [args content]))

(defn parse-expect-files-in-snippet-map [snip-fp-to-exp-fp-map]
  (reduce (fn [m [snip exp-files]]
            (assoc m snip (map parse-expect-file exp-files)))
          {}
          snip-fp-to-exp-fp-map))

(defn correct-result? [interpreter-fn snippet args expected-res]
  (let [res (interpreter-fn snippet args)]
    (= expected-res res)))

(defn test-interpreter-on-snippet [interpreter-fn snippet parsed-expects]
  (dorun (map #(is (correct-result?
                    interpreter-fn
                    snippet
                    (first %)
                    (second %)))
              parsed-expects)))

(defn test-interpreter-on-folder [interpreter-fn folder-name]
  (let [test-map (parse-expect-files-in-snippet-map
                  (match-expect-files-with-snippet-files folder-name))]
    (dorun (map #(test-interpreter-on-snippet interpreter-fn
                                              (slurp (first %))
                                              (second %))
                test-map))))

