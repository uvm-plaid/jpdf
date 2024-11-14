grammar Constraints;

constraintsection : 'constraints:' constraintExpr* EOF;

constraintExpr
    : constraintTerms '==' constraintTerms #AssertConstraintExpr
    | constraintExpr 'AND' constraintExpr #AndConstraintExpr
    | 'NOT' constraintExpr #NotConstraintExpr
    ;

constraintTerms
    : constraintTerms '+' constraintTerms #PlusConstraintTerm
    | constraintTerms '-' constraintTerms #MinusConstraintTerm
    | constraintTerms '*' constraintTerms #TimesConstraintTerm
    | 's' '[' STRING ']' '@' VALUE #SecretConstraintTerm
    | 'r' '[' STRING ']'  '@' VALUE #RandomConstraintTerm
    | 'm' '[' STRING ']' '@' VALUE #MessageConstraintTerm
    | 'p' '[' STRING ']' '@' VALUE #PublicConstraintTerm
    | 'out' '@' VALUE #OutputConstraintTerm
    ;

STRING : '"' ~('"')+ '"';
VALUE : [0-9]+;
WS : [ \t\n\r\f]+ -> skip;