module S = Set.Make(String);;
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
        type t = int * string
        let compare (x : t) (y : t) = compare x y
      end);;

let var_to_string t c s = t ^ "_" ^ (string_of_int c) ^ "_" ^ s;;

(* Rather than an expr type, store a pair of the cid and corresponding string/name *)
let iovars (views : views) =
  let rec vs = function
      Top -> (VS.empty, VS.empty)
    | Bot -> (VS.empty, VS.empty)
    | Dist(V(Cid(c), String(s))) -> (VS.empty,VS.empty)
    | Dist((H(String(s)))) -> (VS.empty,VS.singleton (-1, "H_"  ^ s))
    | Dist((F(Cid(c), String(s)))) -> (VS.empty,VS.singleton (c, var_to_string "F" c s))
    | Dist((S(Cid(c), String(s)))) -> (VS.singleton (c, var_to_string "S" c s),VS.empty)
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
      let (s,f) = vs p in (VS.union s ss, VS.union f fs, VS.add (
        match v with V(Cid(c), String(st)) -> (c, var_to_string "V" c st)) vidss))
    (VS.empty, VS.empty, VS.empty) views

let ms_top = MS.singleton (Mem.empty);;

let ms_bot = MS.empty;;

let ms t c s = MS.singleton (Mem.of_list [(var_to_string t c s, true)]);;

(* 
  Get the type (S, F, H, or V), cid, and identifier of a variable from string form. For H use -1 as cid 
  Input is of the form V_0_0 or H_0
*)
let parse_string x = 
  let s_list = String.split_on_char '_' x in
  let s_0 = if String.starts_with "not" (List.nth s_list 0) then 
    Char.escaped (List.nth s_list 0).[3] 
  else 
    (List.nth s_list 0) in
  if s_0 = "H" then
    (s_0, -1, List.nth s_list 1)
  else
    (s_0, int_of_string (List.nth s_list 1), List.nth s_list 2);;

(* 
  Remove negation from clause body
  Replace mem format of (name, true/false) with just name and prepending not when false 
*)
let remove_negation_lists x = 
  List.map (fun y -> List.map (fun z -> 
    if snd(z) = false then ("not" ^ fst(z)) else (fst(z)))
  (Mem.elements y)) (MS.elements x);;

let truth_tables (views : views) =
  let rec tt = function
      Top -> ms_top
    | Bot -> ms_bot
    | Dist(V(Cid(c), String(s))) -> ms "V" c s
    | Dist(H(String(s))) -> MS.singleton (Mem.of_list [("H" ^ "_" ^ s, true)])
    | Dist(F(Cid(c), String(s))) -> ms "F" c s
    | Dist(S(Cid(c), String(s))) -> ms "S" c s
    | Meet(p1, p2) -> meet (tt p1) (tt p2)
    | Join(p1, p2) -> join (tt p1) (tt p2)
    | Comp(p) -> comp (tt p)
    | Xord(p1, p2) ->
       let tp1 = tt p1 in
       let tp2 = tt p2 in
       join (meet tp1 (comp tp2)) (meet (comp tp1) tp2)
    | _ -> raise (TypeError "free variable encountered in truth_tables")
  in
  List.concat_map
    (fun (v, Jpdf(p)) ->
      let tp = tt p in
      let tv = match v with (V(Cid(c), String(s))) -> "V" ^ "_" ^ (string_of_int c) ^ "_" ^ s in
      (* Add the "not" version of the variable and remove negation in body*)
      [(tv, (remove_negation_lists tp));(("not" ^ tv), (remove_negation_lists (comp tp)))]
      )
    views;;

(* 
  Deal with multiple definitions
  If a head appears more than once and the body of any of those clauses is longer than 1, create new head variables for each part
  with corresponding rules added going back to the original head.
*)
let multiple_defs logic = 
  List.concat_map (fun (k,v) -> 
    if List.length v > 1 then
      (List.mapi (fun i x -> ((k ^ "." ^ (string_of_int i)), x)) v) @ (List.mapi (fun i x -> (k, [(k ^ "." ^ (string_of_int i))])) v)
    else
      [(k, (List.nth v 0))]
  ) logic;;

let print_table t = List.iter (fun (k, v) -> Printf.printf "%-11s <- %s\n" k (String.concat ", " v)) t;;

let write_json filename ss fs vs logic_program = 
  let oc = open_out filename in
  Printf.fprintf oc "{\n\"base\":\n{\n\"views\":\n[\n";
  let n = List.length (VS.elements vs) in
  List.iteri (fun i x ->  
    Printf.fprintf oc "{\"cid\": %d,\"name\": \"%s\"}" (fst x) (snd x);
    if i < n - 1 then Printf.fprintf oc ",";
    Printf.fprintf oc "\n") (VS.elements vs);
  Printf.fprintf oc "],\n";

  Printf.fprintf oc "\"secrets\":\n[\n";
  let n = List.length (VS.elements ss) in
  List.iteri (fun i x ->  
    Printf.fprintf oc "{\"cid\": %d,\"name\": \"%s\"}" (fst x) (snd x);
    if i < n - 1 then Printf.fprintf oc ",";
    Printf.fprintf oc "\n") (VS.elements ss);
  Printf.fprintf oc "],\n";

  Printf.fprintf oc "\"flips\":\n[\n";
  let n = List.length (VS.elements fs) in
  List.iteri (fun i x ->  
    Printf.fprintf oc "{\"name\": \"%s\"}" (snd x);
    if i < n - 1 then Printf.fprintf oc ",";
    Printf.fprintf oc "\n") (VS.elements fs);
  Printf.fprintf oc "]";

  Printf.fprintf oc "\n},\n";

  Printf.fprintf oc "\"program\":\n[\n";
  let n = List.length logic_program in
  List.iteri (fun i (k, v) -> 
    Printf.fprintf oc "{\n";
    Printf.fprintf oc "\"head\": \"%s\", " k;
    Printf.fprintf oc "\"body\": [";
    Printf.fprintf oc "\"%s\"" (String.concat "\", \"" v);
    Printf.fprintf oc "]\n";
    Printf.fprintf oc "}";
    if i < n - 1 then Printf.fprintf oc ",";
    Printf.fprintf oc "\n";) logic_program;
  Printf.fprintf oc "]\n";

  Printf.fprintf oc "}";
  close_out oc;;

let lp p filename =
  let (_,views) = progty p in
  let (ss, fs, vs) = iovars views in 
  let temp_logic_program = truth_tables views in
  let logic_program = multiple_defs temp_logic_program in
  print_table logic_program;
  write_json filename ss fs vs logic_program;;