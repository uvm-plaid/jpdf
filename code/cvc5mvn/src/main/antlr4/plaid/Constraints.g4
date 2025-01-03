grammar Constraints;

constraints : 'constraints:' constraintsExpr* EOF;

constraintsExpr
    : '(' constraintsExpr ')' #ParenConstraintsExpr
    | constraintsTerm '==' constraintsTerm #EqualConstraintsExpr
    | constraintsExpr 'AND' constraintsExpr #AndConstraintsExpr
    | 'NOT' constraintsExpr #NotConstraintsExpr
    ;

constraintsTerm
    : '(' constraintsTerm ')' #ParenConstraintsTerm
    | constraintsTerm '*' constraintsTerm #TimesConstraintsTerm
    | constraintsTerm '+' constraintsTerm #PlusConstraintsTerm
    | '-' constraintsTerm #MinusConstraintsTerm
    | 's' '[' STRING ']' '@' VALUE #SecretConstraintsTerm
    | 'r' '[' STRING ']'  '@' VALUE #RandomConstraintsTerm
    | 'm' '[' STRING ']' '@' VALUE #MessageConstraintsTerm
    | 'p' '[' STRING ']' #PublicConstraintsTerm
    | 'out' '@' VALUE #OutputConstraintsTerm
    ;

STRING : '"' ~('"')+ '"';
VALUE : [0-9]+;
WS : [ \t\n\r\f]+ -> skip;
COMMENT : '//' ~[\r\n]* -> skip;