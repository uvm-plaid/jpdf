(* Syntax *)

type cid  = int;;
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
  | F of cid * expr
  | S of cid * expr
  | V of cid * expr
  | String of string
  | Concat of expr * expr
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
    | (Concat(e1,e2)) ->
       (match (esub e1, esub e2) with
        | (String s1, String s2) -> String(s1 ^ s2)
        | (s1,s2) -> Concat(s1, s2))
    | (H(e)) -> H(esub e)
    | (V(p,e)) -> V(p, esub e)
    | (S(p,e)) -> S(p, esub e)
    | (F(p,e)) -> F(p, esub e)
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
  | (String s) -> StringTy(String s), views
  | (Concat(e1, e2)) ->
     (match (pty gamma views e1) with
        (StringTy(s1), v1) ->
         (match (pty gamma v1 e2) with
            (StringTy(s2), v2) -> StringTy(Concat(s1,s2)), v2
          | _ ->  raise (TypeError "non-string type in string expression"))
      |  _ ->  raise (TypeError "non-string type in string expression")) 
  | (F(p,e)) ->
     (match (pty gamma views e) with
        (StringTy si, v) -> (Jpdf((Dist(F(p,si),true)),(Dist(F(p,si),false))), v)
      | _ -> raise (TypeError "not a string type in flip id"))
  | (S(p,e)) ->
     (match (pty gamma views e) with
        (StringTy si, v) -> (Jpdf((Dist(S(p,si),true)),(Dist(S(p,si),false))), v)
      | _ -> raise (TypeError "not a string type in secret id"))
  | (V(p,e)) ->
     (match (pty gamma views e) with
        (StringTy si, v) -> (Jpdf((Dist(V(p,si),true)),(Dist(V(p,si),false))), v)
      | _ -> raise (TypeError "not a string type in view id"))
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
  | _ -> raise (TypeError "UNFINISHED");;

let fnpty (gamma : bindings) ((f, params, e) : fndecl) =
  GenFn((List.map (fun (x,t) -> t) params),(pty (params@gamma) [] e));;

let progty ((fns, e) : progn) =
  let gamma = List.fold_left (fun g ((f,_,_) as fn) -> g@[(f,fnpty g fn)]) [] fns in
  pty gamma [] e;;


(*
   Here (f, [(x1,t1),...,(xn,tn)], t, e) : GenFn([t1;...;tn],t) means:
 
    f(x1 : t1, ..., xn tn) : t { e } :  \forall ftv(t1,...,tn) . \Pi fsv(t1,...,tn) . (t1 * ... * tn) -> t
  
   with the restriction that any string variables occuring in t1...tn are not concatenated 
   nor occur within a jpdf type.

   Example:

   f(x : jpdf('a, 'b), s : string(s))
   {
      H[s || "foo"] and x
   }

     : 

   \forall 'a, 'b, \Pi s . 
        jpdf('a, 'b), s : string(s) -> jpdf ((H[s || "foo"]|T meet 'a, (H[s || "foo"]|F join 'b)))

   f(H["bar"],"baz") :  jpdf ((H["bazfoo"]|T meet H["bar"]|T, (H["bazfoo"]|F join H["bar]|F)))
 *)


let (ex1 : progn) =
  [
  (Fname("f"),
   [(TVar("x"), Jpdf(DVar("a"),DVar("b"))); (SVar("s"), StringTy(Var(SVar("s"))))],
   And(H(Concat(Var(SVar("s")), String("foo"))), Var(TVar("x")))
  )
  ], Assign(V(0,String("pub")), Appl(Fname("f"), [F(1, String("bar")); String("baz")]));;


(* PDF solver and analysis *)


open Hashtbl;;

let map = List.map;;
let foldr = List.fold_right;;
          
module TT = Set.Make(String);;

let meet = TT.inter;;
let join = TT.union;;

let strue = "1";;
let sfalse = "0";;
let hidx = create 0;;

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
    | Dist((V(_,_)) as v, true) -> fst(find vtabs v)
    | Dist((V(_,_)) as v, false) -> snd(find vtabs v)
    | Dist(x, true) ->  gen_rows [(idx x, strue)]
    | Dist(x, false) -> gen_rows [(idx x, sfalse)]
    | Meet(p1, p2) -> meet (tt p1) (tt p2)
    | Join(p1, p2) -> join (tt p1) (tt p2)
    | _ -> raise (TypeError "free variable encountered in solution")
  in
  (ignore(map (fun (v, Jpdf(p1,p2)) -> add vtabs v (tt p1, tt p2)) views);
   vtabs)

let marg_dist inputs deps vtabs =
  let f =
    (fun (x,b) tt ->
      match x with
        (View,_,_) as v ->
         let (t,f) = find vtabs v in
         if b = strue then meet t tt else meet f tt
      | (Secret,_,_) as s -> meet (gen_rows [(idx s,b)]) tt
      | (Flip,_,_) as f -> meet (gen_rows [(idx f,b)]) tt)
  in
  let deptab = foldr f deps (gen_rows []) in
  let intab = foldr f inputs deptab in
  float(TT.cardinal intab) /. float(TT.cardinal deptab);;
  
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

module VS =
  Set.Make(
      struct
        type t = id
        let compare (x : t) (y : t) = compare x y
      end);;

let iovars e =
  let rec vs = function
    (Bool _) -> (VS.empty,VS.empty,VS.empty)
  | (Var ((View,_,_) as v)) -> (VS.empty,VS.empty,VS.singleton v)
  | (Var ((Secret,_,_) as s)) -> (VS.singleton s,VS.empty,VS.empty)
  | (Var ((Flip,_,_) as f)) -> (VS.empty,VS.singleton f,VS.empty)
  | (Var (Local,_,_)) -> (VS.empty,VS.empty,VS.empty)
  | (Not e) -> vs e
  | (And (e1,e2)) ->
     let (s1,f1,v1) = vs e1 in
     let (s2,f2,v2) = vs e2 in
     (VS.union s1 s2,VS.union f1 f2,VS.union v1 v2)
  | (Or (e1,e2)) ->
     let (s1,f1,v1) = vs e1 in
     let (s2,f2,v2) = vs e2 in
     (VS.union s1 s2,VS.union f1 f2,VS.union v1 v2)
  | (Xor (e1,e2)) ->
     let (s1,f1,v1) = vs e1 in
     let (s2,f2,v2) = vs e2 in 
     (VS.union s1 s2,VS.union f1 f2,VS.union v1 v2)
  | (Select (e1,e2,e3)) ->
     let (s1,f1,v1) = vs e1 in
     let (s2,f2,v2) = vs e2 in 
     let (s3,f3,v3) = vs e3 in 
     (VS.union (VS.union s1 s2) s3,VS.union (VS.union f1 f2) f3,VS.union (VS.union v1 v2) v3)
  | (Assign((View,p,j) as v',e)) -> 
     let (s,f,v) = vs e in (s,f,VS.union (VS.singleton v') v)  
  | (Assign((Local,p,j) as v',e)) -> vs e
  | (Seq(e1,e2)) -> 
     let (s1,f1,v1) = vs e1 in
     let (s2,f2,v2) = vs e2 in 
     (VS.union s1 s2,VS.union f1 f2,VS.union v1 v2)
  in
  let (s,f,v) = vs e in (VS.elements s,VS.elements f,VS.elements v);;

let jpdf e vars = (make_idx vars; truth_tables e);;

let genpdf e = let (s,f,_) = iovars e in jpdf e (s@f);;

(*
  passive_secure : expr -> int -> id -> bool
  in : protocol e, number of parties n, output view o
  out : true iff e is passive secure for n parties.
*)
let passive_secure e n o =
  (* find the different types of variables- secrets, flips, views- in the protocol *)
  let (s,f,v) = iovars e in
  (* generate the jpdf for the expression given input variables. The jpdf is encoded 
     as a mapping from view ids to their truth tables.  *)
  let pdf = jpdf e (s@f) in
  (* enumerate all partitions of parties into honest and corrupt sets. Each element 
     of partitions is a pair h,c which is a 2-set partition of parties, where the size
     of c (the corrupt parties) is |n|/2 with h the honest majority. *)
  let partitions = group (enumerate n) [n - (n/2); (n/2)] in
  (* For every honest,corrupt partition (h,c), search for a witness of unequal 
     ideal and adversarial knowledge *)
  List.for_all
    (fun [h;c] ->
      (* List all the honest input variables as hi *)
      let hi = List.filter (fun (l,pi,_) -> l = Secret && List.mem pi h) s  in
      (* List all the corrupt input variables as ci *)
      let ci = List.filter (fun (l,pi,_) -> l = Secret && List.mem pi c) s in
      (* List all the corrupt views as cv *)
      let cv = List.filter (fun (l,pi,_) -> l = View && List.mem pi c) v in
      (* Letting P be jpdf encoded as pdf, in check leakage we check:
                   P(hi|ci|o) = P(hi|ci|cv|o). 
         We do this by iterating over all possible assignments of hi, ci, cv, and o, and 
         checking equality of their marginal distributions. *)
      check_leakage hi ci cv o pdf) 
  partitions;;




