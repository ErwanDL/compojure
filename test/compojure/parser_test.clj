(ns compojure.parser-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojure.java.io :as io]
            [compojure.parser :refer [success? parse]]))


(defn successful-parse? [snippet-name]
  (success? (parse
             (slurp (io/resource
                     (str "e_snippets/" snippet-name))))))

(deftest e-parser-test
  (testing "On syntactically invalid programs"
    (is (not (successful-parse? "basic/lexerror.e")))
    (is (not (successful-parse? "basic/syntaxerror1.e")))
    (is (not (successful-parse? "basic/syntaxerror2.e"))))
  (testing "On syntactically correct programs"
    (is (successful-parse? "basic/ret0.e"))
    (is (successful-parse? "basic/uninitialized.e"))
    (is (successful-parse? "basic/just_a_variable_37.e"))
    (is (successful-parse? "basic/2xpy.e"))
    (is (successful-parse? "basic/x-is-what.e"))
    (is (successful-parse? "basic/ret60.e"))
    (is (successful-parse? "basic/ret20.e"))
    (is (successful-parse? "basic/print_int.e"))
    (is (successful-parse? "basic/useless-assigns.e"))
    (is (successful-parse? "basic/loop.e"))
    (is (successful-parse? "basic/loop2.e"))
    (is (successful-parse? "basic/loop3.e"))
    (is (successful-parse? "basic/loop4.e"))
    (is (successful-parse? "basic/prime.e"))
    (is (successful-parse? "basic/sum_ints_upto_10.e"))
    (is (successful-parse? "basic/sum_ints_upto_n.e"))
    (is (successful-parse? "basic/mulmulmul.e"))
    (is (successful-parse? "basic/fibbetter.e"))
    (is (successful-parse? "basic/35gt12.e"))
    (is (successful-parse? "basic/arg-test.e"))
    (is (successful-parse? "basic/a_19.e"))
    (is (successful-parse? "basic/lots-of-regs.e"))
    (is (successful-parse? "basic/range_0_10.e"))
    (is (successful-parse? "basic/range_10_0_then_dec0.e"))
    (is (successful-parse? "basic/gcd.e"))
    (is (successful-parse? "basic/a_simple_variable_23.e"))
    (is (successful-parse? "basic/ret21.e"))
    (is (successful-parse? "basic/dec_to_0.e"))
    (is (successful-parse? "basic/comment.e"))
    (is (successful-parse? "basic/comment++.e"))
    (is (successful-parse? "basic/muldivmod.e"))
    (is (successful-parse? "basic/1_or_3a.e"))
    (is (successful-parse? "basic/double.e"))
    (is (successful-parse? "basic/1_or_4.e"))
    (is (successful-parse? "basic/fac.e"))))