// define lexer and parser rules in a single combined grammar file
grammar Prelude;
/* Parser Rules*/

program : function+ EOF;

function : fname LPAREN (y (',' y)*)? RPAREN LCURLY p_expression RCURLY #ExprFunc
          | fname LPAREN (y (',' y)*)? RPAREN LCURLY command RCURLY #CommandFunc
          ;

p_expression : p_expression binop p_expression #OperExprvalue
            | LPAREN p_expression RPAREN #ParenPExpr
            | p_expression '.' l #FieldSelectExpr
            | 'let' y '=' p_expression 'in' p_expression #LetExpr
            | fname LPAREN p_expression (',' p_expression)* RPAREN #FunctionCallExpr
            | memloc #MemExpr
            | p_expression AT p_expression #AtExpr
            | LCURLY l '=' p_expression (';' l '=' p_expression)* RCURLY #FieldExpr
            | y #EVarExpr
            | value #ValExpr
            ;

command : command (';' command) #CommandList
        | p_expression ASSIGN (LPAREN)? p_expression (RPAREN)? #AssignCommand
        | 'assert' LPAREN p_expression '=' p_expression RPAREN AT p_expression #AssertCommand
        | fname LPAREN p_expression (',' p_expression)* RPAREN #FunctionCallCommand
        ;

//p_variable : memloc AT p_expression;
memloc : secretloc | randomloc | messageloc | publicloc;
secretloc : SECRET index #SecretExpr;
randomloc : RANDOM index #RandomExpr;
messageloc : MESSAGE index #MessageExpr;
publicloc : PUBLIC index #PublicExpr;
outputloc : OUTPUT #OutputExpr;
index : LSQUARE p_expression RSQUARE;

l : IDENTIFIER;
y : IDENTIFIER;
fname : IDENTIFIER;
binop :  TIMES #TimesExpr| PLUS #PlusExpr| MINUS #MinusExpr| CONCAT #ConcatExpr;

value : STRING #String
      | VALUE #Val
      | o_expression #Expr
      | o_variable #Var
      | LCURLY l '=' value (';' l '=' value)* RCURLY #Field
      ;

o_expression : o_expression TIMES o_expression #Times
            | o_expression PLUS o_expression #Plus
            | MINUS o_expression #Minus
            | LPAREN o_expression RPAREN #Paren
            | RANDOM LSQUARE STRING RSQUARE #RandomMemory
            | SECRET LSQUARE STRING RSQUARE #SecretMemory
            | MESSAGE LSQUARE STRING RSQUARE #MessageMemory
            | PUBLIC LSQUARE STRING RSQUARE #PublicMemory
            | VALUE #ValueExpr
            ;

o_variable : RANDOM LSQUARE STRING RSQUARE AT VALUE #RandomVar
            | SECRET LSQUARE STRING RSQUARE AT VALUE #SecretVar
            | MESSAGE LSQUARE STRING RSQUARE AT VALUE #MessageVar
            | PUBLIC LSQUARE STRING RSQUARE #PublicVar
            | OUTPUT AT VALUE #OutputVar
            ;

/* Lexer Rules */
// We define value to be any integer
VALUE : [0-9]+;
SECRET : 's';
RANDOM : 'r';
MESSAGE : 'm';
PUBLIC : 'p';
OUTPUT : 'out';
AT : '@';
ASSIGN : ':=';
//LET : 'let';
//ASSERT : 'assert';
//IN : 'in';
// We define identifier to match any combination of uppercase, lowercase, and integer
IDENTIFIER : [a-zA-Z0-9]+;

// We define string to match double quotes
STRING : '"' ~('"')+ '"';
// We define operator tokens consiting of plus, minus, and mutiplication
TIMES :'*';
PLUS : '+';
CONCAT : '++';
//EQUAL : '=';
MINUS : '-';
LPAREN : '(';
RPAREN : ')';
LSQUARE : '[';
RSQUARE : ']';
LCURLY : '{';
RCURLY : '}';
//NEWLINE : '\r'? '\n';
// We represent a whitespace token, ignored by skip
WS : [ \t\n\r\f]+ -> skip;
