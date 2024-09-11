// define lexer and parser rules in a single combined grammar file
grammar Prelude;
/* Parser Rules*/

program : function+ EOF;

function : fname LPAREN (y (',' y)*)? RPAREN LCURLY p_expression RCURLY #ExprFunc
          | fname LPAREN (y (',' y)*)? RPAREN LCURLY command RCURLY #CommandFunc
          ;

p_expression : p_expression binop p_expression #OperExprvalue
            | LPAREN p_expression RPAREN #ParenPExpr
            | p_expression '.' l #FieldSelecExpr
            | 'let' y '=' p_expression 'in' p_expression #LetExpr
            | fname LPAREN p_expression (',' p_expression)* RPAREN #FuncExpr
            | memloc #MemExpr
            | p_variable #VarExpr
            | LCURLY l '=' p_expression (';' l '=' p_expression)* RCURLY #FieldExpr
            | y #EVarExpr
            | value #ValueExpr
            ;

command : command (';' command) #ListCommand
        | p_expression ASSIGN (LPAREN)? p_expression (RPAREN)? AT p_expression #AssignCommand
        | 'assert' LPAREN p_expression '=' p_expression RPAREN AT p_expression #AssertCommand
        | fname LPAREN p_expression (',' p_expression)* RPAREN #FunctionCommand
        ;

p_variable : memloc AT p_expression;
memloc : secretloc | randomloc | messageloc | publicloc;
secretloc : SECRET index #SecretMemory;
randomloc : RANDOM index #RandomMemory;
messageloc : MESSAGE index #MessageMemory;
publicloc : PUBLIC index #PublicMemory;
outputloc : OUTPUT #OutputMemory;
index : LSQUARE p_expression RSQUARE;

l : IDENTIFIER #Field;
y : IDENTIFIER #EVar;
fname : IDENTIFIER #FName;
binop :  TIMES | PLUS | MINUS | CONCAT;

value : STRING
      | VALUE
      | o_expression
      | o_variable
      | LCURLY l '=' value (';' l '=' value)* RCURLY
      ;

o_expression : o_expression TIMES o_expression #TimesExpr
            | o_expression PLUS o_expression #PlusExpr
            | MINUS o_expression #MinusExpr
            | LPAREN o_expression RPAREN #ParenOExpr
            | RANDOM LSQUARE STRING RSQUARE #RandomExpr
            | SECRET LSQUARE STRING RSQUARE #SecretExpr
            | MESSAGE LSQUARE STRING RSQUARE #MessageExpr
            | PUBLIC LSQUARE STRING RSQUARE #PublicExpr
            | VALUE #ValExpr
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
