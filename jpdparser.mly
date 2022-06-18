%{

    open Jpdast;;

%}

/*
 * Tokens
 */

%token AND
%token <bool> BOOL
%token EOEX
%token LPAREN
%token NOT
%token OR
%token XOR
%token SEQUENCE
%token ASSIGN
%token RPAREN
%token LBRACK
%token RBRACK
%token COMMA
%token <int> INT
%token <char> CHAR
%token FLIP
%token LOCAL
%token SELECT
%token VIEW
%token SECRET

/*
 * Precedences and associativities.  Lower precedences come first.
*/

%right SEQUENCE                              
%right ASSIGN
%right OR                               /* Or */
%right XOR                               /* Or */
%right AND                              /* And */
%right NOT                              /* not e */
%nonassoc prec_paren                    /* (e) */

/*
 * The entry point.
 */
%start main
%type <Jpdast.expr> main  

%%

main:
  expr EOEX { $1 }
;

expr:
    BOOL 
      { Bool $1 }
  | FLIP LBRACK int_expr COMMA int_expr RBRACK
      { Var(Flip, $3, $5) }
  | VIEW LBRACK int_expr COMMA int_expr RBRACK
      { Var(View, $3, $5) }
  | SECRET LBRACK int_expr COMMA int_expr RBRACK
      { Var(Secret, $3, $5) }
  | LOCAL  LBRACK int_expr COMMA int_expr RBRACK
      { Var(Local, $3, $5) }
  | expr AND expr
      { And($1, $3) }
  | expr OR expr
      { Or($1, $3) }
  | expr XOR expr
      { Xor($1, $3) }
  | NOT expr
      { Not $2 }
  | SELECT LPAREN expr COMMA expr COMMA expr RPAREN
      { Select($3,$5,$7) }
  | LPAREN expr RPAREN
      { $2 }
  | expr SEQUENCE expr
      { Seq($1, $3) }
  | VIEW LBRACK int_expr COMMA int_expr RBRACK ASSIGN expr
      { Assign((View, $3, $5), $8) }
  | LOCAL LBRACK int_expr COMMA int_expr RBRACK ASSIGN expr
      { Assign((Local, $3, $5), $8) }
;

char_expr:
   CHAR
    { $1 }
;

int_expr:
   INT
    { $1 }
;
%%

