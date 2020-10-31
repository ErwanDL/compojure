(ns compojure.e-regex)

(def while-kw-re #"while")
(def int-kw-re #"int")
(def void-kw-re #"void")
(def char-kw-re #"char")
(def if-re #"if")
(def else-kw-re #"else")
(def return-kw-re #"return")
(def print-kw-re #"print")
(def struct-kw-re #"struct")
(def dot-re #"\.")
(def plus-re #"\+")
(def minus-re #"-")
(def asterisk-re #"\*")
(def division-re #"/")
(def modulo-re #"%")
(def lbrace-re #"\{")
(def rbrace-re #"\}")
(def lbracket-re #"\[")
(def rbracket-re #"\]")
(def lparen-re #"\(")
(def rparen-re #"\)")
(def semicolon-re #";")
(def comma-re #",")
(def assign-re #"=")
(def equality-re #"==")
(def non-equality-re #"!=")
(def strict-lesser-re #"<")
(def strict-greater-re #">")
(def lesser-eq-re #"<=")
(def greater-eq-re #">=")
(def identifier-re #"[a-zA-Z][a-zA-Z0-9_]*")
(def sline-comment-re #"//.*")
(def mline-comment-re #"(?s)/\*.*\*/")
(def char-re #"'[\p{ASCII}]'")
(def string-re #"\".*\"")
(def whitespace-re #"\s+")
(def int-re #"-?[0-9]+")


    ; (Cat (char_regexp '"',
    ;       Cat (Star (
    ;           Alt (
    ;             char_range (List.filter (fun c -> c <> '"' && c <> '\\') alphabet),
    ;             Cat (char_regexp '\\', char_range (char_list_of_string "tn0\\\""))
    ;           )
    ;         ),
    ;            char_regexp '"')),
    ;  fun s -> Some (SYM_STRING (Stdlib.Scanf.unescaped (String.slice ~first:1 ~last:(-1) s))));
    ; (char_range (char_list_of_string " \t\n"), fun s -> None);
    ; (plus digit_regexp, fun s -> Some (SYM_INTEGER (int_of_string s)));
    ; (Eps, fun s -> Some (SYM_EOF))