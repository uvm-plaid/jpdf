(*
  
  

 *)


(*

f(x : jpdf('a, 'b), s : string(s))
{
   H[s || "foo"] and x
}

v[0,"pub"] := f(flip[1,"bar"],"baz") 

*)


let (ex1 : progn) =
  [
  (Fname("f"),
   [(EVar("x"), Jpdf(DVar("a"),DVar("b"))); (EVar("s"), StringTy(Var(EVar("s"))))],
   And(H(Concat(Var(EVar("s")), String("foo"))), Var(EVar("x")))
  )
  ], Assign(V(Cid(0),String("pub")), Appl(Fname("f"), [F(Cid(1), String("bar")); String("baz")]));;

(*

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

let ex2 =
(
[
(
Fname("share3"),
[
(EVar("client"), CidTy(Var(EVar("s1"))));
(EVar("secretid"), StringTy(Var(EVar("s2"))))
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
)

