// define lexer and parser rules in a single combined grammar file
grammar Overture;
/* Parser Rules*/

program : assignment+ ;
assignment : dest ASSIGN expr atparty ;

expr
    : LPAREN expr RPAREN
    | MINUS expr
    | expr TIMES expr
    | expr PLUS expr
    | memloc
    | VALUE
    ;

atparty : AT party ;
dest : outputloc atparty; // TODO Add more
memloc : secretloc | randomloc | messageloc | publicloc | outputloc ;
secretloc : SECRET index ;
randomloc : RANDOM index ;
messageloc : MESSAGE index ;
publicloc : PUBLIC index ;
outputloc : OUTPUT ;
index : LSQUARE VALUE RSQUARE ; // TODO Should be identifier, why not working?
party : VALUE ;

VALUE : [0-9]+ ;

ASSIGN : ':=' ;
SECRET : 's' ;
RANDOM : 'r' ;
MESSAGE : 'm' ;
PUBLIC : 'p' ;
OUTPUT : 'out' ;

// We define identifier to match any combination of uppercase, lowercase, and integer
IDENTIFIER : [a-zA-Z][a-zA-Z0-9]+;

TIMES : '*' ;
PLUS : '+' ;
MINUS : '-' ;
LPAREN : '(' ;
RPAREN : ')' ;
LSQUARE : '[' ;
RSQUARE : ']' ;
AT : '@' ;

// We represent a whitespace token, ignored by skip
WS : [ \t\r\n]+ -> skip ;