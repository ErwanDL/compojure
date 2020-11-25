(ns compojure.cfg.dead-assign-elim-test
  (:require [clojure.test :refer [deftest is testing]]
            [compojure.cfg.cfg-lang :as cfgl]
            [compojure.e-lang.e-lang :as e]
            [compojure.cfg.dead-assign-elim :refer [dead?
                                                    replace-node-if-dead-assignment
                                                    elim-dead-assign-iteration
                                                    eliminate-dead-assignments]]))

(deftest dead?-test
  (is (not (dead? (cfgl/->Assignment "a" (e/->Int 3) 1)
                  {0 #{}, 1 #{"a"}})))
  (is (dead? (cfgl/->Assignment "a" (e/->Int 3) 1)
             {0 #{}, 1 #{"b"}})))

(deftest replace-node-if-dead-assignment-test
  (is (= (cfgl/->Print (e/->Variable "a") 1)
         (replace-node-if-dead-assignment
          (cfgl/->Print (e/->Variable "a") 1)
          {2 #{"a"}, 1 #{}})))
  (is (= (cfgl/->Assignment "a" (e/->Int 2) 1)
         (replace-node-if-dead-assignment
          (cfgl/->Assignment "a" (e/->Int 2) 1)
          {2 #{}, 1 #{"a" "b"}})))
  (is (= (cfgl/->Nop 1)
         (replace-node-if-dead-assignment
          (cfgl/->Assignment "a" (e/->Int 2) 1)
          {2 #{}, 1 #{"b"}}))))

(def dependent-dead-assigns
  {:before-elim {4 (cfgl/->Assignment "a" (e/->Int 2) 3)
                 3 (cfgl/->Assignment "b" (e/->Variable "a") 2)
                 2 (cfgl/->Assignment "c" (e/->Variable "b") 1)
                 1 (cfgl/->Return (e/->Variable "a"))}
   :after-elim {4 (cfgl/->Assignment "a" (e/->Int 2) 3)
                3 (cfgl/->Nop 2)
                2 (cfgl/->Nop 1)
                1 (cfgl/->Return (e/->Variable "a"))}})


(deftest elim-dead-assign-iteration-test
  (testing "Eliminates a single dead assignment"
    (is (= {3 (cfgl/->Assignment "a" (e/->Int 2) 2)
            2 (cfgl/->Nop 1)
            1 (cfgl/->Return (e/->Variable "a"))}
           (elim-dead-assign-iteration
            {3 (cfgl/->Assignment "a" (e/->Int 2) 2)
             2 (cfgl/->Assignment "b" (e/->Variable "a") 1)
             1 (cfgl/->Return (e/->Variable "a"))}))))
  (testing "Doesn't eliminate a dead assignment that depends on
            another dead assignment"
    (is (not= (:after-elim dependent-dead-assigns)
              (elim-dead-assign-iteration
               (:before-elim dependent-dead-assigns))))))

(deftest eliminate-dead-assignments-test
  (is (= (:after-elim dependent-dead-assigns)
         (eliminate-dead-assignments
          (:before-elim dependent-dead-assigns)))))