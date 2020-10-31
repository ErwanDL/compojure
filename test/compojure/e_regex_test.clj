(ns compojure.e-regex-test
  (:require [clojure.test :refer [deftest testing is]]
            [compojure.e-regex :as e-re]))

(deftest identifier-re-test
  (is (re-matches e-re/identifier-re "myArray"))
  (is (re-matches e-re/identifier-re "my_array"))
  (is (re-matches e-re/identifier-re "HelloWorld"))
  (is (re-matches e-re/identifier-re "id2account"))
  (is (not (re-matches e-re/identifier-re "3D")))
  (is (not (re-matches e-re/identifier-re "hypen-separated")))
  (is (not (re-matches e-re/identifier-re " hasLeadingSpace")))
  (is (not (re-matches e-re/identifier-re "hasTrailingSpace ")))
  (is (not (re-matches e-re/identifier-re "space separated"))))

(deftest sline-comment-re-test
  (is (re-matches e-re/sline-comment-re "// this is a comment !"))
  (is (re-matches e-re/sline-comment-re "// this is a * weird ! / */ comment !'-é_."))
  (is (not (re-matches e-re/sline-comment-re "this is not a comment at all")))
  (is (not (re-matches e-re/sline-comment-re "// this spans more than one
                                                 line"))))

(deftest mline-coment-re-test
  (is (re-matches e-re/mline-comment-re "/* on one line */"))
  (is (re-matches e-re/mline-comment-re "/* on two
                                         lines */"))
  (is (not (re-matches e-re/mline-comment-re "int delta = 0;")))
  (is (not (re-matches e-re/mline-comment-re "/* not terminated
                                                anywhere")))
  (is (not (re-matches e-re/mline-comment-re "// /* nope */"))))

(deftest char-re-test
  (is (re-matches e-re/char-re "'c'"))
  (is (re-matches e-re/char-re "'8'"))
  (is (re-matches e-re/char-re "'$'"))
  (is (re-matches e-re/char-re "' '"))
  (is (re-matches e-re/char-re "'\n'"))
  (is (not (re-matches e-re/char-re "'ca'")))
  (is (not (re-matches e-re/char-re "'a")))
  (is (not (re-matches e-re/char-re "''")))
  (is (not (re-matches e-re/char-re "\"a\"")))
  (is (not (re-matches e-re/char-re "hey!"))))

(deftest string-re-test
  (is (re-matches e-re/string-re "\"hello it's me\""))
  (is (re-matches e-re/string-re "\"this is pretty \\\" meta\""))
  (is (not (re-matches e-re/string-re "\"non terminated")))
  (is (not (re-matches e-re/string-re "\"multiline strings
                                       are not accepted\"")))
  (is (not (re-matches e-re/string-re "you're not even trying"))))

(deftest int-re-test
  (is (re-matches e-re/int-re "0"))
  (is (re-matches e-re/int-re "9283"))
  (is (re-matches e-re/int-re "-65"))
  (is (not (re-matches e-re/int-re "")))
  (is (not (re-matches e-re/int-re "- 4")))
  (is (not (re-matches e-re/int-re "4.56"))))