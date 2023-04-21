open Jpdast;;
open Printf;;

let rec pp e pad =
  match e with
    Bool(true) -> "Bool(true)"
  | Bool(false) -> "Bool(false)"
  | And(e1, e2) ->
     "And(" ^ pp e1 pad ^ ", " ^ pp e2 pad ^ ")"
  | Or(e1, e2) ->
     "Or(" ^ pp e1 pad ^ ", " ^ pp e2 pad ^ ")"
  | Xor(e1, e2) ->
     "Xor(" ^ pp e1 pad ^ ", " ^ pp e2 pad ^ ")"
  | Not(e1) ->
     "Not(" ^ pp e1 pad ^ ")"
  | Select(e1, e2, e3) ->
     "Select(" ^ pp e1 pad ^ ", " ^ pp e2 pad ^ ", " ^ pp e3 pad ^ ")"
  | Var(Flip, n1, n2) ->
     "Var(Flip, " ^ (string_of_int n1) ^ ", " ^  (string_of_int n2) ^ ")"
  | Var(Secret, n1, n2) ->
     "Var(Secret, " ^ (string_of_int n1) ^ ", " ^  (string_of_int n2) ^ ")"
  | Var(View, n1, n2) ->
     "Var(View, " ^ (string_of_int n1) ^ ", " ^  (string_of_int n2) ^ ")"
  | Var(Local, n1, n2) ->
     "Var(Local, " ^ (string_of_int n1) ^ ", " ^  (string_of_int n2) ^ ")"
(*  | Var(Local(x), n1, n2) ->
     "Var(Local" ^ "(" ^ Char.escaped x ^ ")" ^ ", " ^
       (string_of_int n1) ^ ", " ^  (string_of_int n2) ^ ")" *)
  | Assign((View, n1, n2), e) -> 
     "Assign((View, " ^ (string_of_int n1) ^ ", " ^  (string_of_int n2) ^ "), " ^ pp e pad ^ ")"
  | Assign((Local, n1, n2), e) -> 
     "Assign((Local, " ^ (string_of_int n1) ^ ", " ^  (string_of_int n2) ^ "), " ^ pp e pad ^ ")"
(*  | Assign((Local(x), n1, n2), e) -> 
     "Assign((Local" ^  "(" ^ Char.escaped x ^ ")" ^ ", " ^
       (string_of_int n1) ^ ", " ^  (string_of_int n2) ^ "), " ^ pp e pad ^ ")" *)
  | Seq(e1, e2) -> "Seq(" ^ pp e1 pad ^ ",\n" ^ pp e2 pad ^ ")"

let pretty_print e = (pp e "") ^ "\n"

let _ =
  while true do
    Printf.printf ">> ";
    flush stdout;
    let lexbuf = Lexing.from_channel stdin in
    let result = Jpdparser.main Jpdlexer.token lexbuf in
    print_string (pretty_print result); flush stdout
  done

