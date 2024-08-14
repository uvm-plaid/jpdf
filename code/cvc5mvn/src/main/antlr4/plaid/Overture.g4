// define lexer and parser rules in a single combined grammar file
grammar Overture;
/* Parser Rules*/
// We define expression to be a value, an arithmetic expression, or a string.
// TODO Revision 2
/*
protocol
    : dest ASSIGN source #Command
    | protocol ';' protocol #Protocols ;
*/

protocol : command (';' command)* EOF ;
command : dest ASSIGN source ;

expression : expression TIMES expression #TimesExpr
            | expression PLUS expression #PlusExpr
            | MINUS expression #MinusExpr
            | LPAREN expression RPAREN #ParenExpr
            | memloc #MemExpr
            | VALUE #ValueExpr
            ;

variable : memloc atparty | OUTPUT atparty;

atparty : AT VALUE;
dest : destloc atparty;
destloc : messageloc | publicloc | outputloc;
messageloc : MESSAGE index #MessageMemory;
publicloc : PUBLIC #PublicMemory;
outputloc : OUTPUT #OutputMemory;
index : LSQUARE STRING RSQUARE;
source : expression atparty;

memloc : secretloc | randomloc | messageloc | publicloc;
secretloc : SECRET index #SecretMemory;
randomloc : RANDOM index #RandomMemory;

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
MINUS : '-';
LPAREN : '(';
RPAREN : ')';
LSQUARE : '[';
RSQUARE : ']';
//NEWLINE : '\r'? '\n';
// We represent a whitespace token, ignored by skip
WS : [ \t\r\n]+ -> skip;
