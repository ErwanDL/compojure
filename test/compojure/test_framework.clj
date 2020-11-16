(ns compojure.test-framework
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn snippet-files-in [folder-name]
  (filter #(string/ends-with? (str %) ".e")
          (rest (file-seq (io/file
                           (io/resource
                            (str "e_snippets/" folder-name)))))))
