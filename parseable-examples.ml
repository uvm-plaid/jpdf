(* Commented portion is input, corresponding ocaml portion is parser output *)

(* two party secret sharing *)

(*
v[1,"0"] := flip[2,"0"] xor s[2,"0"];
v[2,"0"] := flip[1,"0"] xor s[1,"0"];
v[0,"1"] := v[1,"0"] xor flip[1,"0"];
v[0,"2"] := v[2,"0"] xor flip[2,"0"];
v[0,"pub"] := (v[0,"1"] xor v[0,"2"])
*)

(
[

],
Seq((Assign((V((Cid(1)),(String("0")))),(Xor((F((Cid(2)),(String("0")))),(S((Cid(2)),(String("0")))))))),
(Seq((Assign((V((Cid(2)),(String("0")))),(Xor((F((Cid(1)),(String("0")))),(S((Cid(1)),(String("0")))))))),
(Seq((Assign((V((Cid(0)),(String("1")))),(Xor((V((Cid(1)),(String("0")))),(F((Cid(1)),(String("0")))))))),
(Seq((Assign((V((Cid(0)),(String("2")))),(Xor((V((Cid(2)),(String("0")))),(F((Cid(2)),(String("0")))))))),(Assign((V((Cid(0)),(String("pub")))),(Xor((V((Cid(0)),(String("1")))),(V((Cid(0)),(String("2")))))))))))))))
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
v[0,"pub"] := ((v[0,"1"] xor v[0,"2"]) xor v[0,"3"])   
*)

(
[
((Fname("share3")),[((EVar("client")),(CidTy((Var((EVar("client")))))));((EVar("secretid")),(StringTy((Var((EVar("sid")))))))],(
(Let((EVar("s1")),(F((Var((EVar("client")))),(String("share1")))),
(Let((EVar("s2")),(F((Var((EVar("client")))),(String("share2")))),
(Let((EVar("s3")),(Xor((Xor((Var((EVar("s1")))),(Var((EVar("s2")))))),(S((Var((EVar("client")))),(Var((EVar("secretid")))))))),(Record([(("s1"),(Var((EVar("s1")))));(("s2"),(Var((EVar("s2")))));(("s3"),(Var((EVar("s3")))))]))))))))))
],
Let((EVar("shares1")),(Appl((Fname("share3")),[(Cid(1));(String("secret"))])),
(Let((EVar("shares2")),(Appl((Fname("share3")),[(Cid(2));(String("secret"))])),
(Let((EVar("shares3")),(Appl((Fname("share3")),[(Cid(3));(String("secret"))])),
(Seq((Assign((V((Cid(1)),(String("0")))),(Dot((Var((EVar("shares2")))),("s1"))))),
(Seq((Assign((V((Cid(1)),(String("1")))),(Dot((Var((EVar("shares3")))),("s1"))))),
(Seq((Assign((V((Cid(2)),(String("0")))),(Dot((Var((EVar("shares1")))),("s2"))))),
(Seq((Assign((V((Cid(2)),(String("1")))),(Dot((Var((EVar("shares3")))),("s2"))))),
(Seq((Assign((V((Cid(3)),(String("0")))),(Dot((Var((EVar("shares1")))),("s3"))))),
(Seq((Assign((V((Cid(3)),(String("1")))),(Dot((Var((EVar("shares2")))),("s3"))))),
(Seq((Assign((V((Cid(0)),(String("1")))),(Xor((Xor((Dot((Var((EVar("shares1")))),("s1"))),(V((Cid(1)),(String("0")))))),(V((Cid(1)),(String("1")))))))),
(Seq((Assign((V((Cid(0)),(String("2")))),(Xor((Xor((Dot((Var((EVar("shares2")))),("s2"))),(V((Cid(2)),(String("0")))))),(V((Cid(2)),(String("1")))))))),
(Seq((Assign((V((Cid(0)),(String("3")))),(Xor((Xor((Dot((Var((EVar("shares3")))),("s3"))),(V((Cid(3)),(String("0")))))),(V((Cid(3)),(String("1")))))))),(Assign((V((Cid(0)),(String("pub")))),(Xor((Xor((V((Cid(0)),(String("1")))),(V((Cid(0)),(String("2")))))),(V((Cid(0)),(String("3")))))))))))))))))))))))))))))))
);;

(* garbled and using select4 without polarizing keys and sending over full table *)

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
let r00 = k00 xor ~false in
let r10 = k10 xor ~false in
let r01 = k01 xor ~false in
let r11 = k11 xor ~true in
let gr11 = (((((wla.p0 and wlb.p0) and r00) xor ((wla.p1 and wlb.p0) and r10)) xor ((wla.p0 and wlb.p1) and r01)) xor ((wla.p1 and wlb.p1) and r11)) in
let gr10 = (((((wla.p0 and (not wlb.p0)) and r00) xor ((wla.p1 and (not wlb.p0)) and r10)) xor ((wla.p0 and (not wlb.p1)) and r01)) xor ((wla.p1 and (not wlb.p1)) and r11)) in
let gr01 = ((((((not wla.p0) and wlb.p0) and r00) xor (((not wla.p1) and wlb.p0) and r10)) xor (((not wla.p0) and wlb.p1) and r01)) xor (((not wla.p1) and wlb.p1) and r11)) in
let gr00 = ((((((not wla.p0) and not wlb.p0) and r00) xor (((not wla.p1) and not wlb.p0) and r10)) xor (((not wla.p0) and not wlb.p1) and r01)) xor (((not wla.p1) and not wlb.p1) and r11)) in
v[1,"r1"] := gr00;
v[1,"r2"] := gr01;
v[1,"r3"] := gr10;
v[1,"r4"] := gr11;
v[1,"p1Key"] := OT[s[1,"0"],wla.k1,wla.k0];
v[1,"p1Ptr"] := OT[s[1,"0"],wla.p1,wla.p0];
v[1,"p2Key"] := select[s[2,"0"],wlb.k1,wlb.k0];
v[1,"p2Ptr"] := select[s[2,"0"],wlb.p1,wlb.p0];
v[0,"output"] := (select4(v[1,"p1Key"],v[1,"p2Key"],H["1"],H["2"],H["3"],H["4"]) xor select4(v[1,"p1Ptr"],v[1,"p2Ptr"],v[1,"r1"],v[1,"r2"],v[1,"r3"], v[1,"r4"]))   
*)

(
[
((Fname("select4")),[((EVar("b1")),(Jpdf((DVar("a")))));((EVar("b2")),(Jpdf((DVar("b")))));((EVar("x1")),(Jpdf((DVar("a1")))));((EVar("x2")),(Jpdf((DVar("a2")))));((EVar("x3")),(Jpdf((DVar("a3")))));((EVar("x4")),(Jpdf((DVar("a4")))))],((Select((Var((EVar("b1")))),(Select((Var((EVar("b2")))),(Var((EVar("x1")))),(Var((EVar("x2")))))),(Select((Var((EVar("b2")))),(Var((EVar("x3")))),(Var((EVar("x4"))))))))))
],
Let((EVar("wla")),(Record([(("k0"),(F((Cid(2)),(String("ka0")))));(("k1"),(F((Cid(2)),(String("ka1")))));(("p0"),(F((Cid(2)),(String("pa0")))));(("p1"),(Not((F((Cid(2)),(String("pa0")))))))])),
(Let((EVar("wlb")),(Record([(("k0"),(F((Cid(2)),(String("kb0")))));(("k1"),(F((Cid(2)),(String("kb1")))));(("p0"),(F((Cid(2)),(String("pb0")))));(("p1"),(Not((F((Cid(2)),(String("pb0")))))))])),
(Let((EVar("k00")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k0")));(Dot((Var((EVar("wlb")))),("k0")));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),
(Let((EVar("k10")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k0")));(Dot((Var((EVar("wlb")))),("k1")));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),
(Let((EVar("k01")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k1")));(Dot((Var((EVar("wlb")))),("k0")));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),
(Let((EVar("k11")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k1")));(Dot((Var((EVar("wlb")))),("k1")));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),
(Let((EVar("r00")),(Xor((Var((EVar("k00")))),(Bool(false)))),
(Let((EVar("r10")),(Xor((Var((EVar("k10")))),(Bool(false)))),
(Let((EVar("r01")),(Xor((Var((EVar("k01")))),(Bool(false)))),
(Let((EVar("r11")),(Xor((Var((EVar("k11")))),(Bool(true)))),
(Let((EVar("gr11")),(Xor((Xor((Xor((And((And((Dot((Var((EVar("wla")))),("p0"))),(Dot((Var((EVar("wlb")))),("p0"))))),(Var((EVar("r00")))))),(And((And((Dot((Var((EVar("wla")))),("p1"))),(Dot((Var((EVar("wlb")))),("p0"))))),(Var((EVar("r10")))))))),(And((And((Dot((Var((EVar("wla")))),("p0"))),(Dot((Var((EVar("wlb")))),("p1"))))),(Var((EVar("r01")))))))),(And((And((Dot((Var((EVar("wla")))),("p1"))),(Dot((Var((EVar("wlb")))),("p1"))))),(Var((EVar("r11")))))))),
(Let((EVar("gr10")),(Xor((Xor((Xor((And((And((Dot((Var((EVar("wla")))),("p0"))),(Not((Dot((Var((EVar("wlb")))),("p0"))))))),(Var((EVar("r00")))))),(And((And((Dot((Var((EVar("wla")))),("p1"))),(Not((Dot((Var((EVar("wlb")))),("p0"))))))),(Var((EVar("r10")))))))),(And((And((Dot((Var((EVar("wla")))),("p0"))),(Not((Dot((Var((EVar("wlb")))),("p1"))))))),(Var((EVar("r01")))))))),(And((And((Dot((Var((EVar("wla")))),("p1"))),(Not((Dot((Var((EVar("wlb")))),("p1"))))))),(Var((EVar("r11")))))))),
(Let((EVar("gr01")),(Xor((Xor((Xor((And((And((Not((Dot((Var((EVar("wla")))),("p0"))))),(Dot((Var((EVar("wlb")))),("p0"))))),(Var((EVar("r00")))))),(And((And((Not((Dot((Var((EVar("wla")))),("p1"))))),(Dot((Var((EVar("wlb")))),("p0"))))),(Var((EVar("r10")))))))),(And((And((Not((Dot((Var((EVar("wla")))),("p0"))))),(Dot((Var((EVar("wlb")))),("p1"))))),(Var((EVar("r01")))))))),(And((And((Not((Dot((Var((EVar("wla")))),("p1"))))),(Dot((Var((EVar("wlb")))),("p1"))))),(Var((EVar("r11")))))))),
(Let((EVar("gr00")),(Xor((Xor((Xor((And((And((Not((Dot((Var((EVar("wla")))),("p0"))))),(Not((Dot((Var((EVar("wlb")))),("p0"))))))),(Var((EVar("r00")))))),(And((And((Not((Dot((Var((EVar("wla")))),("p1"))))),(Not((Dot((Var((EVar("wlb")))),("p0"))))))),(Var((EVar("r10")))))))),(And((And((Not((Dot((Var((EVar("wla")))),("p0"))))),(Not((Dot((Var((EVar("wlb")))),("p1"))))))),(Var((EVar("r01")))))))),(And((And((Not((Dot((Var((EVar("wla")))),("p1"))))),(Not((Dot((Var((EVar("wlb")))),("p1"))))))),(Var((EVar("r11")))))))),
(Seq((Assign((V((Cid(1)),(String("r1")))),(Var((EVar("gr00")))))),
(Seq((Assign((V((Cid(1)),(String("r2")))),(Var((EVar("gr01")))))),
(Seq((Assign((V((Cid(1)),(String("r3")))),(Var((EVar("gr10")))))),
(Seq((Assign((V((Cid(1)),(String("r4")))),(Var((EVar("gr11")))))),
(Seq((Assign((V((Cid(1)),(String("p1Key")))),(OT((S((Cid(1)),(String("0")))),(Dot((Var((EVar("wla")))),("k1"))),(Dot((Var((EVar("wla")))),("k0"))))))),
(Seq((Assign((V((Cid(1)),(String("p1Ptr")))),(OT((S((Cid(1)),(String("0")))),(Dot((Var((EVar("wla")))),("p1"))),(Dot((Var((EVar("wla")))),("p0"))))))),
(Seq((Assign((V((Cid(1)),(String("p2Key")))),(Select((S((Cid(2)),(String("0")))),(Dot((Var((EVar("wlb")))),("k1"))),(Dot((Var((EVar("wlb")))),("k0"))))))),
(Seq((Assign((V((Cid(1)),(String("p2Ptr")))),(Select((S((Cid(2)),(String("0")))),(Dot((Var((EVar("wlb")))),("p1"))),(Dot((Var((EVar("wlb")))),("p0"))))))),(Assign((V((Cid(0)),(String("output")))),(Xor((Appl((Fname("select4")),[(V((Cid(1)),(String("p1Key"))));(V((Cid(1)),(String("p2Key"))));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),(Appl((Fname("select4")),[(V((Cid(1)),(String("p1Ptr"))));(V((Cid(1)),(String("p2Ptr"))));(V((Cid(1)),(String("r1"))));(V((Cid(1)),(String("r2"))));(V((Cid(1)),(String("r3"))));(V((Cid(1)),(String("r4"))))])))))))))))))))))))))))))))))))))))))))))))))))))
);;

(* garbled and using select4 with polarizing keys and only sending one row *)

(*
select4(b1 : jpd('a),b2 : jpd('b),x1 : jpd('a1),x2 : jpd('a2),x3 : jpd('a3),x4 : jpd('a4))
{
  select[b1,select[b2,x1,x2],select[b2,x3,x4]]
}

let wla = ({k0 = flip[2,"ka0"];k1 = not flip[2,"ka0"];p0 = flip[2,"pa0"];p1 = not flip[2,"pa0"]}) in
let wlb = ({k0 = flip[2,"kb0"];k1 = not flip[2,"kb0"];p0 = flip[2,"pb0"];p1 = not flip[2,"pb0"]}) in
let k00 = select4(wla.k0,wlb.k0,H["1"],H["2"],H["3"],H["4"]) in
let k10 = select4(wla.k0,wlb.k1,H["1"],H["2"],H["3"],H["4"]) in
let k01 = select4(wla.k1,wlb.k0,H["1"],H["2"],H["3"],H["4"]) in
let k11 = select4(wla.k1,wlb.k1,H["1"],H["2"],H["3"],H["4"]) in
let r00 = k00 xor ~false in
let r10 = k10 xor ~false in
let r01 = k01 xor ~false in
let r11 = k11 xor ~true in
let gr11 = (((((wla.p0 and wlb.p0) and r00) xor ((wla.p1 and wlb.p0) and r10)) xor ((wla.p0 and wlb.p1) and r01)) xor ((wla.p1 and wlb.p1) and r11)) in
let gr10 = (((((wla.p0 and (not wlb.p0)) and r00) xor ((wla.p1 and (not wlb.p0)) and r10)) xor ((wla.p0 and (not wlb.p1)) and r01)) xor ((wla.p1 and (not wlb.p1)) and r11)) in
let gr01 = ((((((not wla.p0) and wlb.p0) and r00) xor (((not wla.p1) and wlb.p0) and r10)) xor (((not wla.p0) and wlb.p1) and r01)) xor (((not wla.p1) and wlb.p1) and r11)) in
let gr00 = ((((((not wla.p0) and not wlb.p0) and r00) xor (((not wla.p1) and not wlb.p0) and r10)) xor (((not wla.p0) and not wlb.p1) and r01)) xor (((not wla.p1) and not wlb.p1) and r11)) in
v[1,"p1Key"] := OT[s[1,"0"],wla.k1,wla.k0];
v[1,"p1Ptr"] := OT[s[1,"0"],wla.p1,wla.p0];
v[1,"p2Key"] := select[s[2,"0"],wlb.k1,wlb.k0];
v[1,"p2Ptr"] := select[s[2,"0"],wlb.p1,wlb.p0];
v[0,"output"] := (select4(v[1,"p1Key"],v[1,"p2Key"],H["1"],H["2"],H["3"],H["4"]) xor select4(v[1,"p1Ptr"],v[1,"p2Ptr"],gr00,gr01,gr10,gr11))
*)

(
[
((Fname("select4")),[((EVar("b1")),(Jpdf((DVar("a")))));((EVar("b2")),(Jpdf((DVar("b")))));((EVar("x1")),(Jpdf((DVar("a1")))));((EVar("x2")),(Jpdf((DVar("a2")))));((EVar("x3")),(Jpdf((DVar("a3")))));((EVar("x4")),(Jpdf((DVar("a4")))))],((Select((Var((EVar("b1")))),(Select((Var((EVar("b2")))),(Var((EVar("x1")))),(Var((EVar("x2")))))),(Select((Var((EVar("b2")))),(Var((EVar("x3")))),(Var((EVar("x4"))))))))))
],
Let((EVar("wla")),(Record([(("k0"),(F((Cid(2)),(String("ka0")))));(("k1"),(Not((F((Cid(2)),(String("ka0")))))));(("p0"),(F((Cid(2)),(String("pa0")))));(("p1"),(Not((F((Cid(2)),(String("pa0")))))))])),
(Let((EVar("wlb")),(Record([(("k0"),(F((Cid(2)),(String("kb0")))));(("k1"),(Not((F((Cid(2)),(String("kb0")))))));(("p0"),(F((Cid(2)),(String("pb0")))));(("p1"),(Not((F((Cid(2)),(String("pb0")))))))])),
(Let((EVar("k00")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k0")));(Dot((Var((EVar("wlb")))),("k0")));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),
(Let((EVar("k10")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k0")));(Dot((Var((EVar("wlb")))),("k1")));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),
(Let((EVar("k01")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k1")));(Dot((Var((EVar("wlb")))),("k0")));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),
(Let((EVar("k11")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k1")));(Dot((Var((EVar("wlb")))),("k1")));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),
(Let((EVar("r00")),(Xor((Var((EVar("k00")))),(Bool(false)))),
(Let((EVar("r10")),(Xor((Var((EVar("k10")))),(Bool(false)))),
(Let((EVar("r01")),(Xor((Var((EVar("k01")))),(Bool(false)))),
(Let((EVar("r11")),(Xor((Var((EVar("k11")))),(Bool(true)))),
(Let((EVar("gr11")),(Xor((Xor((Xor((And((And((Dot((Var((EVar("wla")))),("p0"))),(Dot((Var((EVar("wlb")))),("p0"))))),(Var((EVar("r00")))))),(And((And((Dot((Var((EVar("wla")))),("p1"))),(Dot((Var((EVar("wlb")))),("p0"))))),(Var((EVar("r10")))))))),(And((And((Dot((Var((EVar("wla")))),("p0"))),(Dot((Var((EVar("wlb")))),("p1"))))),(Var((EVar("r01")))))))),(And((And((Dot((Var((EVar("wla")))),("p1"))),(Dot((Var((EVar("wlb")))),("p1"))))),(Var((EVar("r11")))))))),
(Let((EVar("gr10")),(Xor((Xor((Xor((And((And((Dot((Var((EVar("wla")))),("p0"))),(Not((Dot((Var((EVar("wlb")))),("p0"))))))),(Var((EVar("r00")))))),(And((And((Dot((Var((EVar("wla")))),("p1"))),(Not((Dot((Var((EVar("wlb")))),("p0"))))))),(Var((EVar("r10")))))))),(And((And((Dot((Var((EVar("wla")))),("p0"))),(Not((Dot((Var((EVar("wlb")))),("p1"))))))),(Var((EVar("r01")))))))),(And((And((Dot((Var((EVar("wla")))),("p1"))),(Not((Dot((Var((EVar("wlb")))),("p1"))))))),(Var((EVar("r11")))))))),
(Let((EVar("gr01")),(Xor((Xor((Xor((And((And((Not((Dot((Var((EVar("wla")))),("p0"))))),(Dot((Var((EVar("wlb")))),("p0"))))),(Var((EVar("r00")))))),(And((And((Not((Dot((Var((EVar("wla")))),("p1"))))),(Dot((Var((EVar("wlb")))),("p0"))))),(Var((EVar("r10")))))))),(And((And((Not((Dot((Var((EVar("wla")))),("p0"))))),(Dot((Var((EVar("wlb")))),("p1"))))),(Var((EVar("r01")))))))),(And((And((Not((Dot((Var((EVar("wla")))),("p1"))))),(Dot((Var((EVar("wlb")))),("p1"))))),(Var((EVar("r11")))))))),
(Let((EVar("gr00")),(Xor((Xor((Xor((And((And((Not((Dot((Var((EVar("wla")))),("p0"))))),(Not((Dot((Var((EVar("wlb")))),("p0"))))))),(Var((EVar("r00")))))),(And((And((Not((Dot((Var((EVar("wla")))),("p1"))))),(Not((Dot((Var((EVar("wlb")))),("p0"))))))),(Var((EVar("r10")))))))),(And((And((Not((Dot((Var((EVar("wla")))),("p0"))))),(Not((Dot((Var((EVar("wlb")))),("p1"))))))),(Var((EVar("r01")))))))),(And((And((Not((Dot((Var((EVar("wla")))),("p1"))))),(Not((Dot((Var((EVar("wlb")))),("p1"))))))),(Var((EVar("r11")))))))),
(Seq((Assign((V((Cid(1)),(String("p1Key")))),(OT((S((Cid(1)),(String("0")))),(Dot((Var((EVar("wla")))),("k1"))),(Dot((Var((EVar("wla")))),("k0"))))))),
(Seq((Assign((V((Cid(1)),(String("p1Ptr")))),(OT((S((Cid(1)),(String("0")))),(Dot((Var((EVar("wla")))),("p1"))),(Dot((Var((EVar("wla")))),("p0"))))))),
(Seq((Assign((V((Cid(1)),(String("p2Key")))),(Select((S((Cid(2)),(String("0")))),(Dot((Var((EVar("wlb")))),("k1"))),(Dot((Var((EVar("wlb")))),("k0"))))))),
(Seq((Assign((V((Cid(1)),(String("p2Ptr")))),(Select((S((Cid(2)),(String("0")))),(Dot((Var((EVar("wlb")))),("p1"))),(Dot((Var((EVar("wlb")))),("p0"))))))),(Assign((V((Cid(0)),(String("output")))),(Xor((Appl((Fname("select4")),[(V((Cid(1)),(String("p1Key"))));(V((Cid(1)),(String("p2Key"))));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),(Appl((Fname("select4")),[(V((Cid(1)),(String("p1Ptr"))));(V((Cid(1)),(String("p2Ptr"))));(Var((EVar("gr00"))));(Var((EVar("gr01"))));(Var((EVar("gr10"))));(Var((EVar("gr11"))))])))))))))))))))))))))))))))))))))))))))))
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
v[2, "mc"] := (select[s[2, "c"], v[2, "f1"], v[2, "f0"]] xor v[2, "rd"])
*)

(
[

],
Seq((Assign((V((Cid(2)),(String("rd")))),(Select((F((Cid(2)),(String("d")))),(H((String("r1")))),(H((String("r0")))))))),
(Seq((Assign((V((Cid(1)),(String("e")))),(Xor((S((Cid(2)),(String("c")))),(F((Cid(2)),(String("d")))))))),
(Seq((Assign((V((Cid(2)),(String("f0")))),(Xor((S((Cid(1)),(String("m0")))),(Select((V((Cid(1)),(String("e")))),(H((String("r1")))),(H((String("r0")))))))))),
(Seq((Assign((V((Cid(2)),(String("f1")))),(Xor((S((Cid(1)),(String("m1")))),(Select((Not((V((Cid(1)),(String("e")))))),(H((String("r1")))),(H((String("r0")))))))))),
(Assign((V((Cid(2)),(String("mc")))),(Xor((Select((S((Cid(2)),(String("c")))),(V((Cid(2)),(String("f1")))),(V((Cid(2)),(String("f0")))))),(V((Cid(2)),(String("rd")))))))))))))))
);;


(* AND garbled gate- verified correct and passive secure. *)

(*

select4(b1 : jpd('a),b2 : jpd('b),x1 : jpd('a1),x2 : jpd('a2),x3 : jpd('a3),x4 : jpd('a4))
{
  select[b1,select[b2,x1,x2],select[b2,x3,x4]]
}

permute4(b1 : jpd('a),b2 : jpd('b),x1 : jpd('a1),x2 : jpd('a2),x3 : jpd('a3),x4 : jpd('a4))
{
  let val1 = ((b1 and (b2 and x1)) xor ((b1 and ((not b2) and x2)) xor (((not b1) and (b2 and x3)) xor (((not b1) and ((not b2) and x4)))))) in
  let val2 = ((b1 and ((not b2) and x1)) xor ((b1 and (b2 and x2)) xor (((not b1) and ((not b2) and x3)) xor (((not b1) and (b2 and x4)))))) in
  let val3 = (((not b1) and (b2 and x1)) xor (((not b1) and ((not b2) and x2)) xor ((b1 and (b2 and x3)) xor ((b1 and ((not b2) and x4)))))) in
  let val4 = (((not b1) and ((not b2) and x1)) xor (((not b1) and (b2 and x2)) xor ((b1 and ((not b2) and x3)) xor ((b1 and (b2 and x4)))))) in
  {v1 = val1;v2 = val2;v3 = val3;v4 = val4} 
}

let p1 = flip[2,"p1"] in
let p2 = flip[2,"p2"] in
let ka = flip[2,"ka"] in
let kb = flip[2,"kb"] in
let k11 = select4(ka,kb,H["1"],H["2"],H["3"],H["4"]) in
let k10 = select4(ka,not kb,H["1"],H["2"],H["3"],H["4"]) in
let k01 = select4(not ka,kb,H["1"],H["2"],H["3"],H["4"]) in
let k00 = select4(not ka,not kb,H["1"],H["2"],H["3"],H["4"]) in
let r11 = k11 xor ~true in
let r10 = k10 xor ~false in
let r01 = k01 xor ~false in
let r00 = k00 xor ~false in
let table = permute4(p1,p2,r11,r10,r01,r00) in
v[1,"gr1"] := table.v1;
v[1,"gr2"] := table.v2;
v[1,"gr3"] := table.v3;
v[1,"gr4"] := table.v4;
v[1,"wlap"] := select[s[1,"0"],p1,(not p1)];
v[1,"wlbp"] := select[s[2,"0"],p2,(not p2)];
v[1,"ka"] := select[s[1,"0"],ka,(not ka)];
v[1,"kb"] := select[s[2,"0"],kb,(not kb)];
v[1,"key"] := select4(v[1,"ka"],v[1,"kb"],H["1"],H["2"],H["3"],H["4"]);
v[1,"row"] := select4(v[1,"wlap"],v[1,"wlbp"],v[1,"gr1"],v[1,"gr2"],v[1,"gr3"],v[1,"gr4"]);
v[0,"out1"] := (v[1,"key"] xor v[1,"row"])

*)


let agc = (
[
((Fname("select4")),[((EVar("b1")),(Jpdf((DVar("a")))));((EVar("b2")),(Jpdf((DVar("b")))));((EVar("x1")),(Jpdf((DVar("a1")))));((EVar("x2")),(Jpdf((DVar("a2")))));((EVar("x3")),(Jpdf((DVar("a3")))));((EVar("x4")),(Jpdf((DVar("a4")))))],((Select((Var((EVar("b1")))),(Select((Var((EVar("b2")))),(Var((EVar("x1")))),(Var((EVar("x2")))))),(Select((Var((EVar("b2")))),(Var((EVar("x3")))),(Var((EVar("x4"))))))))));
((Fname("permute4")),[((EVar("b1")),(Jpdf((DVar("a")))));((EVar("b2")),(Jpdf((DVar("b")))));((EVar("x1")),(Jpdf((DVar("a1")))));((EVar("x2")),(Jpdf((DVar("a2")))));((EVar("x3")),(Jpdf((DVar("a3")))));((EVar("x4")),(Jpdf((DVar("a4")))))],(
(Let((EVar("val1")),(Xor((And((Var((EVar("b1")))),(And((Var((EVar("b2")))),(Var((EVar("x1")))))))),(Xor((And((Var((EVar("b1")))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x2")))))))),(Xor((And((Not((Var((EVar("b1")))))),(And((Var((EVar("b2")))),(Var((EVar("x3")))))))),(And((Not((Var((EVar("b1")))))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x4")))))))))))))),
(Let((EVar("val2")),(Xor((And((Var((EVar("b1")))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x1")))))))),(Xor((And((Var((EVar("b1")))),(And((Var((EVar("b2")))),(Var((EVar("x2")))))))),(Xor((And((Not((Var((EVar("b1")))))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x3")))))))),(And((Not((Var((EVar("b1")))))),(And((Var((EVar("b2")))),(Var((EVar("x4")))))))))))))),
(Let((EVar("val3")),(Xor((And((Not((Var((EVar("b1")))))),(And((Var((EVar("b2")))),(Var((EVar("x1")))))))),(Xor((And((Not((Var((EVar("b1")))))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x2")))))))),(Xor((And((Var((EVar("b1")))),(And((Var((EVar("b2")))),(Var((EVar("x3")))))))),(And((Var((EVar("b1")))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x4")))))))))))))),
(Let((EVar("val4")),(Xor((And((Not((Var((EVar("b1")))))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x1")))))))),(Xor((And((Not((Var((EVar("b1")))))),(And((Var((EVar("b2")))),(Var((EVar("x2")))))))),(Xor((And((Var((EVar("b1")))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x3")))))))),(And((Var((EVar("b1")))),(And((Var((EVar("b2")))),(Var((EVar("x4")))))))))))))),(Record([(("v1"),(Var((EVar("val1")))));(("v2"),(Var((EVar("val2")))));(("v3"),(Var((EVar("val3")))));(("v4"),(Var((EVar("val4")))))]))))))))))))
],
Let((EVar("p1")),(F((Cid(2)),(String("p1")))),
(Let((EVar("p2")),(F((Cid(2)),(String("p2")))),
(Let((EVar("ka")),(F((Cid(2)),(String("ka")))),
(Let((EVar("kb")),(F((Cid(2)),(String("kb")))),
(Let((EVar("k11")),(Appl((Fname("select4")),[(Var((EVar("ka"))));(Var((EVar("kb"))));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),
(Let((EVar("k10")),(Appl((Fname("select4")),[(Var((EVar("ka"))));(Not((Var((EVar("kb"))))));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),
(Let((EVar("k01")),(Appl((Fname("select4")),[(Not((Var((EVar("ka"))))));(Var((EVar("kb"))));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),
(Let((EVar("k00")),(Appl((Fname("select4")),[(Not((Var((EVar("ka"))))));(Not((Var((EVar("kb"))))));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])),
(Let((EVar("r11")),(Xor((Var((EVar("k11")))),(Bool(true)))),
(Let((EVar("r10")),(Xor((Var((EVar("k10")))),(Bool(false)))),
(Let((EVar("r01")),(Xor((Var((EVar("k01")))),(Bool(false)))),
(Let((EVar("r00")),(Xor((Var((EVar("k00")))),(Bool(false)))),
(Let((EVar("table")),(Appl((Fname("permute4")),[(Var((EVar("p1"))));(Var((EVar("p2"))));(Var((EVar("r11"))));(Var((EVar("r10"))));(Var((EVar("r01"))));(Var((EVar("r00"))))])),
(Seq((Assign((V((Cid(1)),(String("gr1")))),(Dot((Var((EVar("table")))),("v1"))))),
(Seq((Assign((V((Cid(1)),(String("gr2")))),(Dot((Var((EVar("table")))),("v2"))))),
(Seq((Assign((V((Cid(1)),(String("gr3")))),(Dot((Var((EVar("table")))),("v3"))))),
(Seq((Assign((V((Cid(1)),(String("gr4")))),(Dot((Var((EVar("table")))),("v4"))))),
(Seq((Assign((V((Cid(1)),(String("wlap")))),(Select((S((Cid(1)),(String("0")))),(Var((EVar("p1")))),(Not((Var((EVar("p1")))))))))),
(Seq((Assign((V((Cid(1)),(String("wlbp")))),(Select((S((Cid(2)),(String("0")))),(Var((EVar("p2")))),(Not((Var((EVar("p2")))))))))),
(Seq((Assign((V((Cid(1)),(String("ka")))),(Select((S((Cid(1)),(String("0")))),(Var((EVar("ka")))),(Not((Var((EVar("ka")))))))))),
(Seq((Assign((V((Cid(1)),(String("kb")))),(Select((S((Cid(2)),(String("0")))),(Var((EVar("kb")))),(Not((Var((EVar("kb")))))))))),
(Seq((Assign((V((Cid(1)),(String("key")))),(Appl((Fname("select4")),[(V((Cid(1)),(String("ka"))));(V((Cid(1)),(String("kb"))));(H((String("1"))));(H((String("2"))));(H((String("3"))));(H((String("4"))))])))),
(Seq((Assign((V((Cid(1)),(String("row")))),(Appl((Fname("select4")),[(V((Cid(1)),(String("wlap"))));(V((Cid(1)),(String("wlbp"))));(V((Cid(1)),(String("gr1"))));(V((Cid(1)),(String("gr2"))));(V((Cid(1)),(String("gr3"))));(V((Cid(1)),(String("gr4"))))])))),(Assign((V((Cid(0)),(String("out1")))),(Xor((V((Cid(1)),(String("key")))),(V((Cid(1)),(String("row")))))))))))))))))))))))))))))))))))))))))))))))))))))
  );;

(* 
   AND garbled circuit- verified correct and passive secure. Note: this takes a long time to parse with current 
   parser. Also this is starting to push tractability bounds on desktop..
*)

(*
select4(b1 : jpd('a),b2 : jpd('b),x1 : jpd('a1),x2 : jpd('a2),x3 : jpd('a3),x4 : jpd('a4))
{
  select[b1,select[b2,x1,x2],select[b2,x3,x4]]
}

permute4(b1 : jpd('a),b2 : jpd('b),x1 : jpd('a1),x2 : jpd('a2),x3 : jpd('a3),x4 : jpd('a4))
{
  let val1 = ((b1 and (b2 and x1)) xor ((b1 and ((not b2) and x2)) xor (((not b1) and (b2 and x3)) xor (((not b1) and ((not b2) and x4)))))) in
  let val2 = ((b1 and ((not b2) and x1)) xor ((b1 and (b2 and x2)) xor (((not b1) and ((not b2) and x3)) xor (((not b1) and (b2 and x4)))))) in
  let val3 = (((not b1) and (b2 and x1)) xor (((not b1) and ((not b2) and x2)) xor ((b1 and (b2 and x3)) xor ((b1 and ((not b2) and x4)))))) in
  let val4 = (((not b1) and ((not b2) and x1)) xor (((not b1) and (b2 and x2)) xor ((b1 and ((not b2) and x3)) xor ((b1 and (b2 and x4)))))) in
  {v1 = val1;v2 = val2;v3 = val3;v4 = val4} 
}

garbledecode(wl : { k : jpd('ck); p : jpd('cp) })
{
  let r1 = wl.k xor ~true in
  let r0 = (not wl.k) xor ~false in
  v[1,"OUTtt1"] := select[wl.p,r1,r0];
  v[1,"OUTtt2"] := select[not wl.p,r1,r0]
}

keygen(b1 : jpd('a),b2 : jpd('b))
{
  let k11 = select4(wla.k,wlb.k,H[gid || "1"],H[gid || "2"],H[gid || "3"],H[gid || "4"]) in
  let k10 = select4(wla.k,not wlb.k,H[gid || "1"],H[gid || "2"],H[gid || "3"],H[gid || "4"]) in
  let k01 = select4(not wla.k,wlb.k,H[gid || "1"],H[gid || "2"],H[gid || "3"],H[gid || "4"]) in
  let k00 = select4(not wla.k,not wlb.k,H[gid || "1"],H[gid || "2"],H[gid || "3"],H[gid || "4"]) in
  {k11 = k11;k10 = k10;k01 = k01;k00 = k00}
}

sharetable(gid : string(gid), vid : string(vid), table : {v1 : jpd('a1);v2 : jpd('a2);v3 : jpd('a3);v4 : jpd('a4)})
{   
  v[1,gid || vid || "1"] := table.v1;
  v[1,gid || vid || "2"] := table.v2;
  v[1,gid || vid || "3"] := table.v3;
  v[1,gid || vid || "4"] := table.v4
}

garblegate(gid : string(gid), wla : {k : jpd('ak);p : jpd('ap)}, wlb : {k : jpd('bk);p : jpd('bp)}, wlc : {k : jpd('ck);p : jpd('cp)}) 
{
  let keys = keygen(wla.k,wlb.k) in
  let r11 = keys.k11 xor wlc.k in 
  let r10 = keys.k10 xor (not wlc.k) in
  let r01 = keys.k01 xor (not wlc.k) in
  let r00 = keys.k00 xor (not wlc.k) in
  let p11 = keys.k11 xor wlc.p in
  let p10 = keys.k10 xor (not wlc.p) in
  let p01 = keys.k01 xor (not wlc.p) in
  let p00 = keys.k00 xor (not wlc.p) in  
  let null = sharetable(gid, "tt", permute4(wla.p,wlb.p,r11,r10,r01,r00)) in
  sharetable(gid, "pt", permute4(wla.p,wlb.p,p11,p10,p01,p00))
}

decode(wl : { k : jpd('ck); p : jpd('cp) })
{
  wl.k xor select[wl.p,v[1,"OUTtt1"],v[1,"OUTtt2"]]
}

evalgate(gid : string(gid), wla : { k : jpd('ak); p : jpd('ap) }, wlb : { k : jpd('bk); p : jpd('bp) })
{
  let k = select4(wla.k,wlb.k,H[gid || "1"],H[gid || "2"],H[gid || "3"],H[gid || "4"]) in
  let ct = select4(wla.p,wlb.p,v[1,gid || "tt1"],v[1,gid || "tt2"],v[1,gid || "tt3"],v[1,gid || "tt4"]) in
  let cp = select4(wla.p,wlb.p,v[1,gid || "pt1"],v[1,gid || "pt2"],v[1,gid || "pt3"],v[1,gid || "pt4"]) in
  { k = k xor ct; p = k xor cp }
}

let wl1 = { k = flip[2,"fwl1"]; p = flip[2,"pwl1"] } in
let wl2 = { k = flip[2,"fwl2"]; p = flip[2,"pwl2"] } in
let wlo = { k = flip[2,"fwlo"]; p = flip[2,"fwlo"] } in
let null = garbledecode(wlo) in
let null = garblegate("1",wl1,wl2,wlo) in
v[1,"k1"] := select[s[1,"0"],wl1.k,(not wl1.k)];
v[1,"p1"] := select[s[1,"0"],wl1.p,(not wl1.p)];
v[1,"k2"] := select[s[2,"0"],wl2.k,(not wl2.k)];
v[1,"p2"] := select[s[2,"0"],wl2.p,(not wl2.p)];
v[0,"out1"] := decode(evalgate("1",{ k = v[1,"k1"]; p = v[1,"p1"] },{k = v[1,"k2"]; p = v[1,"p2"] }))
 
 *)

(
[
((Fname("select4")),[((EVar("b1")),(Jpdf((DVar("a")))));((EVar("b2")),(Jpdf((DVar("b")))));((EVar("x1")),(Jpdf((DVar("a1")))));((EVar("x2")),(Jpdf((DVar("a2")))));((EVar("x3")),(Jpdf((DVar("a3")))));((EVar("x4")),(Jpdf((DVar("a4")))))],((Select((Var((EVar("b1")))),(Select((Var((EVar("b2")))),(Var((EVar("x1")))),(Var((EVar("x2")))))),(Select((Var((EVar("b2")))),(Var((EVar("x3")))),(Var((EVar("x4"))))))))));
((Fname("permute4")),[((EVar("b1")),(Jpdf((DVar("a")))));((EVar("b2")),(Jpdf((DVar("b")))));((EVar("x1")),(Jpdf((DVar("a1")))));((EVar("x2")),(Jpdf((DVar("a2")))));((EVar("x3")),(Jpdf((DVar("a3")))));((EVar("x4")),(Jpdf((DVar("a4")))))],(
(Let((EVar("val1")),(Xor((And((Var((EVar("b1")))),(And((Var((EVar("b2")))),(Var((EVar("x1")))))))),(Xor((And((Var((EVar("b1")))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x2")))))))),(Xor((And((Not((Var((EVar("b1")))))),(And((Var((EVar("b2")))),(Var((EVar("x3")))))))),(And((Not((Var((EVar("b1")))))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x4")))))))))))))),
(Let((EVar("val2")),(Xor((And((Var((EVar("b1")))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x1")))))))),(Xor((And((Var((EVar("b1")))),(And((Var((EVar("b2")))),(Var((EVar("x2")))))))),(Xor((And((Not((Var((EVar("b1")))))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x3")))))))),(And((Not((Var((EVar("b1")))))),(And((Var((EVar("b2")))),(Var((EVar("x4")))))))))))))),
(Let((EVar("val3")),(Xor((And((Not((Var((EVar("b1")))))),(And((Var((EVar("b2")))),(Var((EVar("x1")))))))),(Xor((And((Not((Var((EVar("b1")))))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x2")))))))),(Xor((And((Var((EVar("b1")))),(And((Var((EVar("b2")))),(Var((EVar("x3")))))))),(And((Var((EVar("b1")))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x4")))))))))))))),
(Let((EVar("val4")),(Xor((And((Not((Var((EVar("b1")))))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x1")))))))),(Xor((And((Not((Var((EVar("b1")))))),(And((Var((EVar("b2")))),(Var((EVar("x2")))))))),(Xor((And((Var((EVar("b1")))),(And((Not((Var((EVar("b2")))))),(Var((EVar("x3")))))))),(And((Var((EVar("b1")))),(And((Var((EVar("b2")))),(Var((EVar("x4")))))))))))))),(Record([(("v1"),(Var((EVar("val1")))));(("v2"),(Var((EVar("val2")))));(("v3"),(Var((EVar("val3")))));(("v4"),(Var((EVar("val4")))))]))))))))))));
((Fname("garbledecode")),[((EVar("wl")),(RecTy([(("k"),(Jpdf((DVar("ck")))));(("p"),(Jpdf((DVar("cp")))))])))],(
(Let((EVar("r1")),(Xor((Dot((Var((EVar("wl")))),("k"))),(Bool(true)))),
(Let((EVar("r0")),(Xor((Not((Dot((Var((EVar("wl")))),("k"))))),(Bool(false)))),
(Seq((Assign((V((Cid(1)),(String("OUTtt1")))),(Select((Dot((Var((EVar("wl")))),("p"))),(Var((EVar("r1")))),(Var((EVar("r0")))))))),(Assign((V((Cid(1)),(String("OUTtt2")))),(Select((Not((Dot((Var((EVar("wl")))),("p"))))),(Var((EVar("r1")))),(Var((EVar("r0"))))))))))))))));
((Fname("keygen")),[((EVar("b1")),(Jpdf((DVar("a")))));((EVar("b2")),(Jpdf((DVar("b")))))],(
(Let((EVar("k11")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k")));(Dot((Var((EVar("wlb")))),("k")));(H((Concat((Var((EVar("gid")))),(String("1"))))));(H((Concat((Var((EVar("gid")))),(String("2"))))));(H((Concat((Var((EVar("gid")))),(String("3"))))));(H((Concat((Var((EVar("gid")))),(String("4"))))))])),
(Let((EVar("k10")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k")));(Not((Dot((Var((EVar("wlb")))),("k")))));(H((Concat((Var((EVar("gid")))),(String("1"))))));(H((Concat((Var((EVar("gid")))),(String("2"))))));(H((Concat((Var((EVar("gid")))),(String("3"))))));(H((Concat((Var((EVar("gid")))),(String("4"))))))])),
(Let((EVar("k01")),(Appl((Fname("select4")),[(Not((Dot((Var((EVar("wla")))),("k")))));(Dot((Var((EVar("wlb")))),("k")));(H((Concat((Var((EVar("gid")))),(String("1"))))));(H((Concat((Var((EVar("gid")))),(String("2"))))));(H((Concat((Var((EVar("gid")))),(String("3"))))));(H((Concat((Var((EVar("gid")))),(String("4"))))))])),
(Let((EVar("k00")),(Appl((Fname("select4")),[(Not((Dot((Var((EVar("wla")))),("k")))));(Not((Dot((Var((EVar("wlb")))),("k")))));(H((Concat((Var((EVar("gid")))),(String("1"))))));(H((Concat((Var((EVar("gid")))),(String("2"))))));(H((Concat((Var((EVar("gid")))),(String("3"))))));(H((Concat((Var((EVar("gid")))),(String("4"))))))])),(Record([(("k11"),(Var((EVar("k11")))));(("k10"),(Var((EVar("k10")))));(("k01"),(Var((EVar("k01")))));(("k00"),(Var((EVar("k00")))))]))))))))))));
((Fname("sharetable")),[((EVar("gid")),(StringTy((Var((EVar("gid")))))));((EVar("vid")),(StringTy((Var((EVar("vid")))))));((EVar("table")),(RecTy([(("v1"),(Jpdf((DVar("a1")))));(("v2"),(Jpdf((DVar("a2")))));(("v3"),(Jpdf((DVar("a3")))));(("v4"),(Jpdf((DVar("a4")))))])))],(
(Seq((Assign((V((Cid(1)),(Concat((Concat((Var((EVar("gid")))),(Var((EVar("vid")))))),(String("1")))))),(Dot((Var((EVar("table")))),("v1"))))),
(Seq((Assign((V((Cid(1)),(Concat((Concat((Var((EVar("gid")))),(Var((EVar("vid")))))),(String("2")))))),(Dot((Var((EVar("table")))),("v2"))))),
(Seq((Assign((V((Cid(1)),(Concat((Concat((Var((EVar("gid")))),(Var((EVar("vid")))))),(String("3")))))),(Dot((Var((EVar("table")))),("v3"))))),(Assign((V((Cid(1)),(Concat((Concat((Var((EVar("gid")))),(Var((EVar("vid")))))),(String("4")))))),(Dot((Var((EVar("table")))),("v4")))))))))))));
((Fname("garblegate")),[((EVar("gid")),(StringTy((Var((EVar("gid")))))));((EVar("wla")),(RecTy([(("k"),(Jpdf((DVar("ak")))));(("p"),(Jpdf((DVar("ap")))))])));((EVar("wlb")),(RecTy([(("k"),(Jpdf((DVar("bk")))));(("p"),(Jpdf((DVar("bp")))))])));((EVar("wlc")),(RecTy([(("k"),(Jpdf((DVar("ck")))));(("p"),(Jpdf((DVar("cp")))))])))],(
(Let((EVar("keys")),(Appl((Fname("keygen")),[(Dot((Var((EVar("wla")))),("k")));(Dot((Var((EVar("wlb")))),("k")))])),
(Let((EVar("r11")),(Xor((Dot((Var((EVar("keys")))),("k11"))),(Dot((Var((EVar("wlc")))),("k"))))),
(Let((EVar("r10")),(Xor((Dot((Var((EVar("keys")))),("k10"))),(Not((Dot((Var((EVar("wlc")))),("k"))))))),
(Let((EVar("r01")),(Xor((Dot((Var((EVar("keys")))),("k01"))),(Not((Dot((Var((EVar("wlc")))),("k"))))))),
(Let((EVar("r00")),(Xor((Dot((Var((EVar("keys")))),("k00"))),(Not((Dot((Var((EVar("wlc")))),("k"))))))),
(Let((EVar("p11")),(Xor((Dot((Var((EVar("keys")))),("k11"))),(Dot((Var((EVar("wlc")))),("p"))))),
(Let((EVar("p10")),(Xor((Dot((Var((EVar("keys")))),("k10"))),(Not((Dot((Var((EVar("wlc")))),("p"))))))),
(Let((EVar("p01")),(Xor((Dot((Var((EVar("keys")))),("k01"))),(Not((Dot((Var((EVar("wlc")))),("p"))))))),
(Let((EVar("p00")),(Xor((Dot((Var((EVar("keys")))),("k00"))),(Not((Dot((Var((EVar("wlc")))),("p"))))))),
(Let((EVar("null")),(Appl((Fname("sharetable")),[(Var((EVar("gid"))));(String("tt"));(Appl((Fname("permute4")),[(Dot((Var((EVar("wla")))),("p")));(Dot((Var((EVar("wlb")))),("p")));(Var((EVar("r11"))));(Var((EVar("r10"))));(Var((EVar("r01"))));(Var((EVar("r00"))))]))])),(Appl((Fname("sharetable")),[(Var((EVar("gid"))));(String("pt"));(Appl((Fname("permute4")),[(Dot((Var((EVar("wla")))),("p")));(Dot((Var((EVar("wlb")))),("p")));(Var((EVar("p11"))));(Var((EVar("p10"))));(Var((EVar("p01"))));(Var((EVar("p00"))))]))]))))))))))))))))))))))));
((Fname("decode")),[((EVar("wl")),(RecTy([(("k"),(Jpdf((DVar("ck")))));(("p"),(Jpdf((DVar("cp")))))])))],((Xor((Dot((Var((EVar("wl")))),("k"))),(Select((Dot((Var((EVar("wl")))),("p"))),(V((Cid(1)),(String("OUTtt1")))),(V((Cid(1)),(String("OUTtt2"))))))))));
((Fname("evalgate")),[((EVar("gid")),(StringTy((Var((EVar("gid")))))));((EVar("wla")),(RecTy([(("k"),(Jpdf((DVar("ak")))));(("p"),(Jpdf((DVar("ap")))))])));((EVar("wlb")),(RecTy([(("k"),(Jpdf((DVar("bk")))));(("p"),(Jpdf((DVar("bp")))))])))],(
(Let((EVar("k")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("k")));(Dot((Var((EVar("wlb")))),("k")));(H((Concat((Var((EVar("gid")))),(String("1"))))));(H((Concat((Var((EVar("gid")))),(String("2"))))));(H((Concat((Var((EVar("gid")))),(String("3"))))));(H((Concat((Var((EVar("gid")))),(String("4"))))))])),
(Let((EVar("ct")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("p")));(Dot((Var((EVar("wlb")))),("p")));(V((Cid(1)),(Concat((Var((EVar("gid")))),(String("tt1"))))));(V((Cid(1)),(Concat((Var((EVar("gid")))),(String("tt2"))))));(V((Cid(1)),(Concat((Var((EVar("gid")))),(String("tt3"))))));(V((Cid(1)),(Concat((Var((EVar("gid")))),(String("tt4"))))))])),
(Let((EVar("cp")),(Appl((Fname("select4")),[(Dot((Var((EVar("wla")))),("p")));(Dot((Var((EVar("wlb")))),("p")));(V((Cid(1)),(Concat((Var((EVar("gid")))),(String("pt1"))))));(V((Cid(1)),(Concat((Var((EVar("gid")))),(String("pt2"))))));(V((Cid(1)),(Concat((Var((EVar("gid")))),(String("pt3"))))));(V((Cid(1)),(Concat((Var((EVar("gid")))),(String("pt4"))))))])),(Record([(("k"),(Xor((Var((EVar("k")))),(Var((EVar("ct")))))));(("p"),(Xor((Var((EVar("k")))),(Var((EVar("cp")))))))]))))))))))
],
Let((EVar("wl1")),(Record([(("k"),(F((Cid(2)),(String("fwl1")))));(("p"),(F((Cid(2)),(String("pwl1")))))])),
(Let((EVar("wl2")),(Record([(("k"),(F((Cid(2)),(String("fwl2")))));(("p"),(F((Cid(2)),(String("pwl2")))))])),
(Let((EVar("wlo")),(Record([(("k"),(F((Cid(2)),(String("fwlo")))));(("p"),(F((Cid(2)),(String("fwlo")))))])),
(Let((EVar("null")),(Appl((Fname("garbledecode")),[(Var((EVar("wlo"))))])),
(Let((EVar("null")),(Appl((Fname("garblegate")),[(String("1"));(Var((EVar("wl1"))));(Var((EVar("wl2"))));(Var((EVar("wlo"))))])),
(Seq((Assign((V((Cid(1)),(String("k1")))),(Select((S((Cid(1)),(String("0")))),(Dot((Var((EVar("wl1")))),("k"))),(Not((Dot((Var((EVar("wl1")))),("k"))))))))),
(Seq((Assign((V((Cid(1)),(String("p1")))),(Select((S((Cid(1)),(String("0")))),(Dot((Var((EVar("wl1")))),("p"))),(Not((Dot((Var((EVar("wl1")))),("p"))))))))),
(Seq((Assign((V((Cid(1)),(String("k2")))),(Select((S((Cid(2)),(String("0")))),(Dot((Var((EVar("wl2")))),("k"))),(Not((Dot((Var((EVar("wl2")))),("k"))))))))),
(Seq((Assign((V((Cid(1)),(String("p2")))),(Select((S((Cid(2)),(String("0")))),(Dot((Var((EVar("wl2")))),("p"))),(Not((Dot((Var((EVar("wl2")))),("p"))))))))),(Assign((V((Cid(0)),(String("out1")))),(Appl((Fname("decode")),[(Appl((Fname("evalgate")),[(String("1"));(Record([(("k"),(V((Cid(1)),(String("k1")))));(("p"),(V((Cid(1)),(String("p1")))))]));(Record([(("k"),(V((Cid(1)),(String("k2")))));(("p"),(V((Cid(1)),(String("p2")))))]))]))])))))))))))))))))))))
);;

