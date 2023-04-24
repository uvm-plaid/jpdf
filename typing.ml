(* Syntax *)

type id = EVar of string | Fname of string;;
type field = string;;

type expr =
    Var of id 
  | Bool of bool
  | Not of expr 
  | And of expr * expr 
  | Or of expr * expr 
  | Xor of expr * expr 
  | H of expr
  | F of expr * expr
  | S of expr * expr
  | V of expr * expr
  | String of string
  | Concat of expr * expr
  | Cid of int
  | Select of expr * expr * expr 
  | OT of expr * expr * expr 
  | Assign of expr * expr 
  | Appl of id * expr list
  | Record of (field * expr) list
  | Dot of expr * field
  | Let of id * expr * expr
  | Seq of expr * expr;;

type ppdf =
    Meet of ppdf * ppdf
  | Join of ppdf * ppdf
  | Comp of ppdf
  | Xord of ppdf * ppdf
  | Dist of expr 
  | DVar of string
  | Bot
  | Top;;

type jpdty =
    RecTy of (field * jpdty) list
  | StringTy of expr
  | CidTy of expr
  | Jpdf of ppdf
  | GenFn of jpdty list * (jpdty * views)
  | Unit

and views = (expr * jpdty) list;;

type bindings = (id * jpdty) list;;
  
type fndecl = id * bindings * expr;;

type progn = (fndecl list) * expr;;

(* Type Theory *)

exception TypeError of string;;

let synthesize t1s t2s =
  let rec synth = function
      (StringTy(Var(x)), StringTy(e)) -> ([], [(x,e)])
    | (CidTy(Var(x)), CidTy(e)) -> ([], [(x,e)])
    | (Jpdf(DVar(_) as alpha), Jpdf(mu)) -> ([(alpha, mu)], [])
    | (RecTy(fs1), RecTy(fs2)) ->
       let synthr ((f1, t1),(f2, t2)) =
         if f1 <> f2 then raise (TypeError "record field name mismatch") else synth (t1,t2)
       in
       List.fold_left
         (fun (ses,sts) x -> let (se,st) = synthr x in (ses@se, sts@st)) ([],[])
         (List.combine fs1 fs2)
    | (Unit,Unit) -> ([],[])
    | _ -> raise (TypeError "cannot synthesize")  
  in  
  List.fold_left
    (fun (ses,sts) x -> let (se,st) = synth x in (ses@se, sts@st)) ([],[])
    (List.combine t1s t2s);;

let substitute t (tsubs, esubs) =
  let rec esub = function
      (Var x) -> List.assoc x esubs 
    | (String s) -> (String s)
    | (Cid n) -> (Cid n)
    | (Concat(e1,e2)) ->
       (match (esub e1, esub e2) with
        | (String s1, String s2) -> String(s1 ^ s2)
        | (s1,s2) -> Concat(s1, s2))
    | (H(e)) -> H(esub e)
    | (V(e1,e2)) -> V(esub e1, esub e2)
    | (S(e1,e2)) -> S(esub e1, esub e2)
    | (F(e1,e2)) -> F(esub e1, esub e2)
    | _ -> raise (TypeError "unexpected expression in substitution")
  in
  let rec pdfsub = function
      (DVar x) -> List.assoc (DVar x) tsubs
    | (Meet(p1,p2)) -> Meet(pdfsub p1, pdfsub p2)
    | (Join(p1,p2)) -> Join(pdfsub p1, pdfsub p2)
    | (Xord(p1,p2)) -> Xord(pdfsub p1, pdfsub p2)
    | (Comp(p)) -> Comp(pdfsub p)
    | (Dist e) -> Dist(esub e)
    | Bot -> Bot
    | Top -> Top
  in
  let rec tsub = function
      (StringTy e) -> StringTy(esub e)
    | (CidTy e) -> CidTy(esub e)
    | (RecTy fs) -> RecTy(List.map (fun (f,t) -> (f, (tsub t))) fs)
    | (Jpdf(mu)) -> Jpdf(pdfsub mu)
    | Unit -> Unit
    | _ -> raise (TypeError "Pi type in subsitution")
  in
  tsub t;;
       
let rec pty (gamma : bindings) (views : views) = function 
    (Bool true) -> Jpdf(Top), views 
  | (Bool false) -> Jpdf(Bot), views
  | (Var x) -> (List.assoc x gamma), views
  | (Cid cid) -> CidTy(Cid cid), views
  | (String s) -> StringTy(String s), views
  | (Concat(e1, e2)) ->
     (match (pty gamma views e1) with
        (StringTy(s1), v1) ->
         (match (pty gamma v1 e2) with
            (StringTy(s2), v2) -> StringTy(Concat(s1,s2)), v2
          | _ ->  raise (TypeError "non-string type in string expression"))
      |  _ ->  raise (TypeError "non-string type in string expression")) 
  | (F(e1,e2)) ->
     (match (pty gamma views e1) with
        (CidTy p, vp) -> 
         (match (pty gamma vp e2) with
            (StringTy si, v) -> (Jpdf(Dist(F(p,si))), v)
          | _ -> raise (TypeError "not a string type in flip id"))
      | _ -> raise (TypeError "not a cid type in flip cid"))
  | (S(e1,e2)) ->
     (match (pty gamma views e1) with
        (CidTy p, vp) -> 
         (match (pty gamma vp e2) with
            (StringTy si, v) -> (Jpdf(Dist(S(p,si))), v)
          | _ -> raise (TypeError "not a string type in secret id"))
      | _ -> raise (TypeError "not a cid type in secret cid"))
  | (V(e1,e2)) ->
     (match (pty gamma views e1) with
        (CidTy p, vp) -> 
         (match (pty gamma vp e2) with
            (StringTy si, v) -> (Jpdf(Dist(V(p,si))), v)
          | _ -> raise (TypeError "not a string type in view id"))
      | _ -> raise (TypeError "not a cid type in view cid"))
  | (H e) ->
     (match (pty gamma views e) with
        (StringTy si, v) -> (Jpdf(Dist(H(si))), v)
      | _ -> raise (TypeError "not a string type in oracle id"))
  | (Assign (e1,e2)) ->
     (match e1 with
        (V(p, e)) ->
         (match (pty gamma views e) with
            (StringTy si, v1) ->
             (match (pty gamma v1 e2) with
                (Jpdf(mu), v2) -> (Unit, v2@[(V(p,si),Jpdf(mu))])
              | _ -> raise (TypeError "not a distribution type in assignment"))
          | _ -> raise (TypeError "not a string type in view id"))
      | _ -> raise (TypeError "non-view assignment"))
  | (And (e1, e2)) ->
     (match (pty gamma views e1) with
        (Jpdf(mu1), v1) ->
        (match (pty gamma v1 e2) with
           (Jpdf(mu2), v2) -> Jpdf(Meet(mu1, mu2)), v2
         | _ ->  raise (TypeError "non-pdf type in logical formula"))
      |  _ ->  raise (TypeError "non-pdf type in logical formula")) 
  | (Or (e1, e2)) ->
     (match (pty gamma views e1) with
        (Jpdf(mu1), v1) ->
        (match (pty gamma v1 e2) with
           (Jpdf(mu2), v2) -> Jpdf(Join(mu1, mu2)), v2
         | _ ->  raise (TypeError "non-pdf type in logical formula"))
      |  _ ->  raise (TypeError "non-pdf type in logical formula")) 
  | (Not e) ->
     (match (pty gamma views e) with
        (Jpdf(mu), v) -> Jpdf(Comp(mu)), v
      |  _ ->  raise (TypeError "non-pdf type in logical formula")) 
  | (Select (e1, e2, e3)) ->
     (match (pty gamma views e1) with
        (Jpdf(mu1), v1) ->
        (match (pty gamma v1 e2) with
           (Jpdf(mu2), v2) ->
            (match (pty gamma v2 e3) with
               (Jpdf(mu3), v3) ->
                Jpdf(Join(Meet(mu1,mu2), Meet(Comp(mu1),mu3))), v3 
             | _ ->  raise (TypeError "non-pdf type in logical formula"))
         |  _ ->  raise (TypeError "non-pdf type in logical formula"))
      |  _ ->  raise (TypeError "non-pdf type in logical formula"))
  | (Xor (e1, e2)) ->
     (match (pty gamma views e1) with
        (Jpdf(mu1), v1) ->
        (match (pty gamma v1 e2) with
           (Jpdf(mu2), v2) -> Jpdf(Xord(mu1,mu2)), v2
         | _ ->  raise (TypeError "non-pdf type in logical formula"))
      |  _ ->  raise (TypeError "non-pdf type in logical formula")) 
  | (Appl (f, args)) ->
     (match (List.assoc f gamma) with
        (GenFn(t1s, (t2, vf))) -> 
         let arg_tys,v =
           List.fold_left
             (fun (ts, v) -> fun e -> let t, v' = (pty gamma v e) in (ts@[t], v'))
             ([],views) args
         in
         let subs = synthesize t1s arg_tys in
         (substitute t2 subs, v@(List.map (fun (v,t) -> (v, substitute t subs)) vf))
      | _ -> raise (TypeError "wrong function type"))
  | (Let(x,e1,e2)) ->  let (t,v) = pty gamma views e1 in pty ((x,t)::gamma) v e2
  | (Seq(e1,e2)) -> let (_,v) = pty gamma views e1 in pty gamma v e2
  | (Dot(e,f)) ->
     (match (pty gamma views e) with
        (RecTy(fs), v) -> (List.assoc f fs, v)
      | _ -> raise (TypeError "record type in select"))
  | (Record fs) ->
     let (fts,v) =
       List.fold_left
         (fun (fts, v) (f,e) -> let (t,v') = pty gamma v e in (fts@[(f,t)],v'))
         ([],views) fs
     in
     (RecTy fts, v)
  | _ -> raise (TypeError "UNFINISHED");;

let fnpty (gamma : bindings) ((f, params, e) : fndecl) =
  GenFn((List.map (fun (x,t) -> t) params),(pty (params@gamma) [] e));;

let progty ((fns, e) : progn) =
  let gamma = List.fold_left (fun g ((f,_,_) as fn) -> g@[(f,fnpty g fn)]) [] fns in
  pty gamma [] e;;



(* Syntax *)

type id = EVar of string | Fname of string;;
type field = string;;

type expr =
    Var of id 
  | Bool of bool
  | Not of expr 
  | And of expr * expr 
  | Or of expr * expr 
  | Xor of expr * expr 
  | H of expr
  | F of expr * expr
  | S of expr * expr
  | V of expr * expr
  | String of string
  | Concat of expr * expr
  | Cid of int
  | Select of expr * expr * expr 
  | OT of expr * expr * expr 
  | Assign of expr * expr 
  | Appl of id * expr list
  | Record of (field * expr) list
  | Dot of expr * field
  | Let of id * expr * expr
  | Seq of expr * expr;;

type ppdf =
    Meet of ppdf * ppdf
  | Join of ppdf * ppdf
  | Comp of ppdf
  | Xord of ppdf * ppdf
  | Dist of expr 
  | DVar of string
  | Bot
  | Top;;

type jpdty =
    RecTy of (field * jpdty) list
  | StringTy of expr
  | CidTy of expr
  | Jpdf of ppdf
  | GenFn of jpdty list * (jpdty * views)
  | Unit

and views = (expr * jpdty) list;;

type bindings = (id * jpdty) list;;
  
type fndecl = id * bindings * expr;;

type progn = (fndecl list) * expr;;

(* Type Theory *)

exception TypeError of string;;

let synthesize t1s t2s =
  let rec synth = function
      (StringTy(Var(x)), StringTy(e)) -> ([], [(x,e)])
    | (CidTy(Var(x)), CidTy(e)) -> ([], [(x,e)])
    | (Jpdf(DVar(_) as alpha), Jpdf(mu)) -> ([(alpha, mu)], [])
    | (RecTy(fs1), RecTy(fs2)) ->
       let synthr ((f1, t1),(f2, t2)) =
         if f1 <> f2 then raise (TypeError "record field name mismatch") else synth (t1,t2)
       in
       List.fold_left
         (fun (ses,sts) x -> let (se,st) = synthr x in (ses@se, sts@st)) ([],[])
         (List.combine fs1 fs2)
    | (Unit,Unit) -> ([],[])
    | _ -> raise (TypeError "cannot synthesize")  
  in  
  List.fold_left
    (fun (ses,sts) x -> let (se,st) = synth x in (ses@se, sts@st)) ([],[])
    (List.combine t1s t2s);;

let substitute t (tsubs, esubs) =
  let rec esub = function
      (Var x) -> List.assoc x esubs 
    | (String s) -> (String s)
    | (Cid n) -> (Cid n)
    | (Concat(e1,e2)) ->
       (match (esub e1, esub e2) with
        | (String s1, String s2) -> String(s1 ^ s2)
        | (s1,s2) -> Concat(s1, s2))
    | (H(e)) -> H(esub e)
    | (V(e1,e2)) -> V(esub e1, esub e2)
    | (S(e1,e2)) -> S(esub e1, esub e2)
    | (F(e1,e2)) -> F(esub e1, esub e2)
    | _ -> raise (TypeError "unexpected expression in substitution")
  in
  let rec pdfsub = function
      (DVar x) -> List.assoc (DVar x) tsubs
    | (Meet(p1,p2)) -> Meet(pdfsub p1, pdfsub p2)
    | (Join(p1,p2)) -> Join(pdfsub p1, pdfsub p2)
    | (Xord(p1,p2)) -> Xord(pdfsub p1, pdfsub p2)
    | (Comp(p)) -> Comp(pdfsub p)
    | (Dist e) -> Dist(esub e)
    | Bot -> Bot
    | Top -> Top
  in
  let rec tsub = function
      (StringTy e) -> StringTy(esub e)
    | (CidTy e) -> CidTy(esub e)
    | (RecTy fs) -> RecTy(List.map (fun (f,t) -> (f, (tsub t))) fs)
    | (Jpdf(mu)) -> Jpdf(pdfsub mu)
    | Unit -> Unit
    | _ -> raise (TypeError "Pi type in subsitution")
  in
  tsub t;;
       
let rec pty (gamma : bindings) (views : views) = function 
    (Bool true) -> Jpdf(Top), views 
  | (Bool false) -> Jpdf(Bot), views
  | (Var x) -> (List.assoc x gamma), views
  | (Cid cid) -> CidTy(Cid cid), views
  | (String s) -> StringTy(String s), views
  | (Concat(e1, e2)) ->
     (match (pty gamma views e1) with
        (StringTy(s1), v1) ->
         (match (pty gamma v1 e2) with
            (StringTy(s2), v2) -> StringTy(Concat(s1,s2)), v2
          | _ ->  raise (TypeError "non-string type in string expression"))
      |  _ ->  raise (TypeError "non-string type in string expression")) 
  | (F(e1,e2)) ->
     (match (pty gamma views e1) with
        (CidTy p, vp) -> 
         (match (pty gamma vp e2) with
            (StringTy si, v) -> (Jpdf(Dist(F(p,si))), v)
          | _ -> raise (TypeError "not a string type in flip id"))
      | _ -> raise (TypeError "not a cid type in flip cid"))
  | (S(e1,e2)) ->
     (match (pty gamma views e1) with
        (CidTy p, vp) -> 
         (match (pty gamma vp e2) with
            (StringTy si, v) -> (Jpdf(Dist(S(p,si))), v)
          | _ -> raise (TypeError "not a string type in secret id"))
      | _ -> raise (TypeError "not a cid type in secret cid"))
  | (V(e1,e2)) ->
     (match (pty gamma views e1) with
        (CidTy p, vp) -> 
         (match (pty gamma vp e2) with
            (StringTy si, v) -> (Jpdf(Dist(V(p,si))), v)
          | _ -> raise (TypeError "not a string type in view id"))
      | _ -> raise (TypeError "not a cid type in view cid"))
  | (H e) ->
     (match (pty gamma views e) with
        (StringTy si, v) -> (Jpdf(Dist(H(si))), v)
      | _ -> raise (TypeError "not a string type in oracle id"))
  | (Assign (e1,e2)) ->
     (match e1 with
        (V(p, e)) ->
         (match (pty gamma views e) with
            (StringTy si, v1) ->
             (match (pty gamma v1 e2) with
                (Jpdf(mu), v2) -> (Unit, v2@[(V(p,si),Jpdf(mu))])
              | _ -> raise (TypeError "not a distribution type in assignment"))
          | _ -> raise (TypeError "not a string type in view id"))
      | _ -> raise (TypeError "non-view assignment"))
  | (And (e1, e2)) ->
     (match (pty gamma views e1) with
        (Jpdf(mu1), v1) ->
        (match (pty gamma v1 e2) with
           (Jpdf(mu2), v2) -> Jpdf(Meet(mu1, mu2)), v2
         | _ ->  raise (TypeError "non-pdf type in logical formula"))
      |  _ ->  raise (TypeError "non-pdf type in logical formula")) 
  | (Or (e1, e2)) ->
     (match (pty gamma views e1) with
        (Jpdf(mu1), v1) ->
        (match (pty gamma v1 e2) with
           (Jpdf(mu2), v2) -> Jpdf(Join(mu1, mu2)), v2
         | _ ->  raise (TypeError "non-pdf type in logical formula"))
      |  _ ->  raise (TypeError "non-pdf type in logical formula")) 
  | (Not e) ->
     (match (pty gamma views e) with
        (Jpdf(mu), v) -> Jpdf(Comp(mu)), v
      |  _ ->  raise (TypeError "non-pdf type in logical formula")) 
  | (Select (e1, e2, e3)) ->
     (match (pty gamma views e1) with
        (Jpdf(mu1), v1) ->
        (match (pty gamma v1 e2) with
           (Jpdf(mu2), v2) ->
            (match (pty gamma v2 e3) with
               (Jpdf(mu3), v3) ->
                Jpdf(Join(Meet(mu1,mu2), Meet(Comp(mu1),mu3))), v3 
             | _ ->  raise (TypeError "non-pdf type in logical formula"))
         |  _ ->  raise (TypeError "non-pdf type in logical formula"))
      |  _ ->  raise (TypeError "non-pdf type in logical formula"))
  | (Xor (e1, e2)) ->
     (match (pty gamma views e1) with
        (Jpdf(mu1), v1) ->
        (match (pty gamma v1 e2) with
           (Jpdf(mu2), v2) -> Jpdf(Xord(mu1,mu2)), v2
         | _ ->  raise (TypeError "non-pdf type in logical formula"))
      |  _ ->  raise (TypeError "non-pdf type in logical formula")) 
  | (Appl (f, args)) ->
     (match (List.assoc f gamma) with
        (GenFn(t1s, (t2, vf))) -> 
         let arg_tys,v =
           List.fold_left
             (fun (ts, v) -> fun e -> let t, v' = (pty gamma v e) in (ts@[t], v'))
             ([],views) args
         in
         let subs = synthesize t1s arg_tys in
         (substitute t2 subs, v@(List.map (fun (v,t) -> (v, substitute t subs)) vf))
      | _ -> raise (TypeError "wrong function type"))
  | (Let(x,e1,e2)) ->  let (t,v) = pty gamma views e1 in pty ((x,t)::gamma) v e2
  | (Seq(e1,e2)) -> let (_,v) = pty gamma views e1 in pty gamma v e2
  | (Dot(e,f)) ->
     (match (pty gamma views e) with
        (RecTy(fs), v) -> (List.assoc f fs, v)
      | _ -> raise (TypeError "record type in select"))
  | (Record fs) ->
     let (fts,v) =
       List.fold_left
         (fun (fts, v) (f,e) -> let (t,v') = pty gamma v e in (fts@[(f,t)],v'))
         ([],views) fs
     in
     (RecTy fts, v)
  | (OT (e1, e2, e3)) ->
      (match (pty gamma views e1) with
        (Jpdf(mu1), v1) ->
        (match (pty gamma v1 e2) with
          (Jpdf(mu2), v2) ->
            (match (pty gamma v2 e3) with
              (Jpdf(mu3), v3) ->
                Jpdf(Join(Meet(mu1,mu2), Meet(Comp(mu1),mu3))), v3 
            | _ ->  raise (TypeError "non-pdf type in logical formula"))
          |  _ ->  raise (TypeError "non-pdf type in logical formula"))
        |  _ ->  raise (TypeError "non-pdf type in logical formula"))
 
  | _ -> raise (TypeError "UNFINISHED");;