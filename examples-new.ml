(*

LAMBDA OBLIV (Figure 3 examples). We show how dependent probabilities can 
be used to precisely characterize leakage of information. 

// Example from Figure 3(a): // 

let f0 = flip[1,"0"] in
let f1 = flip[1,"1"] in
let f2 = select(s[1,"0"], f0, f1) in
v[0,"0"] := f2;
v[0,"1"] := f0   

*)

let (ex0 : progn) = 
[],
Let(EVar("f0"), F(Cid(1), String("0")),
Let(EVar("f1"), F(Cid(1), String("1")),
Let(EVar("f2"), Select(S(Cid(1), String("0")),Var(EVar("f0")),Var(EVar("f1"))),
Seq(
Assign(V(Cid(0), String("0")), Var(EVar("f2"))),
Assign(V(Cid(0), String("1")), Var(EVar("f0")))))));;

(* This reveals the leakage discussed on page 5. The first probability is 1/2, the
   second is 2/3. *)
marg_dist [(S(Cid(1), String("0")),strue)] [(V(Cid(0), String("0")),strue)] (genpdf ex0);;
marg_dist [(S(Cid(1), String("0")),strue)] [(V(Cid(0), String("0")),strue);(V(Cid(0), String("1")),strue)] (genpdf ex0);;

(* 

// Example from LAMBDA OBLIV Figure 3(b): // 

let f0 = flip[1,"0"] in
let f1 = flip[1,"1"] in
let f2 = select(f0, f0, f1) in
let f3 = select(s[1,"0"],f2,flip[1,"2"]) in
v[0,"0"] := f3

*)

let (ex1 : progn) = 
[],
Let(EVar("f0"), F(Cid(1), String("0")),
Let(EVar("f1"), F(Cid(1), String("1")),
Let(EVar("f2"), Select(Var(EVar("f0")),Var(EVar("f0")),Var(EVar("f1"))),
Let(EVar("f3"), Select(S(Cid(1), String("0")),Var(EVar("f2")),F(Cid(1), String("2"))),
Assign(V(Cid(0), String("0")), Var(EVar("f3")))))));;

(* This reveals the leakage discussed on page 6- the probability that
   the secret is 1 is higher given 1 observed in the output. *)
marg_dist [(S(Cid(1), String("0")),strue)] [] (genpdf ex1);;   
marg_dist [(S(Cid(1), String("0")),strue)] [(V(Cid(0), String("0")),strue)] (genpdf ex1);;   

(*

// Contrived example showing off features of the syntax and type system. //


f(x : jpd('a, 'b), s : string(s))
{
   H[s || "foo"] and x
}

v[0,"pub"] := f(flip[1,"bar"],"baz") 

*)


let (ex2 : progn) =
  [
  (Fname("f"),
   [(EVar("x"), Jpdf(DVar("a"),DVar("b"))); (EVar("s"), StringTy(Var(EVar("s"))))],
   And(H(Concat(Var(EVar("s")), String("foo"))), Var(EVar("x")))
  )
  ], Assign(V(Cid(0),String("pub")), Appl(Fname("f"), [F(Cid(1), String("bar")); String("baz")]));;

(* 

// 3-party additive secret sharing over the binary field: // 


share3(client : cid(client), secretid : string(sid))
{
  let s1 = flip[client, "share 1"] in
  let s2 = flip[client, "share 2"] in
  let s3 = s1 xor s2 xor s[client, secretid] in
  { s1 = s1; s2 = s2; s3 = s3 }
}

let shares1 = share3(1, "secret") in
let shares2 = share3(2, "secret") in
let shares3 = share3(3, "secret") in

v[1,0] := shares2.s1;
v[1,1] := shares3.s1;
v[2,0] := shares1.s2;
v[2,1] := shares3.s2;
v[3,0] := shares1.s3;
v[3,1] := shares2.s3;

v[0,1] := shares1.s1 xor v[1,0] xor v[1,1];
v[0,2] := shares2.s2 xor v[2,0] xor v[2,1];
v[0,3] := shares3.s3 xor v[3,0] xor v[3,1];
v[0,pub] := v[0,1] xor v[0,2] xor v[0,3] 

*)

let (ex3 : progn) =
(
[
(
Fname("share3"),
[
(EVar("client"), CidTy(Var(EVar("client"))));
(EVar("secretid"), StringTy(Var(EVar("sid"))))
],
Let(EVar("s1"), F(Var(EVar("client")), String("share 1")),
Let(EVar("s2"), F(Var(EVar("client")), String("share 2")),
Let(EVar("s3"), Xor(Xor(Var(EVar("s1")), Var(EVar("s2"))), S(Var(EVar("client")), Var(EVar("secretid")))),
Record([("s1",Var(EVar("s1"))); ("s2",Var(EVar("s2"))); ("s3",Var(EVar("s3")))]))))
)
],
Let(EVar("shares1"), (Appl(Fname("share3"), [Cid(1);String("secret")])),
Let(EVar("shares2"), (Appl(Fname("share3"), [Cid(2);String("secret")])),
Let(EVar("shares3"), (Appl(Fname("share3"), [Cid(3);String("secret")])),  
Seq(Assign(V(Cid(1),String("0")), Dot(Var(EVar("shares2")), "s1")),
Seq(Assign(V(Cid(1),String("1")), Dot(Var(EVar("shares3")), "s1")),
Seq(Assign(V(Cid(2),String("0")), Dot(Var(EVar("shares1")), "s2")),
Seq(Assign(V(Cid(2),String("1")), Dot(Var(EVar("shares3")), "s2")),
Seq(Assign(V(Cid(3),String("0")), Dot(Var(EVar("shares1")), "s3")),
Seq(Assign(V(Cid(3),String("1")), Dot(Var(EVar("shares2")), "s3")),
Seq(Assign(V(Cid(0),String("1")),
  Xor(Xor(Dot(Var(EVar("shares1")), "s1"),V(Cid(1),String("0"))),V(Cid(1),String("1")))),
Seq(Assign(V(Cid(0),String("2")),
  Xor(Xor(Dot(Var(EVar("shares2")), "s2"),V(Cid(2),String("0"))),V(Cid(2),String("1")))),
Seq(Assign(V(Cid(0),String("3")),
  Xor(Xor(Dot(Var(EVar("shares3")), "s3"),V(Cid(3),String("0"))),V(Cid(3),String("1")))),
Assign(V(Cid(0),String("pub")),
  Xor(Xor(V(Cid(0),String("1")),V(Cid(0),String("2"))),V(Cid(0),String("3"))))))))))))))))
);;

stable ex3 (V(Cid(0),String("pub")));;

(* This is true! *)
passive_secure ex3 3 (V(Cid(0),String("pub")));;


(*

// a dumb example of an unstable protocol //

v[0,"pub"] := s[0,"secret"] xor flip[1,"flip"]

*)

let ex4 = 
(
  [],
  Assign(V(Cid(0),String("pub")), Xor(S(Cid(1),String("secret")), F(Cid(1),String("flip"))))
);;

(* This is false *)
stable ex4 (V(Cid(0),String("pub")));;

(*
Notes on metatheory:

<M,e> -R->[l] <M,e>

PD(M,e) = { (R,l) | R \in tapes, <M,e> -R->[l] M' }

pd(l) = |{ (R,l) | (R,l) \in pd }| / |pd|

tapes(pd) = {R | (R,l) \in pd}

pd1 ~ pd2 <=> tapes(pd1) = tapes(pd2) and pd1(l) = pd2(l) for all l

e is stable <=> there exists unique o . for all R . (M,e) -R-> M' then out(M') = o.
We say that o = output(M,e) for stable e.

NIMO(e,C) <=>

  e is stable and (M1 =_C M2 and output(M1,e) = output(M2,e) => PD(M1,e) ~ PD(M2,e))

Theorem. If NIMO(e,C) for all C assuming |C| <= |P|/2, then PS(e).

Proof. In the real world we have all inputs M, and PD(M,e).
The simulator picks arbitrary M' =_C M with output(M',e) = output(M',e).
Since e is stable, the simulator can iterate through all random
tapes R running (M',e) with each, generating PD(M',e) which is equivalent
to PD(M,e) by assumption and definition. QED

Theorem. Given |C| <= |P|/2 and stable e:

  (prob(M|o) = prob(M|l|o) for all l with PD(M,e)(l) > 0) iff  NIMO(e,C).

*)
