// define lexer and parser rules in a single combined grammar file
grammar Prelude;

program : exprfuncsection? cmdfuncsection? EOF;

exprfuncsection : 'exprfunctions:' exprfunc* ;
exprfunc : ident '(' (ident (',' ident)*)? ')' '{' expr '}' #ExprFunc ;

cmdfuncsection : 'cmdfunctions:' cmdfunc* ;
cmdfunc : ident '(' (ident (',' ident)*)? ')' '{' command '}' #CommandFunc ;

expr
    : expr '.' ident #FieldSelectExpr
    | expr '@' expr #AtExpr
    | '-' expr #MinusExpr
    | expr '*' expr #TimesExpr
    | expr '+' expr #PlusExpr
    | expr '++' expr #ConcatExpr
    | '(' expr ')' #ParenPExpr
    | 'let' ident '=' expr 'in' expr #LetExpr
    | ident '(' (expr (',' expr)*)? ')' #FunctionCallExpr
    | 's' '[' expr ']' #SecretExpr
    | 'r' '[' expr ']' #RandomExpr
    | 'm' '[' expr ']' #MessageExpr
    | 'p' '[' expr ']' #PublicExpr
    | 'out' #OutputExpr
    | '{' (flddecl (';' flddecl)*)? '}' #FieldExpr
    | ident #IdentExpr
    | STRING #Str
    | VALUE #Num
    ;

command
    : command (';' command) #CommandList
    | expr ':=' ('(')? expr (')')? #AssignCommand
    | 'assert' '(' expr '=' expr ')' '@' expr #AssertCommand
    | ident '(' (expr (',' expr)*)? ')' #FunctionCallCommand
    | 'let' ident '=' expr 'in' command #LetCommand
    ;

flddecl : ident '=' expr ;
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
