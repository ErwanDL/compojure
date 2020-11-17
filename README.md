# Compojure

This is an in-progress compiler for a subset of the C language (called the "E" language in this project, which will hopefully resemble C closely once finished), written in Clojure.

This is an application project for CentraleSupélec's compilation course : the compiler was initially meant to be written in OCaml (based on [this skeleton](https://gitlab-research.centralesupelec.fr/cidre-public/compilation/infosec-ecomp)), but I was looking for a cool project to perfect my Clojure skills, so I figured I would just re-write it in Clojure.

## Progress

-   [x] Lexer and parser (using Instaparse)
-   [x] Translation from AST to E-lang high level representation, and interpreter for this representation (`e_interpreter.clj`). This interpreter is called by default from the main function :

    ```
    $ lein run "main(a) { return a + 1; }" 4
    >> 5
    ```

-   [x] Translation to CFG (Control Flow Graph) IR, and interpreter for this IR (`cfg_interpreter.clj`)
-   [ ] Dead code detection and elimination
-   [ ] Translation to RTL (Register Transfer Language)
-   [ ] Translation to RISC-V assembly

## Language features

More features will be added once a first working compiler for the initial set of features is achieved.

-   [x] Integer literals and variables
-   [x] Integer arithmetic
-   [x] Variable assignment
-   [x] Print statement
-   [x] Return statement
-   [x] While loops
-   [x] If/else conditionals
-   [ ] Subroutines (allow calling other functions from `main`)
-   [ ] String type
-   [ ] Arrays
-   [ ] Global variables
-   [ ] Pointers
