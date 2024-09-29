// define lexer and parser rules in a single combined grammar file
grammar Prelude;
/* Parser Rules*/

program : function+ EOF;

function : ident LPAREN (ident (',' ident)*)? RPAREN LCURLY expr RCURLY #ExprFunc
          | ident LPAREN (ident (',' ident)*)? RPAREN LCURLY command RCURLY #CommandFunc
          ;

expr
    : expr '.' ident #FieldSelectExpr
    | expr '*' expr #TimesExpr
    | expr pmop expr #PlusMinusExpr
    | expr '++' expr #ConcatExpr
    | LPAREN expr RPAREN #ParenPExpr
    | 'let' ident '=' expr 'in' expr #LetExpr
    | fname LPAREN expr (',' expr)* RPAREN #FunctionCallExpr
    | memloc #MemExpr
    | expr AT expr #AtExpr
    | LCURLY ident '=' expr (';' ident '=' expr)* RCURLY #FieldExpr
    | IDENTIFIER #IdentExpr
    | STRING #Str
    | VALUE #Num
    ;

command : command (';' command) #CommandList
        | expr ASSIGN (LPAREN)? expr (RPAREN)? #AssignCommand
        | 'assert' LPAREN expr '=' expr RPAREN AT expr #AssertCommand
        | fname LPAREN expr (',' expr)* RPAREN #FunctionCallCommand
        ;

memloc : secretloc | randomloc | messageloc | publicloc;
secretloc : SECRET index #SecretExpr;
randomloc : RANDOM index #RandomExpr;
messageloc : MESSAGE index #MessageExpr;
publicloc : PUBLIC index #PublicExpr;
outputloc : OUTPUT #OutputExpr;
index : LSQUARE expr RSQUARE;

fname : IDENTIFIER;
pmop : '+' | '-' ;
ident : IDENTIFIER ;

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
