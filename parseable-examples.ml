(* Commented portion is input, corresponding ocaml portion is parser output *)

(* two party secret sharing *)

(*
v[1,"0"] := flip[2,"0"] xor s[2,"0"];
v[2,"0"] := flip[1,"0"] xor s[1,"0"];
v[0,"1"] := v[1,"0"] xor flip[1,"0"];
v[0,"2"] := v[2,"0"] xor flip[2,"0"];
v[0,"pub"] := v[0,"1"] xor v[0,"2"]
*)

let (ex0 : progn) =
(
[

],
(Seq((Assign((V((Cid(1)),(String("0")))),(Xor((F((Cid(2)),(String("0")))),(S((Cid(2)),(String("0")))))))),(
(Seq((Assign((V((Cid(2)),(String("0")))),(Xor((F((Cid(1)),(String("0")))),(S((Cid(1)),(String("0")))))))),(
(Seq((Assign((V((Cid(0)),(String("1")))),(Xor((V((Cid(1)),(String("0")))),(F((Cid(1)),(String("0")))))))),(
(Seq((Assign((V((Cid(0)),(String("2")))),(Xor((V((Cid(2)),(String("0")))),(F((Cid(2)),(String("0")))))))),((Assign((V((Cid(0)),(String("pub")))),(Xor((V((Cid(0)),(String("1")))),(V((Cid(0)),(String("2"))))))))))))))))))))
);;

(* 3 party secret sharing (this works and passive_secure = true) *)

(* 
share3(client : cid(client), secretid : string(sid))
{
  let s1 = flip[client, "share1"] in
  let s2 = flip[client, "share2"] in
  let s3 = (s1 xor s2) xor s[client, secretid] in
  {s1 = s1;s2 = s2;s3 = s3}
}

let shares1 = share3(1, "secret") in
let shares2 = share3(2, "secret") in
let shares3 = share3(3, "secret") in
v[1,"0"] := shares2.s1;
v[1,"1"] := shares3.s1;
v[2,"0"] := shares1.s2;
v[2,"1"] := shares3.s2;
v[3,"0"] := shares1.s3;
v[3,"1"] := shares2.s3;
v[0,"1"] := (shares1.s1 xor v[1,"0"]) xor v[1,"1"];
v[0,"2"] := (shares2.s2 xor v[2,"0"]) xor v[2,"1"];
v[0,"3"] := (shares3.s3 xor v[3,"0"]) xor v[3,"1"];
v[0,"pub"] := (v[0,"1"] xor v[0,"2"]) xor v[0,"3"]   
*)

(
[
((Fname("share3")),[((EVar("client")),(CidTy((Var((EVar("client")))))));((EVar("secretid")),(StringTy((Var((EVar("sid")))))))],(
(Let((EVar("s1")),(F((Var((EVar("client")))),(String("share1")))),(
(Let((EVar("s2")),(F((Var((EVar("client")))),(String("share2")))),(
(Let((EVar("s3")),(Xor((Xor((Var((EVar("s1")))),(Var((EVar("s2")))))),(S((Var((EVar("client")))),(Var((EVar("secretid")))))))),((Record([(("s1"),(Var((EVar("s1")))));(("s2"),(Var((EVar("s2")))));(("s3"),(Var((EVar("s3")))))])))))))))))))
],
(Let((EVar("shares1")),(Appl((Fname("share3")),[(Cid(1));(String("secret"))])),(
(Let((EVar("shares2")),(Appl((Fname("share3")),[(Cid(2));(String("secret"))])),(
(Let((EVar("shares3")),(Appl((Fname("share3")),[(Cid(3));(String("secret"))])),(
(Seq((Assign((V((Cid(1)),(String("0")))),(Dot((Var((EVar("shares2")))),("s1"))))),(
(Seq((Assign((V((Cid(1)),(String("1")))),(Dot((Var((EVar("shares3")))),("s1"))))),(
(Seq((Assign((V((Cid(2)),(String("0")))),(Dot((Var((EVar("shares1")))),("s2"))))),(
(Seq((Assign((V((Cid(2)),(String("1")))),(Dot((Var((EVar("shares3")))),("s2"))))),(
(Seq((Assign((V((Cid(3)),(String("0")))),(Dot((Var((EVar("shares1")))),("s3"))))),(
(Seq((Assign((V((Cid(3)),(String("1")))),(Dot((Var((EVar("shares2")))),("s3"))))),(
(Seq((Assign((V((Cid(0)),(String("1")))),(Xor((Xor((Dot((Var((EVar("shares1")))),("s1"))),(V((Cid(1)),(String("0")))))),(V((Cid(1)),(String("1")))))))),(
(Seq((Assign((V((Cid(0)),(String("2")))),(Xor((Xor((Dot((Var((EVar("shares2")))),("s2"))),(V((Cid(2)),(String("0")))))),(V((Cid(2)),(String("1")))))))),(
(Seq((Assign((V((Cid(0)),(String("3")))),(Xor((Xor((Dot((Var((EVar("shares3")))),("s3"))),(V((Cid(3)),(String("0")))))),(V((Cid(3)),(String("1")))))))),((Assign((V((Cid(0)),(String("pub")))),(Xor((Xor((V((Cid(0)),(String("1")))),(V((Cid(0)),(String("2")))))),(V((Cid(0)),(String("3"))))))))))))))))))))))))))))))))))))))))))))
);;

(* garbled and using select4 (this is bugged and passive_secure = false) *)

(* 
select4(b1 : jpd('a),b2 : jpd('b),x1 : jpd('a1),x2 : jpd('a2),x3 : jpd('a3),x4 : jpd('a4))
{
  select[b1,select[b2,x1,x2],select[b2,x3,x4]]
}

let wla = ({k0 = flip[2,"ka0"];k1 = flip[2,"ka1"];p0 = flip[2,"pa0"];p1 = not flip[2,"pa0"]}) in
let wlb = ({k0 = flip[2,"kb0"];k1 = flip[2,"kb1"];p0 = flip[2,"pb0"];p1 = not flip[2,"pb0"]}) in
let k00 = select4(wla.k0,wlb.k0,H["1"],H["2"],H["3"],H["4"]) in
let k10 = select4(wla.k0,wlb.k1,H["1"],H["2"],H["3"],H["4"]) in
let k01 = select4(wla.k1,wlb.k0,H["1"],H["2"],H["3"],H["4"]) in
let k11 = select4(wla.k1,wlb.k1,H["1"],H["2"],H["3"],H["4"]) in
let r00 = k00 xor false in
let r10 = k10 xor false in
let r01 = k01 xor false in
let r11 = k11 xor true in
let gr11 = (((((wla.p0 and wlb.p0) and r00) xor ((wla.p1 and wlb.p0) and r10)) xor ((wla.p0 and wlb.p1) and r01)) xor ((wla.p1 and wlb.p1) and r11)) in
let gr10 = (((((wla.p0 and (not wlb.p0)) and r00) xor ((wla.p1 and (not wlb.p0)) and r10)) xor ((wla.p0 and (not wlb.p1)) and r01)) xor ((wla.p1 and (not wlb.p1)) and r11)) in
let gr01 = ((((((not wla.p0) and wlb.p0) and r00) xor (((not wla.p1) and wlb.p0) and r10)) xor (((not wla.p0) and wlb.p1) and r01)) xor (((not wla.p1) and wlb.p1) and r11)) in
let gr00 = ((((((not wla.p0) and (not wlb.p0)) and r00)) xor (((not wla.p1) and (not wlb.p0)) and r10) xor (((not wla.p0) and (not wlb.p1)) and r01)) xor (((not wla.p1) and (not wlb.p1)) and r11)) in
v[1,"r1"] := gr00;
v[1,"r2"] := gr01;
v[1,"r3"] := gr10;
v[1,"r4"] := gr11;
v[1,"p1Key"] := OT[s[1,"0"],wla.k1,wla.k0];
v[1,"p1Ptr"] := OT[s[1,"0"],wla.p1,wla.p0];
v[1,"p2Key"] := select[s[2,"0"],wlb.k1,wlb.k0];
v[1,"p2Ptr"] := select[s[2,"0"],wlb.p1,wlb.p0];
v[0,"output"] := select4(v[1,"p1Key"],v[1,"p2Key"],H["1"],H["2"],H["3"],H["4"]) xor select4(v[1,"p1Ptr"],v[1,"p2Ptr"],v[1,"r1"],v[1,"r2"],v[1,"r3"], v[1,"r4"])   
*)

(
[
((Fname("select4")),[((EVar("b1")),(Jpdf((DVar("a")))));((EVar("b2")),(Jpdf((DVar("b")))));((EVar("x1")),(Jpdf((DVar("a1")))));((EVar("x2")),(Jpdf((DVar("a2")))));((EVar("x3")),(Jpdf((DVar("a3")))));((EVar("x4")),(Jpdf((DVar("a4")))))],((Select((Var((EVar("b1")))),(Select((Var((EVar("b2")))),(Var((EVar("x1")))),(Var((EVar("x2")))))),(Select((Var((EVar("b2")))),(Var((EVar("x3")))),(Var((EVar("x4"))))))))))
],
(Let((EVar("wla")),(Record([(("k0"),(F((Cid(2)),(String("ka0")))));(("k1"),(F((Cid(2)),(String("ka1")))));(("p0"),(F((Cid(2)),(String("pa0")))));(("p1"),(Not((F((Cid(2)),(String("pa0")))))))])),(
(Let((EVar("wlb")),(Record([(("k0"),(F((Cid(2)),(String("kb0")))));(("k1"),(F((Cid(2)),(String("kb1")))));(("p0"),(F((Cid(2)),(String("pb0")))));(("p1"),(Not((F((Cid(2)),(String("pb0")))))))])),(
(Let((EVar("k00")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k0")));(Dot((Var((EVar("wlb")))),("k0")));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),(
(Let((EVar("k10")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k0")));(Dot((Var((EVar("wlb")))),("k1")));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),(
(Let((EVar("k01")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k1")));(Dot((Var((EVar("wlb")))),("k0")));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),(
(Let((EVar("k11")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k1")));(Dot((Var((EVar("wlb")))),("k1")));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),(
(Let((EVar("r00")),(Xor((Var((EVar("k00")))),(Bool(false)))),(
(Let((EVar("r10")),(Xor((Var((EVar("k10")))),(Bool(false)))),(
(Let((EVar("r01")),(Xor((Var((EVar("k01")))),(Bool(false)))),(
(Let((EVar("r11")),(Xor((Var((EVar("k11")))),(Bool(true)))),(
(Let((EVar("gr11")),(Xor((Xor((Xor((And((And((Dot((Var((EVar("wla")))),("p0"))),(Dot((Var((EVar("wlb")))),("p0"))))),(Var((EVar("r00")))))),(And((And((Dot((Var((EVar("wla")))),("p1"))),(Dot((Var((EVar("wlb")))),("p0"))))),(Var((EVar("r10")))))))),(And((And((Dot((Var((EVar("wla")))),("p0"))),(Dot((Var((EVar("wlb")))),("p1"))))),(Var((EVar("r01")))))))),(And((And((Dot((Var((EVar("wla")))),("p1"))),(Dot((Var((EVar("wlb")))),("p1"))))),(Var((EVar("r11")))))))),(
(Let((EVar("gr10")),(Xor((Xor((Xor((And((And((Dot((Var((EVar("wla")))),("p0"))),(Not((Dot((Var((EVar("wlb")))),("p0"))))))),(Var((EVar("r00")))))),(And((And((Dot((Var((EVar("wla")))),("p1"))),(Not((Dot((Var((EVar("wlb")))),("p0"))))))),(Var((EVar("r10")))))))),(And((And((Dot((Var((EVar("wla")))),("p0"))),(Not((Dot((Var((EVar("wlb")))),("p1"))))))),(Var((EVar("r01")))))))),(And((And((Dot((Var((EVar("wla")))),("p1"))),(Not((Dot((Var((EVar("wlb")))),("p1"))))))),(Var((EVar("r11")))))))),(
(Let((EVar("gr01")),(Xor((Xor((Xor((And((And((Not((Dot((Var((EVar("wla")))),("p0"))))),(Dot((Var((EVar("wlb")))),("p0"))))),(Var((EVar("r00")))))),(And((And((Not((Dot((Var((EVar("wla")))),("p1"))))),(Dot((Var((EVar("wlb")))),("p0"))))),(Var((EVar("r10")))))))),(And((And((Not((Dot((Var((EVar("wla")))),("p0"))))),(Dot((Var((EVar("wlb")))),("p1"))))),(Var((EVar("r01")))))))),(And((And((Not((Dot((Var((EVar("wla")))),("p1"))))),(Dot((Var((EVar("wlb")))),("p1"))))),(Var((EVar("r11")))))))),(
(Let((EVar("gr00")),(Xor((Xor((And((And((Not((Dot((Var((EVar("wla")))),("p0"))))),(Not((Dot((Var((EVar("wlb")))),("p0"))))))),(Var((EVar("r00")))))),(Xor((And((And((Not((Dot((Var((EVar("wla")))),("p1"))))),(Not((Dot((Var((EVar("wlb")))),("p0"))))))),(Var((EVar("r10")))))),(And((And((Not((Dot((Var((EVar("wla")))),("p0"))))),(Not((Dot((Var((EVar("wlb")))),("p1"))))))),(Var((EVar("r01")))))))))),(And((And((Not((Dot((Var((EVar("wla")))),("p1"))))),(Not((Dot((Var((EVar("wlb")))),("p1"))))))),(Var((EVar("r11")))))))),(
(Seq((Assign((V((Cid(1)),(String("r1")))),(Var((EVar("gr00")))))),(
(Seq((Assign((V((Cid(1)),(String("r2")))),(Var((EVar("gr01")))))),(
(Seq((Assign((V((Cid(1)),(String("r3")))),(Var((EVar("gr10")))))),(
(Seq((Assign((V((Cid(1)),(String("r4")))),(Var((EVar("gr11")))))),(
(Seq((Assign((V((Cid(1)),(String("p1Key")))),(OT((S((Cid(1)),(String("0")))),(Dot((Var((EVar("wla")))),("k1"))),(Dot((Var((EVar("wla")))),("k0"))))))),(
(Seq((Assign((V((Cid(1)),(String("p1Ptr")))),(OT((S((Cid(1)),(String("0")))),(Dot((Var((EVar("wla")))),("p1"))),(Dot((Var((EVar("wla")))),("p0"))))))),(
(Seq((Assign((V((Cid(1)),(String("p2Key")))),(Select((S((Cid(2)),(String("0")))),(Dot((Var((EVar("wlb")))),("k1"))),(Dot((Var((EVar("wlb")))),("k0"))))))),(
(Seq((Assign((V((Cid(1)),(String("p2Ptr")))),(Select((S((Cid(2)),(String("0")))),(Dot((Var((EVar("wlb")))),("p1"))),(Dot((Var((EVar("wlb")))),("p0"))))))),((Assign((V((Cid(0)),(String("output")))),(Xor((Appl((Fname("select4")),[(V((Cid(1)),(String("p1Key"))));(V((Cid(1)),(String("p2Key"))));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),(Appl((Fname("select4")),[(V((Cid(1)),(String("p1Ptr"))));(V((Cid(1)),(String("p2Ptr"))));(V((Cid(1)),(String("r1"))));(V((Cid(1)),(String("r2"))));(V((Cid(1)),(String("r3"))));(V((Cid(1)),(String("r4"))))]))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))))
);;

(* Oblivious Transfer *)
(* secrets: s[1, "m0"] is message 0, s[1, "m1"] is message 1, s[2, "c"] is choice bit *)
(* use oracle for random strings r0 and r1 since both sender and receiver need to access them *)
(* flips: flip[2, "d"] since only receiver needs it to choose one of random strings, and send to sender choice bit xor d *)

(* 
v[2, "rd"] := select[flip[2, "d"], H["r1"], H["r0"]];
v[1, "e"] := s[2, "c"] xor flip[2, "d"];
v[2, "f0"] := s[1, "m0"] xor select[v[1, "e"], H["r1"], H["r0"]];
v[2, "f1"] := s[1, "m1"] xor select[not v[1, "e"], H["r1"], H["r0"]];
v[2, "mc"] := select[s[2, "c"], v[2, "f1"], v[2, "f0"]] xor v[2, "rd"]
*)

(
[

],
(Seq((Assign((V((Cid(2)),(String("rd")))),(Select((F((Cid(2)),(String("d")))),(H((String("r1")))),(H((String("r0")))))))),(
(Seq((Assign((V((Cid(1)),(String("e")))),(Xor((S((Cid(2)),(String("c")))),(F((Cid(2)),(String("d")))))))),(
(Seq((Assign((V((Cid(2)),(String("f0")))),(Xor((S((Cid(1)),(String("m0")))),(Select((V((Cid(1)),(String("e")))),(H((String("r1")))),(H((String("r0")))))))))),(
(Seq((Assign((V((Cid(2)),(String("f1")))),(Xor((S((Cid(1)),(String("m1")))),(Select((Not((V((Cid(1)),(String("e")))))),(H((String("r1")))),(H((String("r0")))))))))),(
(Assign((V((Cid(2)),(String("mc")))),(Xor((Select((S((Cid(2)),(String("c")))),(V((Cid(2)),(String("f1")))),(V((Cid(2)),(String("f0")))))),(V((Cid(2)),(String("rd"))))))))))))))))))))
);;