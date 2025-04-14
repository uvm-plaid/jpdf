// define lexer and parser rules in a single combined grammar file
grammar Prelude;

program : exprfuncsection? cmdfuncsection? constraintfuncsection? precondsection? postcondsection? EOF;

constraintfuncsection : 'constraintfunctions:' constraintfunction* ;
constraintfunction : ident '(' (ident (',' ident)*)? ')' '{' constraintExpr '}' #ConstraintFunc ;

exprfuncsection : 'exprfunctions:' exprfunction* ;
exprfunction : ident '(' (ident (',' ident)*)? ')' '{' expr '}' #ExprFunc ;

cmdfuncsection : 'cmdfunctions:' cmdfunc* ;
cmdfunc : //ident '(' (ident (',' ident)*)? ')' '{' command '}' #CommandFunc
          precondsection? ident '(' (typedIdent (',' typedIdent)*)? ')' '{' command '}' postcondsection? #CommandFunc ;

precondsection : 'precondition: (' constraintExpr ')';
postcondsection : 'postcondition: (' constraintExpr ')';

constraintExpr
    : '(' constraintExpr ')' #ParenConstraintExpr
    | expr '==' expr #EqualConstraintExpr
    | 'NOT' constraintExpr #NotConstraintExpr
    | constraintExpr 'AND' constraintExpr #AndConstraintExpr //left associative
    | ident '(' (expr (',' expr)*)? ')' #FunctionCallConstraintExpr
    | 'T' #TrueConstraintExpr
    ;

expr
    : expr '.' ident #FieldSelectExpr
    | expr '@' expr #AtExpr
    | '-' expr #MinusExpr
    | expr '*' expr #TimesExpr
    | expr '+' expr #PlusExpr
    | expr '++' expr #ConcatExpr
    | '(' expr ')' #ParenPExpr
    | 'let' ident '=' expr 'in' expr #LetExpr
    | 'OT' '(' expr '@' expr ',' expr ',' expr ')' #OTExpr
    | 'OT4' '(' '(' expr ',' expr ')' '@' expr ',' expr ',' expr ',' expr ',' expr ')' #OTFourExpr
    | ident '(' (expr (',' expr)*)? ')' #FunctionCallExpr
    | 's[' expr ']' #SecretExpr
    | 'r[' expr ']' #RandomExpr
    | 'm[' expr ']' #MessageExpr
    | 'p[' expr ']' #PublicExpr
    | 'out' #OutputExpr
    | '{' (flddecl (';' flddecl)*)? '}' #FieldExpr
    | ident #IdentExpr
    | STRING #Str
    | VALUE #Num
    ;

command
    : command (';' command) #CommandList // right associative
    | 'let' ident '=' expr 'in' command #LetCommand
    | 'assert' '(' expr '=' expr ')' '@' expr #AssertCommand
    | expr ':=' expr #AssignCommand
    | ident '(' (expr (',' expr)*)? ')' #FunctionCallCommand
    ;

type
    : 'cid' #PartyIndexType
    | 'string' #StringType
    | '{' (typedIdent (';' typedIdent)*)? '}' #RecordType ;

typedIdent : ident ':' type;
flddecl : ident '=' expr ;
ident : IDENTIFIER ;

/* Lexer Rules */
// We define value to be any integer
VALUE : [0-9]+;
IDENTIFIER : [_a-zA-Z][_a-zA-Z0-9]*;
//('_'|'a'..'z'|'A'..'Z') ('_'|'a'..'z'|'A'..'Z'|'0'..'9')* ;
// We define string to match double quotes
STRING : '"' ~('"')+ '"';
// We represent a whitespace token, ignored by skip
WS : [ \t\n\r\f]+ -> skip;
COMMENT : '//' ~[\r\n]* -> skip;
