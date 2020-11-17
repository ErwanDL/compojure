(ns compojure.core
  (:gen-class)
  (:require [compojure.e-lang.e-interpreter :refer [interpret]]))



(defn -main
  [& args]
  (println
   (interpret (first args)
              (map #(Integer/parseInt %) (rest args)))))

