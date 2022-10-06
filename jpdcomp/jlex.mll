

(*let string_buf = Buffer.create 256; *)

(*let reset_string_buffer () = Buffer.clear string_buf *)

let backslash_escapes =  ['\\']

let blank = [' ' '\t' '\n' '\r']
let decimal_literal = ['0'-'9']+
let char_literal = ['a'-'e']

rule main = parse
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
    | "let"                { LET }
    | "||"                 { CONCAT } 
    | "."                  { DOT }
    | "s"                  { SECRET }
    | "v"                  { VIEW }
    | "h"                  { H }
    | "ot"                 { OBLIV }
    | "@"                  { AT }
    | "["                  { LBRACK }
    | "]"                  { RBRACK }
    | ","                  { COMMA }
    | ":="                 { ASSIGN }
    | '"'                  { Buffer.clear buff;;
                             string Buffer.create 256 buff;
                             STR (Buffer.contents buff)} (*(string Buffer.create 256 lexbuf) }*)
    
(*        { Buffer.clear string_buf;
          string lexbuf;
          STRING (Buffer.contents string_buf) } *)
(* | decimal_literal      { INT (int_of_string(Lexing.lexeme lexbuf)) } *)
(* | char_literal         { CHAR ((Lexing.lexeme lexbuf)).[0] }  ZZZZZZZ *)
    | ";;"                 { EOEX }
    | eof                  { EOF }
    | _                    { error lexbuf "found '%s' " @@ get lexbuf}

and string = parse
    | '"' { () }(*
    | '"' { Buffer.contents buff}*)
    | _ as ch
        { Buffer.add_char buff ch;
          string lexbuf }