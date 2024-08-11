// define lexer and parser rules in a single combined grammar file
grammar Overture;
/* Parser Rules*/

program : assignment+ ;
assignment : dest ASSIGN source ;

expr
    : LPAREN expr RPAREN #ParenExpr
    | MINUS expr #MinusExpr
    | expr TIMES expr #TimesExpr
    | expr PLUS expr #PlusExpr
    | memloc #MemExpr
    | VALUE #ValueExpr
    ;

atparty : AT party ;
dest : outputloc atparty; // TODO Add more
source : expr atparty ;
memloc : secretloc | randomloc | messageloc | publicloc | outputloc ;
secretloc : SECRET index #SecretMemory ;
randomloc : RANDOM index #RandomMemory ;
messageloc : MESSAGE index #MessageMemory ;
publicloc : PUBLIC index #PublicMemory ;
outputloc : OUTPUT #OutputMemory ;
index : LSQUARE IDENTIFIER RSQUARE ; // TODO Should be identifier, why not working?
party : VALUE ;

VALUE : [0-9]+ ;

ASSIGN : ':=' ;
SECRET : 's' ;
RANDOM : 'r' ;
MESSAGE : 'm' ;
PUBLIC : 'p' ;
OUTPUT : 'out' ;

// We define identifier to match any combination of uppercase, lowercase, and integer
IDENTIFIER : [0-9A-Za-z]+ ;

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