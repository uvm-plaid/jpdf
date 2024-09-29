// define lexer and parser rules in a single combined grammar file
grammar Prelude;
/* Parser Rules*/

program : function+ EOF;

function : ident '(' (ident (',' ident)*)? ')' '{' expr '}' #ExprFunc
          | ident '(' (ident (',' ident)*)? ')' '{' command '}' #CommandFunc
          ;

expr
    : expr '.' ident #FieldSelectExpr
    | expr '*' expr #TimesExpr
    | expr pmop expr #PlusMinusExpr
    | expr '++' expr #ConcatExpr
    | '(' expr ')' #ParenPExpr
    | 'let' ident '=' expr 'in' expr #LetExpr
    | ident '(' expr (',' expr)* ')' #FunctionCallExpr
    | 's' '[' expr ']' #SecretExpr
    | 'r' '[' expr ']' #RandomExpr
    | 'm' '[' expr ']' #MessageExpr
    | 'p' '[' expr ']' #PublicExpr
    | 'out' #OutputExpr
    | expr '@' expr #AtExpr
    | '{' ident '=' expr (';' ident '=' expr)* '}' #FieldExpr
    | IDENTIFIER #IdentExpr
    | STRING #Str
    | VALUE #Num
    ;

command : command (';' command) #CommandList
        | expr ':=' ('(')? expr (')')? #AssignCommand
        | 'assert' '(' expr '=' expr ')' '@' expr #AssertCommand
        | ident '(' expr (',' expr)* ')' #FunctionCallCommand
        ;

pmop : '+' | '-' ;
ident : IDENTIFIER ;

/* Lexer Rules */
// We define value to be any integer
VALUE : [0-9]+;
// We define identifier to match any combination of uppercase, lowercase, and integer
IDENTIFIER : [a-zA-Z0-9]+;
// We define string to match double quotes
STRING : '"' ~('"')+ '"';
// We represent a whitespace token, ignored by skip
WS : [ \t\n\r\f]+ -> skip;
