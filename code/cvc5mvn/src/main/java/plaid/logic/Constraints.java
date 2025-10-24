package plaid.logic;

import plaid.ast.Constraint;

public class Constraints {
    Constraint precondition;
    Constraint postcondition;

    // constructor
    public Constraints(Constraint precondition, Constraint postcondition){
        this.precondition = precondition;
        this.postcondition = postcondition;
    }
    
    // return precondition
    public Constraint getPre(){
        return precondition;
    }
    
    // return postcondition
    public Constraint getPost(){
        return postcondition;
    }
}
