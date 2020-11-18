(ns compojure.core
  (:gen-class)
  (:require [compojure.e-lang.e-interpreter :as e-interpreter]
            [compojure.cfg.cfg-interpreter :as cfg-interpreter]
            [clojure.tools.cli :refer [parse-opts]]))

(def cli-options
  [["-s" "--string" (str "Directly interpret the string passed as "
                         "last argument, instead of considering it "
                         "as a filepath")]
   ["-r" "--representation REPR" (str "Define what intermediate "
                                      "representation to interpret "
                                      "(one of \"e\", \"cfg\"), "
                                      "defaults to \"e\"")
    :default "e"
    :validate [(fn [val] (some #{val} ["e" "cfg"]))
               "Representation must be one of \"e\", \"cfg\""]]
   ["-h" "--help" "Show command line help"]])

(defn -main [& args]
  (println
   (let [{errors :errors
          argmts :arguments
          opts :options
          summ :summary} (parse-opts args cli-options)]
     (if (:help opts)
       summ
       (if errors
         (first errors)
         (if (< (count argmts) 1)
           "Please provide a string or the path to a file to interpret"
           (let [script (first argmts)
                 src (if (:string opts)
                       script
                       (slurp script))
                 int-args (map #(Integer/parseInt %) (rest argmts))]
             (case (:representation opts)
               "e" (e-interpreter/interpret src int-args)
               "cfg" (cfg-interpreter/interpret src int-args)))))))))
