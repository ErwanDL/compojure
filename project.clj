(defproject compojure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [instaparse "1.4.10"]
                 [rhizome "0.2.9"]
                 [cheshire "5.10.0"]]
  :main ^:skip-aot compojure.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
