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
let extend_idx v = Hashtbl.add hidx v (Hashtbl.length hidx);;
let idx x = Hashtbl.find hidx x;;

let strue = "1";;
let sfalse = "0";;
let sbool b = if b then strue else sfalse;;

module TT = Set.Make(String);;

let meet = TT.inter;;
let join = TT.union;;
let diff = TT.diff;;

let gen_rows (m : mem) : TT.t =
  let deps = Mem.elements m in
  let rec gr = function
      0 -> if (List.mem_assoc 0 deps) then [sbool (List.assoc 0 deps)]
            else [strue;sfalse]
    | n -> 
        if (List.mem_assoc n deps) then List.map (fun r -> r ^ (sbool (List.assoc n deps))) (gr (n-1))
        else List.map (fun r -> r ^ strue) (gr (n-1)) @  List.map (fun r -> r ^ sfalse) (gr (n-1))
  in
  TT.of_list (gr ((Hashtbl.length hidx) - 1));;

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
    (gen_rows Mem.empty)
    views;;

let to_s vs = S.of_list (List.map idx (VS.elements vs));;

let ph = Hashtbl.create 50;;

let pdf_of_tt (xs : VS.t) (tt : TT.t) : dist =
  let s = to_s xs in
  let ms = gen_deps s in
  (* let ph = Hashtbl.create 50 in *)
  let ptt m =
    let dtt = TT.inter tt (gen_rows m) in
    Hashtbl.add ph m (float(TT.cardinal dtt) /. float(TT.cardinal tt)) in
  (MS.iter ptt ms; (s, (fun m -> Hashtbl.find ph m)))
    
let pdf p =
  let (_,views) = progty p in
  let (ss, fs, vs) = iovars views in (
  make_idx (VS.union ss fs);
  pdf_of_tt (VS.union ss vs) (truth_tables views));;

