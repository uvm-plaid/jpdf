(*
     
   Here (f, [(x1,t1),...,(xn,tn)], t, e) : GenFn([t1;...;tn],t) means:
 
    f(x1 : t1, ..., xn tn) : t { e } :  \forall ftv(t1,...,tn) . \Pi fsv(t1,...,tn) . (t1 * ... * tn) -> t
  
   with the restriction that any string variables occuring in t1...tn are not concatenated 
   nor occur within a jpdf type.

   Example:

   f(x : jpdf('a, 'b), s : string('s))
   {
      H[s || "foo"] and x
   }

     :

   \forall 'a, 'b, \Pi 's . 
        jpdf('a, 'b), s : string('s) -> jpdf ((H[s || "foo"]|T meet 'a, (H[s || "foo"]|F join 'b)))

   f(H["bar"],"baz") :  jpdf ((H["bazfoo"]|T meet H["bar"]|T, (H["bazfoo"]|F join H["bar"]|F)))
 *)


let (ex1 : progn) =
  [
  (Fname("f"),
   [(TVar("x"), Jpdf(DVar("a"),DVar("b"))); (SVar("s"), StringTy(Var(SVar("s"))))],
   And(H(Concat(Var(SVar("s")), String("foo"))), Var(TVar("x")))
  )
  ], Assign(V(Cid(0),String("pub")), Appl(Fname("f"), [F(Cid(1), String("bar")); String("baz")]));;


marg_dist [(V(Cid(0),String("pub")),strue)] [(H(String("bazfoo")),sfalse)] (genpdf ex1);;

(*

share3(client : cid('s1), secretid : string('s2))
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

let ex2 =
(
[
(
Fname("share3"),
[
(SVar("client"), CidTy(Var(SVar("s1"))));
(SVar("secretid"), StringTy(Var(SVar("s2"))))
],
Let(TVar("s1"), F(Var(SVar("client")), String("share 1")),
Let(TVar("s2"), F(Var(SVar("client")), String("share 2")),
Let(TVar("s3"), Xor(Xor(Var(TVar("s1")), Var(TVar("s2"))), S(Var(SVar("client")), Var(SVar("secretid")))),
Record([("s1",Var(TVar("s1"))); ("s2",Var(TVar("s2"))); ("s3",Var(TVar("s3")))]))))
)
],
Let(TVar("shares1"), (Appl(Fname("share3"), [Cid(1);String("secret")])),
Let(TVar("shares2"), (Appl(Fname("share3"), [Cid(2);String("secret")])),
Let(TVar("shares3"), (Appl(Fname("share3"), [Cid(3);String("secret")])),  
Seq(Assign(V(Cid(1),String("0")), Dot(Var(TVar("shares2")), "s1")),
Seq(Assign(V(Cid(1),String("1")), Dot(Var(TVar("shares3")), "s1")),
Seq(Assign(V(Cid(2),String("0")), Dot(Var(TVar("shares1")), "s2")),
Seq(Assign(V(Cid(2),String("1")), Dot(Var(TVar("shares3")), "s2")),
Seq(Assign(V(Cid(3),String("0")), Dot(Var(TVar("shares1")), "s3")),
Seq(Assign(V(Cid(3),String("1")), Dot(Var(TVar("shares2")), "s3")),
Seq(Assign(V(Cid(0),String("1")),
  Xor(Xor(Dot(Var(TVar("shares1")), "s1"),V(Cid(1),String("0"))),V(Cid(1),String("1")))),
Seq(Assign(V(Cid(0),String("2")),
  Xor(Xor(Dot(Var(TVar("shares2")), "s2"),V(Cid(2),String("0"))),V(Cid(2),String("1")))),
Seq(Assign(V(Cid(0),String("3")),
  Xor(Xor(Dot(Var(TVar("shares3")), "s3"),V(Cid(3),String("0"))),V(Cid(3),String("1")))),
Assign(V(Cid(0),String("pub")),
  Xor(Xor(V(Cid(0),String("1")),V(Cid(0),String("2"))),V(Cid(0),String("3"))))))))))))))))
)

passive_secure ex2 3 (V(Cid(0),String("pub")));;

marg_dist [(S(Cid(1),String("secret")),strue);
	   (S(Cid(2),String("secret")),sfalse);
	   (S(Cid(3),String("secret")),sfalse);]
	  [(V(Cid(0),String("pub")),strue)]
	  (genpdf ex2);;


marg_dist [(S(Cid(2),String("secret")),strue)] [(V(Cid(0),String("pub")),strue)] (genpdf ex2);;
