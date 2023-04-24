module S = Set.Make(Int);;
type mid = S.elt;;
type mids = S.t;;

module Mem =
  Set.Make(
      struct
        type t = mid * bool
        let compare (x : t) (y : t) = compare x y
      end);;

type mem = Mem.t;;

module MS = Set.Make(Mem);;

type mems = MS.t

let expand (ms : mems) ids =
  let ex ms i = 
    MS.union
      (MS.map (fun m -> Mem.add (i, true) m) ms)
      (MS.map (fun m -> Mem.add (i, false) m) ms)
  in
  S.fold (fun i ms -> ex ms i) ids ms;;

let gen_deps (s : mids) : mems = expand (MS.singleton (Mem.empty)) s;;

module VS =
  Set.Make(
      struct
        type t = expr
        let compare (x : t) (y : t) = compare x y
      end);;

let iovars (views : views) =
  let rec vs = function
      Top -> (VS.empty, VS.empty)
    | Bot -> (VS.empty, VS.empty)
    | Dist(V(_)) -> (VS.empty,VS.empty)
    | Dist((H(_)) as f) -> (VS.empty,VS.singleton f)
    | Dist((F(_)) as f) -> (VS.empty,VS.singleton f)
    | Dist((S(_)) as s) -> (VS.singleton s,VS.empty)
    | Meet(p1, p2) -> 
        let (s1,f1) = vs p1 in
        let (s2,f2) = vs p2 in
        (VS.union s1 s2,VS.union f1 f2)
    | Join(p1, p2) -> 
        let (s1,f1) = vs p1 in
        let (s2,f2) = vs p2 in
        (VS.union s1 s2,VS.union f1 f2)
    | Xord(p1, p2) -> 
        let (s1,f1) = vs p1 in
        let (s2,f2) = vs p2 in
        (VS.union s1 s2,VS.union f1 f2)
    | Comp(p) -> vs p
    | _ -> raise (TypeError "free variable encountered in iovars")
  in
  List.fold_left
    (fun (ss,fs,vidss) (v, Jpdf(p)) ->
      let (s,f) = vs p in (VS.union s ss, VS.union f fs, VS.add v vidss))
    (VS.empty, VS.empty, VS.empty) views

let hidx = Hashtbl.create 0;;
let make_idx vars =
  (Hashtbl.reset hidx; let i = ref 0 in VS.iter (fun x -> Hashtbl.add hidx x !i; i := !i + 1) vars);;
let extend_idx v = Hashtbl.add hidx v (Hashtbl.length hidx);;
let idx x = Hashtbl.find hidx x;;

let strue = "1";;
let sfalse = "0";;
let sbool b = if b then strue else sfalse;;

module TT = Set.Make(String);;

let meet = TT.inter;;
let join = TT.union;;
let diff = TT.diff;;

let gen_start n = 
  let l = ref [""] in
  for i = 0 to n-1 do
    let old = !l in
    l := [];
    List.iter (fun x -> l := (x ^ sfalse)::!l) old;
    List.iter (fun x -> l := (x ^ strue)::!l) old;
  done;
  TT.of_list !l;;

let truth_tables (views : views) =
  let rec tt ptt = function
      Top -> ptt
    | Bot -> TT.empty
    | Dist(x) -> TT.filter (fun r -> r.[idx x] = '1') ptt
    | Meet(p1, p2) -> meet (tt ptt p1) (tt ptt p2)
    | Join(p1, p2) -> join (tt ptt p1) (tt ptt p2)
    | Comp(p) -> diff ptt (tt ptt p)
    | Xord(p1, p2) ->
        let tp1 = tt ptt p1 in
        let tp2 = tt ptt p2 in
        join (meet tp1 (diff ptt tp2)) (meet (diff ptt tp1) tp2)
    | _ -> raise (TypeError "free variable encountered in truth_tables")
  in
  List.fold_left
    (fun ptt (v, Jpdf(p)) ->
      (extend_idx v;
        let tp = tt ptt p in
        join (TT.map (fun r -> r ^ strue) tp) (TT.map (fun r -> r ^ sfalse) (diff ptt tp))))
    (gen_start (Hashtbl.length hidx))
    views;;

let cdist_tt tt hdep ldep =
  let filt_tt deps tt = TT.filter (fun r -> List.for_all (fun (i,b) -> r.[i] = b.[0]) deps) tt in
  let lowtt = filt_tt ldep tt in 
  let hitt = filt_tt hdep lowtt in
  float(TT.cardinal hitt) /. float(TT.cardinal lowtt);;

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

let witness = ref ([],[]);;

let mems_to_lists x = List.map (fun y -> List.map (fun (i, b) -> (i, sbool b)) (Mem.elements y)) (MS.elements x)

let check_leakage hi ci cv o tt = 
  let hdeps = gen_deps (S.of_list (List.map (fun x -> idx x) (VS.elements hi))) in
  let cdeps = gen_deps (S.of_list (List.map (fun x -> idx x) (VS.elements (VS.union (VS.union ci cv) o)))) in
  List.for_all
  (fun cdep ->
    List.for_all
      (fun hdep ->
        if (cdist_tt tt (hdep@cdep) []) = 0.0 then true else
          let p1 = cdist_tt tt hdep cdep in
          (* This line might be wrong? *)
          let ci_o = List.map (fun x -> idx x) (VS.elements (VS.union ci o)) in
          let p2 = cdist_tt tt hdep (List.filter (fun (x,_) -> List.mem x (ci_o)) cdep) in
          if p1 = p2 then true else (witness := (hdep, cdep); false)
      )
      (mems_to_lists hdeps)) (mems_to_lists cdeps);;

(*
  passive_secure : expr -> int -> id -> bool
  in : protocol e, number of parties n, output view o in the form of an expression like (V(Cid(0), String("1")))
  out : true iff e is passive secure for n parties.
*)
let passive_secure e n (V(Cid(p), _) as o) =
  let (_,views) = progty e in
  let (ss, fs, vs) = iovars views in
  make_idx (VS.union ss fs);
  let pdf = truth_tables views in
  (* enumerate all partitions of parties into honest and corrupt sets. Each element 
     of partitions is a pair h,c which is a 2-set partition of parties, where the size
     of c (the corrupt parties) is |n|/2 with h the honest majority. *)
  let partitions = group (enumerate n) [n - (n/2); (n/2)] in
  (* For every honest,corrupt partition (h,c), search for a witness of unequal 
     ideal and adversarial knowledge *)
  List.for_all
    (fun [h;c] ->
      (* List all the honest input variables as hi *)
      let hi = VS.filter (fun (S(Cid(i), _)) -> List.mem i h) ss in
      (* List all the corrupt input variables as ci *)
      let ci = VS.filter (fun (S(Cid(i), _)) -> List.mem i c) ss in
      (* List all the corrupt views as cv *)
      let cv = VS.filter (fun (V(Cid(i), _)) -> List.mem i (p::c)) vs in
      (* Letting P be jpdf encoded as pdf, in check leakage we check:
                   P(hi|ci|o) = P(hi|ci|cv|o). 
         We do this by iterating over all possible assignments of hi, ci, cv, and o, and 
         checking equality of their marginal distributions. *)
      check_leakage hi ci cv (VS.singleton o) pdf) 
  partitions;;