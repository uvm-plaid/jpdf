// define lexer and parser rules in a single combined grammar file
grammar Prelude;
/* Parser Rules*/

program : function+ EOF;

function : fname LPAREN (y (',' y)*)? RPAREN LCURLY p_expression RCURLY #ExprFunc
          | fname LPAREN (y (',' y)*)? RPAREN LCURLY command RCURLY #CommandFunc
          ;

p_expression
    : p_expression '*' p_expression #TimesExpr
    | p_expression pmop p_expression #PlusMinusExpr
    | p_expression '++' p_expression #ConcatExpr
    | LPAREN p_expression RPAREN #ParenPExpr
    | p_expression '.' l #FieldSelectExpr
    | 'let' y '=' p_expression 'in' p_expression #LetExpr
    | fname LPAREN p_expression (',' p_expression)* RPAREN #FunctionCallExpr
    | memloc #MemExpr
    | p_expression AT p_expression #AtExpr
    | LCURLY l '=' p_expression (';' l '=' p_expression)* RCURLY #FieldExpr
    | y #EVarExpr
    /*| value #ValExpr*/
    | STRING #Str
    | VALUE #Num
    ;

command : command (';' command) #CommandList
        | p_expression ASSIGN (LPAREN)? p_expression (RPAREN)? #AssignCommand
        | 'assert' LPAREN p_expression '=' p_expression RPAREN AT p_expression #AssertCommand
        | fname LPAREN p_expression (',' p_expression)* RPAREN #FunctionCallCommand
        ;

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

pmop : '+' | '-' ;

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

// We define identifier to match any combination of uppercase, lowercase, and integer
IDENTIFIER : [a-zA-Z0-9]+;

// We define string to match double quotes
STRING : '"' ~('"')+ '"';
// We define operator tokens consiting of plus, minus, and mutiplication
TIMES :'*';
PLUS : '+';
CONCAT : '++';
MINUS : '-';
LPAREN : '(';
RPAREN : ')';
LSQUARE : '[';
RSQUARE : ']';
LCURLY : '{';
RCURLY : '}';
// We represent a whitespace token, ignored by skip
WS : [ \t\n\r\f]+ -> skip;
