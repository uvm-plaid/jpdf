// define lexer and parser rules in a single combined grammar file
grammar Overture;
/* Parser Rules*/
// We define expression to be a value, an arithmetic expression, or a string.
expression : expression TIMES expression
            | expression PLUS expression
            | MINUS expression
            | LPAREN expression RPAREN
            | 's' LSQUARE STRING RSQUARE
            | 'r' LSQUARE STRING RSQUARE
            | 'm' LSQUARE STRING RSQUARE
            | 'p' LSQUARE STRING RSQUARE
            | value;
value : VALUE;
/* Lexer Rules */
// We define value to be any integer
VALUE : [0-9]+ ;
SHARE : 's';
RANDOM : 'r';
MESSAGE : 'm';
OUTPPUT : 'p';
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
// We represent a whitespace token, ignored by skip
WS : [ \t\r\n]+ -> skip;