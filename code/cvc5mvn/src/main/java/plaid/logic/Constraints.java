package plaid.logic;

import plaid.ast.ConstraintExpr;

public class Constraints {
    ConstraintExpr precondition;
    ConstraintExpr postcondition;

    // constructor
    public Constraints(ConstraintExpr precondition, ConstraintExpr postcondition){
        this.precondition = precondition;
        this.postcondition = postcondition;
    }
    
    // return precondition
    public ConstraintExpr getPre(){
        return precondition;
    }
    
    // return postcondition
    public ConstraintExpr getPost(){
        return postcondition;
    }
}
