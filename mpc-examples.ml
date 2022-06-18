(* 
   We define the following language of expressions e that models multi-party 
   computations over the binary field.

   e ::= true | false | lab[n,n] | lab[n,n] := e | e;e | not e | e and e |
         e or e | e xor e | select(e,e,e)
 
   lab ::= s | v | x | flip

   Identifiers are denoted lab[n1,n2], where n1 is the party id and n2
   is the index, and lab is:

      - s if its a secret
      - v if it's a view
      - flip if it's a boolean value in a uniform random distribution
      - x if it's a local variable.

   We make the additional restriction that only views and local variables
   can be assigned to, and only once. In general, secrets and flips are inputs 
   to programs,  and views are outputs. Communication between parties is modeled 
   by assignment to views. The semantics of select(e1,e2,e3) are to return the 
   result of e2 if e1 is true, otherwise e1. So for example, 
  
     select(true, not true, true and true) 
  
   evaluates to false. We model OT using a "cross party" select where the 
   selection bit belongs to the receiver and does not occur in the sender's 
   view, e.g.:

     v[1,0] := select(s[1,0],x[2,0],x[2,1])

   models an oblivious transfer of party 2's local values x[2,0] and x[2,1]
   to party 1, using party 1's secret s[1,0] as the OT selection bit. 

   * We assume metatheory developed here: https://www.overleaf.com/read/cykgnbtskkbv *
      
   Intuitively a protocol is secure iff the ideal knowledge given the output 
   and corrupt inputs is the same as the adversarial knowledge given the output
   and corrupt inputs as well as the corrupt views from protocol runs.
   
   We can model ideal and adversarial knowledge as a joint probability density
   function (jpdf). Letting X1,...,XN be all the variables in the program, including
   secrets, views, and flip values, we can associate with any program a jpdf 
   P(X1,...,XN) denoting:

     prob(X1 = x1 and ... and Xn = xn) 
  
   for any possible run of the program. This jpdf can be derived in a fairly 
   straightforward manner from the truth tables of given programs- we implement 
   this with the function jpdf in jpd.ml. Different sorts of knowledge can then 
   be obtained from marginal dependent distributions. For example, in a 3-party
   protocol with inputs s[1,1], s[2,1], and s[3,1], where output is written 
   to v[0,1] and 3 is the corrupt party, the ideal knowledge can be denoted as:

     P(s[1,1],s[1,2]|s[3,1]|v[0,1])

   and if party 3 has only one view v[3,1], the adversarial knowledge is denoted as:

     P(s[1,1],s[1,2]|s[3,1]|v[3,1]|v[0,1])

   Thus, to show that the protocol is secure with respect to this honest/corrupt 
   partition, we need to show:

     P(s[1,1],s[1,2]|s[3,1]|v[0,1]) = P(s[1,1],s[1,2]|s[3,1]|v[3,1]|v[0,1])

   and we can disprove this by finding witness values v1-v5 such that:

     prob(s[1,1] = v1 and s[1,2] = v2 | s[3,1] = v3 | v[3,1] = v4 | v[0,1] = v5)
        <>
     prob(s[1,1] = v1 and s[1,2] = v2 | s[3,1] = v3 | v[0,1] = v5)
 
   In the binary field we can accomplish this through a brute-force search of the 
   possible witness space. 

   *** Implementation: ***

   A parser for this language (called parse) with the intended target ast 
   datatype (see jdpast.ml) can be built using the Makefile. 

   The program analysis is defined in jpd.ml. Highlights:
       - The function jpdf computes the jpdf for a given expression by extracting
       the truth tables for all views. 

       - Given the jpdf, marg_dist [(X1,v1);...;(Xn,vn)] [(Y1,v1);...;(Yn,vn)]
       computes:

        prob(X1 = v1 and ... and Xn = vn | Y1 = v1 | ... | Yn = vn)
       
       - The function passive_secure performs the brute-force search of the 
       witness space for a failure witness. 

   *** Examples ***

   Included are the following examples:

     - Lambda obliv examples from Figure 3 
     - Additive secret sharing (binary field)
     - Garbled circuits (binary field)
 *)

(* LAMBDA OBLIV (Figure 3 examples). We show how dependent probabilities can 
   be used to precisely characterize leakage of information. *)

(* Figure 3(a) example 

x[1,0] := flip[1,0];
x[1,1] := flip[1,1];

x[1,2] := select(s[1,0],x[1,0],x[1,1]);

v[0,0] := x[1,2];
v[0,1] := x[1,0];;
*)

let ex_a =
(Seq(Assign((Local, 1, 0), Var(Flip, 1, 0)),
Seq(Assign((Local, 1, 1), Var(Flip, 1, 1)),
Seq(Assign((Local, 1, 2), Select(Var(Secret, 1, 0), Var(Local, 1, 0), Var(Local, 1, 1))),
Seq(Assign((View, 0, 0), Var(Local, 1, 2)),
Assign((View, 0, 1), Var(Local, 1, 0)))))));;

(* This reveals the leakage discussed on page 5. The first probability is 1/2, the
   second is 2/3. *)
marg_dist [((Secret,1,0),strue)] [((View, 0, 0),strue)] (genpdf ex_a);;
marg_dist [((Secret,1,0),strue)] [((View, 0, 0),strue);((View, 0, 1),strue)] (genpdf ex_a);;

(* Figure 3(b) example 

x[1,0] := flip[1,0];
x[1,1] := flip[1,1];

x[1,2] := select(x[1,0],x[1,0],x[1,1]);
x[1,3] := select(s[1,0],x[1,2],flip[1,2]);

v[0,0] := x[1,3];;
*)

let ex_b =
(Seq(Assign((Local, 1, 0), Var(Flip, 1, 0)),
Seq(Assign((Local, 1, 1), Var(Flip, 1, 1)),
Seq(Assign((Local, 1, 2), Select(Var(Local, 1, 0), Var(Local, 1, 0), Var(Local, 1, 1))),
Seq(Assign((Local, 1, 3), Select(Var(Secret, 1, 0), Var(Local, 1, 2), Var(Flip, 1, 2))),
Assign((View, 0, 0), Var(Local, 1, 3)))))));;

(* This reveals the leakage discussed on page 6- the probability that
   the secret is 1 is higher given 1 observed in the output. *)
marg_dist [((Secret,1,0),strue)] [] (genpdf ex_b);;   
marg_dist [((Secret,1,0),strue)] [((View, 0, 0),strue)] (genpdf ex_b);;

(* ADDITIVE SHARING *)
(*
Secure 3-party addition over binary field

v[2,1] := flip[1,1] xor flip[1,2] xor s[1,1];
v[3,1] := flip[1,1];

v[1,1] := flip[2,1] xor flip[2,2] xor s[2,1];
v[3,2] := flip[2,1];

v[1,2] := flip[3,1] xor flip[3,2] xor s[3,1];
v[2,2] := flip[3,1];

v[0,1] := v[1,1] xor v[1,2] xor flip[1,2];
v[0,2] := v[2,1] xor v[2,2] xor flip[2,2];
v[0,3] := v[3,1] xor v[3,2] xor flip[3,2];
v[0,4] := v[0,1] xor v[0,2] xor v[0,3];;
*)

(* the ast of the program, where v[0,4] is the output. *)
let e1 =
(Seq(Assign((View, 2, 1), Xor(Var(Flip, 1, 1), Xor(Var(Flip, 1, 2), Var(Secret, 1, 1)))),
Seq(Assign((View, 3, 1), Var(Flip, 1, 1)),
Seq(Assign((View, 1, 1), Xor(Var(Flip, 2, 1), Xor(Var(Flip, 2, 2), Var(Secret, 2, 1)))),
Seq(Assign((View, 3, 2), Var(Flip, 2, 1)),
Seq(Assign((View, 1, 2), Xor(Var(Flip, 3, 1), Xor(Var(Flip, 3, 2), Var(Secret, 3, 1)))),
Seq(Assign((View, 2, 2), Var(Flip, 3, 1)),
Seq(Assign((View, 0, 1), Xor(Var(View, 1, 1), Xor(Var(View, 1, 2), Var(Flip, 1, 2)))),
Seq(Assign((View, 0, 2), Xor(Var(View, 2, 1), Xor(Var(View, 2, 2), Var(Flip, 2, 2)))),
Seq(Assign((View, 0, 3), Xor(Var(View, 3, 1), Xor(Var(View, 3, 2), Var(Flip, 3, 2)))),
Assign((View, 0, 4), Xor(Var(View, 0, 1), Xor(Var(View, 0, 2), Var(View, 0, 3))))))))))))));;

(* this is true! *)
passive_secure e1 3 (View,0,4);;

(* successful witness example construction *)

(* input variables *)
let vs1 =
  [(Secret, 1, 1);(Secret, 2, 1);(Secret, 3, 1);(Flip, 1, 1);(Flip, 2, 1);(Flip, 3, 1);(Flip, 1, 2);(Flip, 2, 2);(Flip, 3, 2)];; 

(* honest party inputs *)
let i1 =
  [((Secret, 1, 1), sfalse);((Secret, 2, 1), strue)];;

(* corrupt input and public output *)
let d1 = 
  [((Secret, 3, 1), sfalse);((View, 0, 4), strue)];;

(* corrupt inputs and views and public output *)
let d1' = 
  [((Secret, 3, 1), sfalse);((View, 3, 1), strue);((View, 3, 2), sfalse);((View, 0, 4), strue)];;

(* ideal knowledge is that honest parties hold either 1,0 or 0,1, so this is .5 *)
marg_dist i1 d1 (jpdf e1 vs1);;

(* this shows that adding information about the corrupt view has no impact on knowledge *)
let pdf1 = jpdf e1 vs1 in marg_dist i1 d1' pdf1 = marg_dist i1 d1 pdf1;;

(*
Insecure 3-party summation over binary field- party 1 leaks secret information to 
party 2 (assignment to v[2,0] is a bug).

v[2,1] := flip[1,1] xor flip[1,2] xor s[1,1];
v[3,1] := flip[1,1];
v[2,0] := flip[1,1] xor flip[1,2];

v[1,1] := flip[2,1] xor flip[2,2] xor s[2,1];
v[3,2] := flip[2,1];

v[1,2] := flip[3,1] xor flip[3,2] xor s[3,1];
v[2,2] := flip[3,1];

v[0,1] := v[1,1] xor v[1,2] xor flip[1,2];
v[0,2] := v[2,1] xor v[2,2] xor flip[2,2];
v[0,3] := v[3,1] xor v[3,2] xor flip[3,2];
v[0,4] := v[0,1] xor v[0,2] xor v[0,3];;
*)

(* the ast of the program, where v[0,4] is the output. *)
let e2 =
(Seq(Assign((View, 2, 1), Xor(Var(Flip, 1, 1), Xor(Var(Flip, 1, 2), Var(Secret, 1, 1)))),
Seq(Assign((View, 3, 1), Var(Flip, 1, 1)),
Seq(Assign((View, 2, 0), Xor(Var(Flip, 1, 1), Var(Flip, 1, 2))),
Seq(Assign((View, 1, 1), Xor(Var(Flip, 2, 1), Xor(Var(Flip, 2, 2), Var(Secret, 2, 1)))),
Seq(Assign((View, 3, 2), Var(Flip, 2, 1)),
Seq(Assign((View, 1, 2), Xor(Var(Flip, 3, 1), Xor(Var(Flip, 3, 2), Var(Secret, 3, 1)))),
Seq(Assign((View, 2, 2), Var(Flip, 3, 1)),
Seq(Assign((View, 0, 1), Xor(Var(View, 1, 1), Xor(Var(View, 1, 2), Var(Flip, 1, 2)))),
Seq(Assign((View, 0, 2), Xor(Var(View, 2, 1), Xor(Var(View, 2, 2), Var(Flip, 2, 2)))),
Seq(Assign((View, 0, 3), Xor(Var(View, 3, 1), Xor(Var(View, 3, 2), Var(Flip, 3, 2)))),
    Assign((View, 0, 4), Xor(Var(View, 0, 1), Xor(Var(View, 0, 2), Var(View, 0, 3)))))))))))))));;

(* this is false! *)
passive_secure e2 3 (View,0,4);;

(* failure witness example construction *)

(* input variables *)
let vs2 =
  [(Secret, 1, 1);(Secret, 2, 1);(Secret, 3, 1);(Flip, 1, 1);(Flip, 2, 1);(Flip, 3, 1);(Flip, 1, 2);(Flip, 2, 2);(Flip, 3, 2)];; 

(* honest party inputs *)
let i2 =
  [((Secret, 1, 1), sfalse);((Secret, 3, 1), strue)];;

(* corrupt inputs and output *)
let d2 = 
  [((Secret, 2, 1), sfalse);((View, 0, 4), strue)];;

(* corrupt inputs and views and output *)
let d2' = 
  [((Secret, 2, 1), sfalse);((View, 2, 0), strue);((View, 2, 1), strue);((View, 0, 4), strue)];;

(* ideal knowledge is that honest parties hold either 1,0 or 0,1, so this is .5 *)
let pdf2 = jpdf e2 vs2 in marg_dist i2 d2 pdf2;;

(* 
   Bug revealed. Given additional information leaked through the corrupt view, the adversary
   now knows for sure the honest secrets- this is 1.0 
*)
let pdf2 = jpdf e2 vs2 in marg_dist i2 d2' pdf2;;

(* this is false! It is a failure witness. *)
let pdf2 = jpdf e2 vs2 in marg_dist i2 d2 pdf2 = marg_dist i2 d2' pdf2;;


(* GARBLED CIRCUITS over binary field. *)

(* Garbled circuits with OT of table entry. This is passive secure. *)

(*
x[2,0] := flip[2,0];
x[2,1] := not flip[2,0];

x[2,10] := flip[2,1];
x[2,11] := not flip[2,1];

x[2,220] := flip[2,2];
x[2,221] := not flip[2,2];

x[2,120] := flip[2,3];
x[2,121] := not flip[2,3];

x[2,100] := x[2,0] xor x[2,10] xor false;
x[2,101] := x[2,0] xor x[2,11] xor false;
x[2,110] := x[2,1] xor x[2,10] xor false;
x[2,111] := x[2,1] xor x[2,11] xor true;

v[1,20] := select(s[2,0],x[2,1],x[2,0]);
v[1,21] := select(s[2,0],x[2,221],x[2,220]);

v[1,10] := select(s[1,0],x[2,11],x[2,10]);
v[1,11] := select(s[1,0],x[2,121],x[2,120]);

x[2,40] :=
 (x[2,120] and x[2,220] and x[2,100]) xor
 (x[2,120] and x[2,221] and x[2,101]) xor
 (x[2,121] and x[2,220] and x[2,110]) xor
 (x[2,121] and x[2,221] and x[2,111]);
x[2,41] :=
 (x[2,120] and not x[2,220] and x[2,100]) xor
 (x[2,120] and not x[2,221] and x[2,101]) xor
 (x[2,121] and not x[2,220] and x[2,110]) xor
 (x[2,121] and not x[2,221] and x[2,111]);
x[2,42] :=
 (not x[2,120] and x[2,220] and x[2,100]) xor
 (not x[2,120] and x[2,221] and x[2,101]) xor
 (not x[2,121] and x[2,220] and x[2,110]) xor
 (not x[2,121] and x[2,221] and x[2,111]);
x[2,43] :=
 (not x[2,120] and not x[2,220] and x[2,100]) xor
 (not x[2,120] and not x[2,221] and x[2,101]) xor
 (not x[2,121] and not x[2,220] and x[2,110]) xor
 (not x[2,121] and not x[2,221] and x[2,111]);

v[1,100] := select(v[1,11],select(v[1,21],x[2,40],x[2,41]),select(v[1,21],x[2,42],x[2,43]));

v[0,0] := v[1,10] xor v[1,20] xor v[1,100];;
*)

let gc_ot =
(Seq(Assign((Local, 2, 0), Var(Flip, 2, 0)),
Seq(Assign((Local, 2, 1), Not(Var(Flip, 2, 0))),
Seq(Assign((Local, 2, 10), Var(Flip, 2, 1)),
Seq(Assign((Local, 2, 11), Not(Var(Flip, 2, 1))),
Seq(Assign((Local, 2, 220), Var(Flip, 2, 2)),
Seq(Assign((Local, 2, 221), Not(Var(Flip, 2, 2))),
Seq(Assign((Local, 2, 120), Var(Flip, 2, 3)),
Seq(Assign((Local, 2, 121), Not(Var(Flip, 2, 3))),
Seq(Assign((Local, 2, 100), Xor(Var(Local, 2, 0), Xor(Var(Local, 2, 10), Bool(false)))),
Seq(Assign((Local, 2, 101), Xor(Var(Local, 2, 0), Xor(Var(Local, 2, 11), Bool(false)))),
Seq(Assign((Local, 2, 110), Xor(Var(Local, 2, 1), Xor(Var(Local, 2, 10), Bool(false)))),
Seq(Assign((Local, 2, 111), Xor(Var(Local, 2, 1), Xor(Var(Local, 2, 11), Bool(true)))),
Seq(Assign((View, 1, 20), Select(Var(Secret, 2, 0), Var(Local, 2, 1), Var(Local, 2, 0))),
Seq(Assign((View, 1, 21), Select(Var(Secret, 2, 0), Var(Local, 2, 221), Var(Local, 2, 220))),
Seq(Assign((View, 1, 10), Select(Var(Secret, 1, 0), Var(Local, 2, 11), Var(Local, 2, 10))),
Seq(Assign((View, 1, 11), Select(Var(Secret, 1, 0), Var(Local, 2, 121), Var(Local, 2, 120))),
Seq(Assign((Local, 2, 40), Xor(And(Var(Local, 2, 120), And(Var(Local, 2, 220), Var(Local, 2, 100))), Xor(And(Var(Local, 2, 120), And(Var(Local, 2, 221), Var(Local, 2, 101))), Xor(And(Var(Local, 2, 121), And(Var(Local, 2, 220), Var(Local, 2, 110))), And(Var(Local, 2, 121), And(Var(Local, 2, 221), Var(Local, 2, 111))))))),
Seq(Assign((Local, 2, 41), Xor(And(Var(Local, 2, 120), And(Not(Var(Local, 2, 220)), Var(Local, 2, 100))), Xor(And(Var(Local, 2, 120), And(Not(Var(Local, 2, 221)), Var(Local, 2, 101))), Xor(And(Var(Local, 2, 121), And(Not(Var(Local, 2, 220)), Var(Local, 2, 110))), And(Var(Local, 2, 121), And(Not(Var(Local, 2, 221)), Var(Local, 2, 111))))))),
Seq(Assign((Local, 2, 42), Xor(And(Not(Var(Local, 2, 120)), And(Var(Local, 2, 220), Var(Local, 2, 100))), Xor(And(Not(Var(Local, 2, 120)), And(Var(Local, 2, 221), Var(Local, 2, 101))), Xor(And(Not(Var(Local, 2, 121)), And(Var(Local, 2, 220), Var(Local, 2, 110))), And(Not(Var(Local, 2, 121)), And(Var(Local, 2, 221), Var(Local, 2, 111))))))),
Seq(Assign((Local, 2, 43), Xor(And(Not(Var(Local, 2, 120)), And(Not(Var(Local, 2, 220)), Var(Local, 2, 100))), Xor(And(Not(Var(Local, 2, 120)), And(Not(Var(Local, 2, 221)), Var(Local, 2, 101))), Xor(And(Not(Var(Local, 2, 121)), And(Not(Var(Local, 2, 220)), Var(Local, 2, 110))), And(Not(Var(Local, 2, 121)), And(Not(Var(Local, 2, 221)), Var(Local, 2, 111))))))),
Seq(Assign((View, 1, 100), Select(Var(View, 1, 11), Select(Var(View, 1, 21), Var(Local, 2, 40), Var(Local, 2, 41)), Select(Var(View, 1, 21), Var(Local, 2, 42), Var(Local, 2, 43)))),
Assign((View, 0, 0), Xor(Var(View, 1, 10), Xor(Var(View, 1, 20), Var(View, 1, 100))))))))))))))))))))))))));;

(* This is true. *)
passive_secure gc_ot 2 (View,0,0);;

(* Demonstrating correct output... *)

(* P(s[2,0] = 1 | s[1,0] = 0 | v[0,0] = 0) = .5 *)
marg_dist [((Secret,2,0),strue)] [((Secret,1,0),sfalse);((View,0,0),sfalse)] (genpdf gc_ot);;

(* P(s[2,0] = 1 | s[1,0] = 0 | v[0,0] = 0) = .333333... *)
marg_dist [((Secret,2,0),strue);((Secret,1,0),sfalse)] [((View,0,0),sfalse)] (genpdf gc_ot);;


(* Garbled circuits where full table is communicated. This version is not passive
   secure, some information is leaked with full garbled table view. *)

(*
x[2,0] := flip[2,0];
x[2,1] := not flip[2,0];

x[2,10] := flip[2,1];
x[2,11] := not flip[2,1];

x[2,220] := flip[2,2];
x[2,221] := not flip[2,2];

x[2,120] := flip[2,3];
x[2,121] := not flip[2,3];

x[2,100] := x[2,0] xor x[2,10] xor false;
x[2,101] := x[2,0] xor x[2,11] xor false;
x[2,110] := x[2,1] xor x[2,10] xor false;
x[2,111] := x[2,1] xor x[2,11] xor true;

v[1,20] := select(s[2,0],x[2,1],x[2,0]);
v[1,21] := select(s[2,0],x[2,221],x[2,220]);

v[1,10] := select(s[1,0],x[2,11],x[2,10]);
v[1,11] := select(s[1,0],x[2,121],x[2,120]);

v[1,40] :=
 (x[2,120] and x[2,220] and x[2,100]) xor
 (x[2,120] and x[2,221] and x[2,101]) xor
 (x[2,121] and x[2,220] and x[2,110]) xor
 (x[2,121] and x[2,221] and x[2,111]);
v[1,41] :=
 (x[2,120] and not x[2,220] and x[2,100]) xor
 (x[2,120] and not x[2,221] and x[2,101]) xor
 (x[2,121] and not x[2,220] and x[2,110]) xor
 (x[2,121] and not x[2,221] and x[2,111]);
v[1,42] :=
 (not x[2,120] and x[2,220] and x[2,100]) xor
 (not x[2,120] and x[2,221] and x[2,101]) xor
 (not x[2,121] and x[2,220] and x[2,110]) xor
 (not x[2,121] and x[2,221] and x[2,111]);
v[1,43] :=
 (not x[2,120] and not x[2,220] and x[2,100]) xor
 (not x[2,120] and not x[2,221] and x[2,101]) xor
 (not x[2,121] and not x[2,220] and x[2,110]) xor
 (not x[2,121] and not x[2,221] and x[2,111]);

v[1,100] := select(v[1,11],select(v[1,21],v[1,40],v[1,41]),select(v[1,21],v[1,42],v[1,43]));

v[0,0] := v[1,10] xor v[1,20] xor v[1,100];;
*)

let gc_fail =
(Seq(Assign((Local, 2, 0), Var(Flip, 2, 0)),
Seq(Assign((Local, 2, 1), Not(Var(Flip, 2, 0))),
Seq(Assign((Local, 2, 10), Var(Flip, 2, 1)),
Seq(Assign((Local, 2, 11), Not(Var(Flip, 2, 1))),
Seq(Assign((Local, 2, 220), Var(Flip, 2, 2)),
Seq(Assign((Local, 2, 221), Not(Var(Flip, 2, 2))),
Seq(Assign((Local, 2, 120), Var(Flip, 2, 3)),
Seq(Assign((Local, 2, 121), Not(Var(Flip, 2, 3))),
Seq(Assign((Local, 2, 100), Xor(Var(Local, 2, 0), Xor(Var(Local, 2, 10), Bool(false)))),
Seq(Assign((Local, 2, 101), Xor(Var(Local, 2, 0), Xor(Var(Local, 2, 11), Bool(false)))),
Seq(Assign((Local, 2, 110), Xor(Var(Local, 2, 1), Xor(Var(Local, 2, 10), Bool(false)))),
Seq(Assign((Local, 2, 111), Xor(Var(Local, 2, 1), Xor(Var(Local, 2, 11), Bool(true)))),
Seq(Assign((View, 1, 20), Select(Var(Secret, 2, 0), Var(Local, 2, 1), Var(Local, 2, 0))),
Seq(Assign((View, 1, 21), Select(Var(Secret, 2, 0), Var(Local, 2, 221), Var(Local, 2, 220))),
Seq(Assign((View, 1, 10), Select(Var(Secret, 1, 0), Var(Local, 2, 11), Var(Local, 2, 10))),
Seq(Assign((View, 1, 11), Select(Var(Secret, 1, 0), Var(Local, 2, 121), Var(Local, 2, 120))),
Seq(Assign((View, 1, 40), Xor(And(Var(Local, 2, 120), And(Var(Local, 2, 220), Var(Local, 2, 100))), Xor(And(Var(Local, 2, 120), And(Var(Local, 2, 221), Var(Local, 2, 101))), Xor(And(Var(Local, 2, 121), And(Var(Local, 2, 220), Var(Local, 2, 110))), And(Var(Local, 2, 121), And(Var(Local, 2, 221), Var(Local, 2, 111))))))),
Seq(Assign((View, 1, 41), Xor(And(Var(Local, 2, 120), And(Not(Var(Local, 2, 220)), Var(Local, 2, 100))), Xor(And(Var(Local, 2, 120), And(Not(Var(Local, 2, 221)), Var(Local, 2, 101))), Xor(And(Var(Local, 2, 121), And(Not(Var(Local, 2, 220)), Var(Local, 2, 110))), And(Var(Local, 2, 121), And(Not(Var(Local, 2, 221)), Var(Local, 2, 111))))))),
Seq(Assign((View, 1, 42), Xor(And(Not(Var(Local, 2, 120)), And(Var(Local, 2, 220), Var(Local, 2, 100))), Xor(And(Not(Var(Local, 2, 120)), And(Var(Local, 2, 221), Var(Local, 2, 101))), Xor(And(Not(Var(Local, 2, 121)), And(Var(Local, 2, 220), Var(Local, 2, 110))), And(Not(Var(Local, 2, 121)), And(Var(Local, 2, 221), Var(Local, 2, 111))))))),
Seq(Assign((View, 1, 43), Xor(And(Not(Var(Local, 2, 120)), And(Not(Var(Local, 2, 220)), Var(Local, 2, 100))), Xor(And(Not(Var(Local, 2, 120)), And(Not(Var(Local, 2, 221)), Var(Local, 2, 101))), Xor(And(Not(Var(Local, 2, 121)), And(Not(Var(Local, 2, 220)), Var(Local, 2, 110))), And(Not(Var(Local, 2, 121)), And(Not(Var(Local, 2, 221)), Var(Local, 2, 111))))))),
Seq(Assign((View, 1, 100), Select(Var(View, 1, 11), Select(Var(View, 1, 21), Var(View, 1, 40), Var(View, 1, 41)), Select(Var(View, 1, 21), Var(View, 1, 42), Var(View, 1, 43)))),
Assign((View, 0, 0), Xor(Var(View, 1, 10), Xor(Var(View, 1, 20), Var(View, 1, 100))))))))))))))))))))))))));;

(* This is false. *)
passive_secure gc_fail 2 (View,0,0);;

(* Failure (leakage) witness. *)
let gc_fail_witness =
([((Secret, 2, 0), "0")],
 [((Secret, 1, 0), "0"); ((View, 1, 10), "1"); ((View, 1, 11), "1");
  ((View, 1, 20), "1"); ((View, 1, 21), "1"); ((View, 1, 40), "0");
  ((View, 1, 41), "1"); ((View, 1, 42), "1"); ((View, 1, 43), "1");
  ((View, 1, 100), "0"); ((View, 0, 0), "0")]);;

(* Demonstrating correct output... *)

(* P(s[2,0] = 1 | s[1,0] = 0 | v[0,0] = 0) = .5 *)
marg_dist [((Secret,2,0),strue)] [((Secret,1,0),sfalse);((View,0,0),sfalse)] (genpdf gc_fail);;

(* P(s[2,0] = 1 | s[1,0] = 0 | v[0,0] = 0) = .333333... *)
marg_dist [((Secret,2,0),strue);((Secret,1,0),sfalse)] [((View,0,0),sfalse)] (genpdf gc_fail);;

