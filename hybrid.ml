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

let restrict (m : mem) (s : mids) : mem = Mem.filter (fun (id,_) -> S.mem id s) m;;

let margd (s1 : mids) (s2, d : dist) : dist =
  let deps = gen_deps (S.diff s2 s1) in
  s1, (fun (ma : Mem.t) -> List.fold_left (fun p mb -> (d (Mem.union ma mb)) +. p) 0.0 ((MS.elements) deps));;

let condd mb (s,d : dist) : dist =
  S.diff s (dom mb), (fun (ma : Mem.t) ->
    let p = ((snd (margd (dom mb) (s,d))) mb) in
    if p = 0.0 then 0.0 else (d (Mem.union ma mb)) /. p);;

let meetd (s1,d1 : dist) (s2,d2 as mu2 : dist) : dist =    (* P(A and B) = P(A) * P(B|A) *)
  S.union s1 s2,
  (fun m ->
    let s = S.inter s1 s2 in
    if S.is_empty s then d1(restrict m s1) *. d2(restrict m s2)
    else d1(restrict m s1) *. (snd(condd (restrict m s) mu2) (restrict m (S.diff s2 s))));;

exception MemDomain;;

let rec pow a = function
  | 0 -> 1
  | 1 -> a
  | n -> 
    let b = pow a (n / 2) in
    b * b * (if n mod 2 = 0 then 1 else a)

let initd (s : mids) : dist =
  let prob = 1.0 /. float_of_int(pow 2 (S.cardinal s)) in 
  s, (fun m -> if S.equal (dom m) s then prob else raise MemDomain);;

let uni (ms : mems) : dist =
  let s = dom (MS.choose ms) in
  s, 
  (fun (m : Mem.t) ->
    if (S.equal (dom m) s) then
      if (MS.mem m ms) then (1.0 /. float_of_int(MS.cardinal ms))
      else 0.0
    else raise MemDomain);;

let meet ms1 ms2 =
  if MS.is_empty ms1 || MS.is_empty ms2 then ms1 else
    let s1 = dom (MS.choose ms1) in
    let s2 = dom (MS.choose ms2) in
    let ms1' = expand ms1 (S.diff s2 s1) in
    let ms2' = expand ms2 (S.diff s1 s2) in
    MS.inter ms1' ms2';;

let join ms1 ms2 =
  if MS.is_empty ms1 then ms2 else if MS.is_empty ms2 then ms1 else
    let s1 = dom (MS.choose ms1) in
    let s2 = dom (MS.choose ms2) in
    let ms1' = expand ms1 (S.diff s2 s1) in
    let ms2' = expand ms2 (S.diff s1 s2) in
    MS.union ms1' ms2';;

let comp ms =
  let allms = gen_deps (dom (MS.choose ms)) in
  MS.diff allms ms 
  
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
let idxs (vs : VS.t) : mids = VS.fold (fun x ids -> S.add (idx x) ids) vs S.empty;;

let ms_top = MS.singleton (Mem.empty);;

let ms_bot = MS.empty;;

let ms ml = MS.singleton (Mem.of_list (List.map (fun (i,v) -> (idx i, v)) ml));;

let truth_tables (views : views) : mems list =
  let rec tt = function
      Top -> ms_top
    | Bot -> ms_bot
    | Dist(x) -> ms [x,true]
    | Meet(p1, p2) -> meet (tt p1) (tt p2)
    | Join(p1, p2) -> join (tt p1) (tt p2)
    | Comp(p) -> comp (tt p)
    | Xord(p1, p2) ->
       let tp1 = tt p1 in
       let tp2 = tt p2 in
       join (meet tp1 (comp tp2)) (meet (comp tp1) tp2)
    | _ -> raise (TypeError "free variable encountered in truth_tables")
  in
  List.map
    (fun (v, Jpdf(p)) ->
      let tp = tt p in
      join (meet tp (ms [v,true])) (meet (comp tp) (ms [v,false])))
    views;;

let viewsd invars vtts : dist = List.fold_left (fun mu tt -> meetd mu (uni tt)) (initd invars) vtts;;

let viewsd_tabular invars vtts : dist = uni (List.fold_left (fun ftt tt -> meet ftt tt) (gen_deps invars) vtts);;

let to_s vs = S.of_list (List.map (fun x -> idx x) (VS.elements vs));;

let tts p =
  let (_,views) = progty p in
  let (ss, fs, vs) = iovars views in (
  make_idx (VS.union ss (VS.union fs vs));
  truth_tables views);;

let pdf p =
  let (_,views) = progty p in
  let (ss, fs, vs) = iovars views in (
  make_idx (VS.union ss (VS.union fs vs));
  viewsd (S.union (to_s ss) (to_s fs)) (truth_tables views));;

let pdft p =
  let (_,views) = progty p in
  let (ss, fs, vs) = iovars views in (
  make_idx (VS.union ss (VS.union fs vs));
  viewsd_tabular (S.union (to_s ss) (to_s fs)) (truth_tables views));;
  
let query mu hdep ldep =
  let tomem deps = Mem.of_list (List.map (fun (x,b) -> idx x, b) deps) in
  let lomem = tomem ldep in
  let himem = tomem hdep in
  let hids = dom himem in
  snd(margd hids (condd lomem mu)) himem;;

let qry mu hdep ldep =
  let tomem deps = Mem.of_list deps in
  let lomem = tomem ldep in
  let himem = tomem hdep in
  let hids = dom himem in
  snd(margd hids (condd lomem mu)) himem;;
