(* 
   We define the following language of expressions e that models multi-party 
   computations over the binary field.

   e ::= true | false | lab[n,n] | v[n,n] := e | e;e | not e | e and e
   | e or e | e xor e lab ::= s | v | flip

   Identifiers are denoted lab[n1,n2], where n1 is the party id and n2
   is the index, and lab is s if its a secret, v if it's a view, and
   flip if it's boolean value in a uniform random distribution. Only
   views can be assigned to, indicating communication between
   parties. We require that views are assigned to at most once.

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

   Thus, to show that a protocol is secure, we need to show:

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

   In the following we illustrate these ideas with a 3-party additive sharing protocol.
   First we define the protocol correctly, and then introduce a bug that is caught 
   by passive_secure.

 *)

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






