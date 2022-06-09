type vart = Secret | View | Flip;;

type id = vart * int * int;;

type expr =
  Var of id |
  Bool of bool |
  Not of expr |
  And of expr * expr |
  Or of expr * expr |
  Xor of expr * expr |
  Assign of id * expr |
  Seq of expr * expr;;

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

let truth_tables e =
  let vtabs = create 0 in
  let rec tt = function
      (Bool true) -> (gen_rows [], TT.empty)
    | (Bool false) -> (TT.empty, gen_rows [])
    | (Var ((View,_,_) as v)) -> find vtabs v
    | (Var ((Secret,_,_) as s)) -> (gen_rows [(idx s, strue)], gen_rows [(idx s, sfalse)])
    | (Var ((Flip,_,_) as f)) -> (gen_rows [(idx f, strue)], gen_rows [(idx f, sfalse)]) 
    | (Not e) ->
       let (t,f) = tt e in (f,t)
    | (And (e1,e2)) ->
       let (t1,f1) = tt e1 in
       let (t2,f2) = tt e2 in
       (meet t1 t2, join f1 f2)
    | (Or (e1,e2)) ->
       let (t1,f1) = tt e1 in
       let (t2,f2) = tt e2 in
       (join t1 t2, meet f1 f2)
    | (Xor (e1,e2)) ->
       let (t1,f1) = tt e1 in
       let (t2,f2) = tt e2 in
       (join (meet t1 f2) (meet f1 t2), join (meet t1 t2) (meet f1 f2))
    | (Assign((View,p,j) as v,e)) -> 
       let (t,f) = tt e in (add vtabs v (t,f); (t,f))
    | (Seq(e1,e2)) -> (let _ = tt e1 in tt e2)
  in let _ = tt e in vtabs;;

let marg_dist inputs deps vtabs =
  let deptab =
    foldr (fun (x,b) tt ->
        match x with
          (View,_,_) as v ->
           let (t,f) = find vtabs v in
           if b = strue then meet t tt else meet f tt
        | (Secret,_,_) as s -> meet (gen_rows [(idx s,b)]) tt) deps (gen_rows [])
  in
  float(TT.cardinal (meet (gen_rows (map (fun (v,b) -> idx v,b) inputs)) deptab)) /.
    float(TT.cardinal deptab);;

let jpdf e vars = (make_idx vars; truth_tables e);;
  
let gen_deps xs =
  let rec gd = function
      0 -> [[]]
    | n -> let vs = gd (n-1) in
           map (fun r -> strue :: r) vs @  map (fun r -> sfalse :: r) vs
  in
  let vs = gd (List.length xs) in
  map (List.combine xs) vs;;

let check_leakage hi ci cv o vtabs = 
  let hdeps = gen_deps hi in
  let cdeps = gen_deps (ci@cv@[o]) in
  List.for_all
  (fun cdep ->
    List.for_all
      (fun hdep ->
        marg_dist hdep cdep vtabs =
          marg_dist hdep (List.filter (fun (x,_) -> List.mem x (ci@[o])) cdep) vtabs)
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
        let compare (x : t) (y : t) = compare x y (* if x = y then 0 else if x < y then -1 else 1 *)
      end);;

let vars e =
  let rec vs = function
    (Bool _) -> (VS.empty,VS.empty,VS.empty)
  | (Var ((View,_,_) as v)) -> (VS.empty,VS.empty,VS.singleton v)
  | (Var ((Secret,_,_) as s)) -> (VS.singleton s,VS.empty,VS.empty)
  | (Var ((Flip,_,_) as f)) -> (VS.empty,VS.singleton f,VS.empty)
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
  | (Assign((View,p,j) as v',e)) -> 
     let (s,f,v) = vs e in (s,f,VS.union (VS.singleton v') v)     
  | (Seq(e1,e2)) -> 
     let (s1,f1,v1) = vs e1 in
     let (s2,f2,v2) = vs e2 in 
     (VS.union s1 s2,VS.union f1 f2,VS.union v1 v2)
  in
  let (s,f,v) = vs e in (VS.elements s,VS.elements f,VS.elements v);;

(*
  passive_secure : expr -> int -> id -> bool
  in : protocol e, number of parties n, output view o
  out : true iff e is passive secure for n parties.
 *)
let passive_secure e n o =
  (* find the different types of variables- secrets, flips, views- in the protocol *)
  let (s,f,v) = vars e in
  (* generate the jpdf for the expressive given variables *)
  let pdf = jpdf e (s@f) in
  (* enumerate all partitions of parties into honest and corrupt sets *)
  let partitions = group (enumerate n) [n - (n/2); (n/2)] in
  (* search for a witness of unequal ideal and adversarial knowledge *)
  List.for_all
    (fun [h;c] ->
      let hi = List.filter (fun (l,pi,_) -> l = Secret && List.mem pi h) s  in
      let ci = List.filter (fun (l,pi,_) -> l = Secret && List.mem pi c) s in
      let cv = List.filter (fun (l,pi,_) -> l = View && List.mem pi c) v in
      check_leakage hi ci cv o pdf) partitions;;




