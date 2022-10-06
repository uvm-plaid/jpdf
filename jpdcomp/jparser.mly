%{

    open Jpast;;

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
%token <string> STR
/* %token <char> CHAR */
%token FLIP
%token LOCAL
%token SELECT
%token VIEW
%token SECRET

/* new tokens */

%token OBLIV
%token H
%token AT
%token DOT
%token CONCAT

/*
 * Precedences and associativities.  Lower precedences come first.
*/

%right SEQUENCE                              
%right ASSIGN
%right OR                               /* Or */
%right XOR                               /* Or */
%right AND                              /* And */
%right NOT                              /* not e */
%right CONCAT
%right AT
%right DOT
%nonassoc prec_paren                    /* (e) */

/*
 * The entry point.
 */
%start main
%type <Jpast.expr> main  

%%

main:
  expr EOEX { $1 }
;

expr:
    BOOL 
      { Bool $1 }
  | NOT expr
      { Not $2 }

  | expr AND expr
      { And($1, $3) }
  | expr OR expr
      { Or($1, $3) }
  | expr XOR expr
      { Xor($1, $3) }
  | expr AT expr
      { $1 @ $3}
  | expr DOT field_expr
      { $1.$3}
  | str_expr CONCAT str_expr
      {$1 || $3}

  | FLIP LBRACK int_expr COMMA str_expr RBRACK
      { Var(Flip, $3, $5) }
  | VIEW LBRACK int_expr COMMA str_expr RBRACK
      { Var(View, $3, $5) }
  | SECRET LBRACK int_expr COMMA str_expr RBRACK
      { Var(Secret, $3, $5) }
  | SELECT LPAREN expr COMMA expr COMMA expr RPAREN
      { Select($3,$5,$7) }

  | OBLIV LPAREN expr COMMA expr COMMA expr RPAREN
      { OT($3,$5,$7) }

  | H LBRACK str_expr RBRACK
      { H[$3]}

  | LPAREN expr RPAREN
      { $2 }
  | expr SEQUENCE expr
      { Seq($1, $3) }
  | VIEW LBRACK int_expr COMMA str_expr RBRACK ASSIGN expr
      { Assign((View, $3, $5), $8) }
;

str_expr:
   STR
    { $1 }
;

int_expr:
   INT
    { $1 }
;

field_expr:
   STR
   { $1 }
;


%%
