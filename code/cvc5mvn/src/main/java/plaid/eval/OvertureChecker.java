package plaid.eval;


import plaid.ast.*;

import java.util.List;

public class OvertureChecker {

    public void atsHaveNums(Node node) {
        switch (node) {
            case AtExpr x -> {
                if (!(x.getE2() instanceof Num)) {
                    throw new RuntimeException("not a number: " + x.getE2());
                }
            }
            default -> node.children().forEach(this::atsHaveNums);
        }
    }

    public void noAt(Node node){
        switch(node){
            case AtExpr atExpr -> {
                throw new RuntimeException("multiple at expressions");
            }
            default -> node.children().forEach(this::noAt);
        }
    }

    // check right side of assignment. there should not be multiple at expression
    public void isValidSender(Node node){
        switch (node) {
            // e1 := e2
            case AssignCommand assignCommand -> {
                if (assignCommand.getE2().getClass().equals(AtExpr.class)) {
                    assignCommand.getE2().children().forEach(this::noAt);
                }
            }
            default -> node.children().forEach(this::isValidSender);
        }
    }

    // especially for output case, party indices should agree
    public void isIndexMatch(Node node){
        switch(node){
            // out@i := e@i
            case AssignCommand assignCommand -> {
                if(assignCommand.getE1().getClass().equals(AtExpr.class)){

                }
            }
            default -> node.children().forEach(this::isIndexMatch);
        }
    }
    // check w is a string in m[w]

    // check e is overture expression in m[w] := e@i

    // left side only should be memories
//    public void isLeftMemories(Node node){
//        switch(node){
//            case MemoryExpr
//            case PublicExpr
//            case OutputExpr
//        }
//    }
    public void isOverture(PreludeCommand c){

            // TODO: the result should be overture protocol
            // the protocol looks like (m[w]/p[w]/out@i := e@i)
            // we don't want out@2 := m["x"]@2 + m["y"]@2
            // we also don't want different party numbers for receiver of output and sender
                // we don't want out@1 := m["x"]@2

            //
            // let expression evaluates to value (string, party index, overture expr, overture var, field

            // e1@e2 => overture expression @ party number

            // expression function call evaluates to value

            // field expression evaluates to {field name = value}
            // field selection evaluates to a value

            // memory expression evaluates to m[w]

            // binary operation evaluates to overture expression (number, overture memory, binary operation expressions, OT)

            // assign command evaluates to (overture variable := overture expr@i) // overture variable is overture expressions appended party number

            // assert command evaluates to assert (overture expr = overture expr) @ i

            // command function call evaluates to protocol

            // let command evaluates to protocol


            // prelude expression evaluates to protocol


    }
}
