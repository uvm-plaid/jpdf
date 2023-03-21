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

let dom (m : mem) : mids = S.of_list (fst(List.split (Mem.elements m)));;

type dist = mids * (mem -> float);;

let expand (ms : mems) ids =
  let ex ms i = 
    MS.union
      (MS.map (fun m -> Mem.add (i, true) m) ms)
      (MS.map (fun m -> Mem.add (i, false) m) ms)
  in
  S.fold (fun i ms -> ex ms i) ids ms;;

let gen_deps (s : mids) : mems = expand (MS.singleton (Mem.empty)) s;;

let margd (s1 : mids) (s2, d : dist) : dist =
  let deps = gen_deps (S.diff s2 s1) in
  s1, (fun (ma : Mem.t) -> List.fold_left (fun p mb -> (d (Mem.union ma mb)) +. p) 0.0 ((MS.elements) deps));;

let condd mb (s,d : dist) : dist =
  S.diff s (dom mb), (fun (ma : Mem.t) ->
    let p = ((snd (margd (dom mb) (s,d))) mb) in
    if p = 0.0 then 0.0 else (d (Mem.union ma mb)) /. p);;

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
let idx x = Hashtbl.find hidx x;;
let strue = "1";;
let sfalse = "0";;

module TT = Set.Make(String);;

let meet = TT.inter;;
let join = TT.union;;
let diff = TT.diff;;

let gen_rows deps =
  let rec gr = function
      0 -> if (List.mem_assoc 0 deps) then [(List.assoc 0 deps)]
            else [strue;sfalse]
    | n -> 
        if (List.mem_assoc n deps) then List.map (fun r -> r ^ (List.assoc n deps)) (gr (n-1))
        else List.map (fun r -> r ^ strue) (gr (n-1)) @  List.map (fun r -> r ^ sfalse) (gr (n-1))
  in
  TT.of_list (gr ((Hashtbl.length hidx) - 1));;

(*
 This uses the views type from hybrid.ml and the TT type from jpd.ml.
 It also assumes the extension of the domain of idx to include views,
 with each view index in the order of its declaration and all view indices
 greater than secret and flip indices. 
*)
let truth_tables (views : views) start : TT.t =
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
      let tp = tt ptt p in
      (* due to assumptions about idx, string concatenation comports with indices *)	 
      join (TT.map (fun r -> r ^ strue) tp) (TT.map (fun r -> r ^ sfalse) (diff ptt tp)))
    (* assume this call behaves as in jpd.ml- it just generates the tt for secrets and flips,
        not views, and is the basis of the folding *)
    start
    views;;

(* Example of use *)
(*
let (ex0 : progn) = 
[],
Let(EVar("f0"), F(Cid(1), String("0")),
    Let(EVar("f1"), F(Cid(1), String("1")),
        Let(EVar("f2"), Select(S(Cid(1), String("0")),Var(EVar("f0")),Var(EVar("f1"))),
            Seq(
              Assign(V(Cid(0), String("0")), Var(EVar("f2"))),
              Assign(V(Cid(0), String("1")), Var(EVar("f0"))))))) in
let (_,views) = progty ex0 in
let (ss, fs, vs) = iovars views in
let start = make_idx (VS.union ss fs); gen_rows [] in
let table = make_idx (VS.union (VS.union ss fs) vs); truth_tables views start in
TT.elements table;;
*)