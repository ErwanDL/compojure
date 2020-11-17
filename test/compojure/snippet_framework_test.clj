(ns compojure.snippets-framework-test
  "Testing the test utils... Seems pretty meta to me."
  (:require [clojure.test :refer [deftest is]]
            [clojure.java.io :as io]
            [compojure.snippets-framework :refer [parse-expect-file-args
                                                  add-to-snippet-files-map
                                                  file-names-map-from]]))

(deftest parse-expect-file-args-test
  (is (= ["1" "3"]
         (parse-expect-file-args "/snippets/test.e.expect_1_3")))
  (is (= []
         (parse-expect-file-args "/snippets/test.e.expect")))
  (is (= []
         (parse-expect-file-args "/snippets/test.e.expect_"))))

(deftest add-to-snippet-files-map-test
  (is (= {"/snippets/test.e" ["/snippets/test.e.expect"]
          "/snippets/other.e" []}
         (add-to-snippet-files-map
          {"/snippets/test.e" []
           "/snippets/other.e" []}
          "/snippets/test.e.expect"))))

(deftest file-names-map-from-test
  (is (= {"/snippets/test.e" []
          "/snippets/other.e" []}
         (file-names-map-from
          [(io/file "/snippets/test.e")
           (io/file "/snippets/other.e")]))))
