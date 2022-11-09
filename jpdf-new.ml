(* Syntax *)

type id = SVar of string | TVar of string | Fname of string;;
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
  | Dist of expr * bool
  | DVar of string
  | Bot
  | Top;;

type jpdty =
    RecTy of (field * jpdty) list
  | StringTy of expr
  | CidTy of expr
  | Jpdf of ppdf * ppdf
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
      (StringTy(Var(SVar(x))), StringTy(e)) -> ([], [((SVar x),e)])
    | (CidTy(Var(SVar(x))), CidTy(e)) -> ([], [((SVar x),e)])
    | (Jpdf((DVar(_) as alpha), (DVar(_) as beta)), Jpdf(pdf1, pdf2)) ->
       ([(alpha, pdf1); (beta, pdf2)], [])
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
      (Var(SVar x)) -> List.assoc (SVar x) esubs 
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
    | (Dist(e,b)) -> Dist(esub e,b)
    | Bot -> Bot
    | Top -> Top
  in
  let rec tsub = function
      (StringTy e) -> StringTy(esub e)
    | (CidTy e) -> CidTy(esub e)
    | (RecTy fs) -> RecTy(List.map (fun (f,t) -> (f, (tsub t))) fs)
    | (Jpdf(pdft, pdff)) -> Jpdf(pdfsub pdft, pdfsub pdff)
    | Unit -> Unit
    | _ -> raise (TypeError "Pi type in subsitution")
  in
  tsub t;;
       
let rec pty (gamma : bindings) (views : views) = function 
    (Bool true) -> Jpdf(Top, Bot), views 
  | (Bool false) -> Jpdf(Bot, Top), views
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
            (StringTy si, v) -> (Jpdf((Dist(F(p,si),true)),(Dist(F(p,si),false))), v)
          | _ -> raise (TypeError "not a string type in flip id"))
      | _ -> raise (TypeError "not a cid type in flip cid"))
  | (S(e1,e2)) ->
     (match (pty gamma views e1) with
        (CidTy p, vp) -> 
         (match (pty gamma vp e2) with
            (StringTy si, v) -> (Jpdf((Dist(S(p,si),true)),(Dist(S(p,si),false))), v)
          | _ -> raise (TypeError "not a string type in secret id"))
      | _ -> raise (TypeError "not a cid type in secret cid"))
  | (V(e1,e2)) ->
     (match (pty gamma views e1) with
        (CidTy p, vp) -> 
         (match (pty gamma vp e2) with
            (StringTy si, v) -> (Jpdf((Dist(V(p,si),true)),(Dist(V(p,si),false))), v)
          | _ -> raise (TypeError "not a string type in view id"))
      | _ -> raise (TypeError "not a cid type in view cid"))
  | (H e) ->
     (match (pty gamma views e) with
        (StringTy si, v) -> (Jpdf((Dist(H(si),true)),(Dist(H(si),false))), v)
      | _ -> raise (TypeError "not a string type in oracle id"))
  | (Assign (e1,e2)) ->
     (match e1 with
        (V(p, e)) ->
         (match (pty gamma views e) with
            (StringTy si, v1) ->
             (match (pty gamma v1 e2) with
                ((Jpdf(t,f)) as t2, v2) -> (Unit, v2@[(V(p,si),t2)])
              | _ -> raise (TypeError "not a distribution type in assignment"))
          | _ -> raise (TypeError "not a string type in view id"))
      | _ -> raise (TypeError "non-view assignment"))
  | (And (e1, e2)) ->
     (match (pty gamma views e1) with
        (Jpdf(t1,f1), v1) ->
        (match (pty gamma v1 e2) with
           (Jpdf(t2,f2), v2) -> Jpdf(Meet(t1, t2), Join(f1,f2)), v2
         | _ ->  raise (TypeError "non-pdf type in logical formula"))
      |  _ ->  raise (TypeError "non-pdf type in logical formula")) 
  | (Xor (e1, e2)) ->
     (match (pty gamma views e1) with
        (Jpdf(t1,f1), v1) ->
        (match (pty gamma v1 e2) with
           (Jpdf(t2,f2), v2) ->
            Jpdf(Join(Meet(t1, f2),Meet(f1,t2)), Join(Meet(t1,t2),Meet(f1,f2))), v2
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

(* PDF solver and marginal distribution query *)

open Hashtbl;;

let map = List.map;;
let foldr = List.fold_right;;
          
module TT = Set.Make(String);;

let meet = TT.inter;;
let join = TT.union;;

let strue = "1";;
let sfalse = "0";;

let hidx = create 0;;

module VS =
  Set.Make(
      struct
        type t = expr
        let compare (x : t) (y : t) = compare x y
      end);;

let idx x = find hidx x;;

let make_idx vars =
  (reset hidx; let i = ref 0 in List.iter (fun x -> add hidx x !i; i := !i + 1) vars);;

let gen_rows deps =
  let rec gr = function
      0 -> if (List.mem_assoc 0 deps) then [(List.assoc 0 deps)]
           else [strue;sfalse]
    | n -> 
       if (List.mem_assoc n deps) then map (fun r -> (List.assoc n deps) ^ r) (gr (n-1))
       else map (fun r -> strue ^ r) (gr (n-1)) @  map (fun r -> sfalse ^ r) (gr (n-1))
  in
  TT.of_list (gr ((length hidx) - 1));;

let truth_tables views =
  let vtabs = create 0 in
  let rec tt = function
      Top -> gen_rows []
    | Bot -> TT.empty
    | Dist((V(_)) as v, true) -> fst(find vtabs v)
    | Dist((V(_)) as v, false) -> snd(find vtabs v)
    | Dist(x, true) ->  gen_rows [(idx x, strue)]
    | Dist(x, false) -> gen_rows [(idx x, sfalse)]
    | Meet(p1, p2) -> meet (tt p1) (tt p2)
    | Join(p1, p2) -> join (tt p1) (tt p2)
    | _ -> raise (TypeError "free variable encountered in truth_tables")
  in
  (ignore(map (fun (v, Jpdf(p1,p2)) -> add vtabs v (tt p1, tt p2)) views);
   vtabs)

let iovars views =
  let rec vs = function
      Top -> (VS.empty, VS.empty)
    | Bot -> (VS.empty, VS.empty)
    | Dist(V(_), _) -> (VS.empty,VS.empty)
    | Dist((H(_)) as f, _) -> (VS.empty,VS.singleton f)
    | Dist((F(_)) as f, _) -> (VS.empty,VS.singleton f)
    | Dist((S(_)) as s, _) -> (VS.singleton s,VS.empty)
    | Meet(p1, p2) -> 
       let (s1,f1) = vs p1 in
       let (s2,f2) = vs p2 in
       (VS.union s1 s2,VS.union f1 f2)
    | Join(p1, p2) -> 
       let (s1,f1) = vs p1 in
       let (s2,f2) = vs p2 in
       (VS.union s1 s2,VS.union f1 f2)
    | _ -> raise (TypeError "free variable encountered in iovars")
  in
  let (ss, fs, vs) =
    List.fold_left
      (fun (ss,fs,views) (v,Jpdf(t,f)) ->
        let (st,ft) = vs t in
        let (sf,ff) = vs f in
        (VS.union (VS.union ss st) sf,
         VS.union (VS.union fs ft) ff,
         VS.union views (VS.singleton v)))
      (VS.empty, VS.empty, VS.empty) views
  in
  (VS.elements ss, VS.elements fs, VS.elements vs)

let jpdf views vars = (make_idx vars; truth_tables views);;

let solve views =  let (s,f,_) = iovars views in jpdf views (s@f);;

let genpdf e = solve (snd (progty e));;

let marg_dist inputs deps vtabs =
  let f =
    (fun (x,b) tt ->
      match x with
        V(_) as v ->
         let (t,f) = find vtabs v in
         if b = strue then meet t tt else meet f tt
      | S(_) as s -> meet (gen_rows [(idx s,b)]) tt
      | F(_) as f -> meet (gen_rows [(idx f,b)]) tt
      | H(_) as f -> meet (gen_rows [(idx f,b)]) tt)
  in
  let deptab = foldr f deps (gen_rows []) in
  let intab = foldr f inputs deptab in
  float(TT.cardinal intab) /. float(TT.cardinal deptab);;

(* Simulator security analysis *)

let gen_deps xs =
  let rec gd = function
      0 -> [[]]
    | n -> let vs = gd (n-1) in
           map (fun r -> strue :: r) vs @  map (fun r -> sfalse :: r) vs
  in
  if xs = [] then [] else
    let vs = gd (List.length xs) in
    map (List.combine xs) vs;;

let witness = ref ([],[]);;
              
let check_leakage hi ci cv o vtabs = 
  let hdeps = gen_deps hi in
  let cdeps = gen_deps (ci@cv@[o]) in
  List.for_all
  (fun cdep ->
    List.for_all
      (fun hdep ->
        if (marg_dist (hdep@cdep) [] vtabs) = 0.0 then true else
          let p1 = marg_dist hdep cdep vtabs in
          let p2 = marg_dist hdep (List.filter (fun (x,_) -> List.mem x (ci@[o])) cdep) vtabs in
          if p1 = p2 then true else (witness := (hdep, cdep); false)
      )
      hdeps) cdeps

let rec enumerate = function
    0 -> []
  | n -> n :: enumerate (n-1);;

(*
  code borrowed from https://ocaml.org/problems
 *)
let group list sizes =
  let initial = List.map (fun size -> size, []) sizes in
  let prepend p list =
    let emit l acc = l :: acc in
    let rec aux emit acc = function
      | [] -> emit [] acc
      | (n, l) as h :: t ->
         let acc = if n > 0 then emit ((n - 1, p :: l) :: t) acc
                   else acc in
         aux (fun l acc -> emit (h :: l) acc) acc t
    in
    aux emit [] list
  in
  let rec aux = function
    | [] -> [initial]
    | h :: t -> List.concat (List.map (prepend h) (aux t))
  in
  let all = aux list in
  let complete = List.filter (List.for_all (fun (x, _) -> x = 0)) all in
  List.map (List.map snd) complete;;
