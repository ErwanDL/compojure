(ns compojure.snippets-framework-test
  "Testing the test utils... Seems pretty meta to me."
  (:require [clojure.test :refer [deftest is]]
            [compojure.snippets-framework :refer [parse-expect-file-args
                                                  submap?]]))

(deftest parse-expect-file-args-test
  (is (= [1 3]
         (parse-expect-file-args "/snippets/test.e.expect_1_3")))
  (is (= []
         (parse-expect-file-args "/snippets/test.e.expect")))
  (is (= []
         (parse-expect-file-args "/snippets/test.e.expect_"))))

(deftest submap?-test
  (is (submap? {:a 3, :b 4} {:a 3, :b 4, :c 3}))
  (is (not (submap? {:a 3, :b 4} {:a 3, :c 5})))
  (is (not (submap? {:a 3, :b 4} {:a 3, :b 5, :c 5})))
  (is (not (submap? {:a 3, :b 4} {:b 4}))))
