{
  open Jpdparser;;
} 

let blank = [' ' '\t' '\n' '\r']
let decimal_literal = ['0'-'9']+
let char_literal = ['a'-'e']

rule token = parse
| blank+               { token lexbuf }
| "and"                { AND }
| "or"                 { OR }
| "xor"                { XOR }
| "not"                { NOT }
| "false"              { BOOL false }
| "true"               { BOOL true }
| "("                  { LPAREN }
| ")"                  { RPAREN }
| ";"                  { SEQUENCE }
| ":="                 { ASSIGN }
| "select"             { SELECT }
| "flip"               { FLIP }
| "s"                  { SECRET }
| "v"                  { VIEW }
| "x"                  { LOCAL }
| "["                  { LBRACK }
| "]"                  { RBRACK }
| ","                  { COMMA }
| ":="                 { ASSIGN }
| decimal_literal      { INT (int_of_string(Lexing.lexeme lexbuf)) }
(* | char_literal         { CHAR ((Lexing.lexeme lexbuf)).[0] }  ZZZZZZZ *)
| ";;"                 { EOEX }
| eof                  { EOEX }

{} 



