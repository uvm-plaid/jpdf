{
  open Jpdparser;;
} 

let blank = [' ' '\t' '\n' '\r']
let decimal_literal = ['0'-'9']+

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
| "flip"               { FLIP }
| "s"                  { SECRET }
| "v"                  { VIEW }
| "["                  { LBRACK }
| "]"                  { RBRACK }
| ","                  { COMMA }
| ":="                 { ASSIGN }
| decimal_literal      { INT (int_of_string(Lexing.lexeme lexbuf)) }
| ";;"                 { EOEX }
| eof                  { EOEX }

{} 



