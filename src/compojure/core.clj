(ns compojure.core
  (:gen-class)
  (:require [compojure.parser :refer [parse]]
            [compojure.e-prog :refer [ast-to-program]]
            [compojure.e-interpreter :refer [eval-e-prog]]))


(defn interpret-e-program
  [source-code args]
  (-> source-code
      (parse)
      (ast-to-program)
      (eval-e-prog args)))

(defn -main
  [& args]
  (println
   (interpret-e-program (first args)
                        (map #(Integer/parseInt %) (rest args)))))

