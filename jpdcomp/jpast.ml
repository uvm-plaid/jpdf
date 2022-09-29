type vartype = Secret | View | Flip | Local;;

type id = vartype * int * ss

type w = string

type svar = String

type ss = w | svar | Prod of ss * ss

(* type bool = Bool *)

type val = expr

type tau = Nat | Bool | Prod of tau * tau

type client  = Int

type expr =
    Var of id 
  | Bool of Bool
  | Not of expr 
  | And of expr * expr 
  | Or of expr * expr 
  | Xor of expr * expr 

  | H of ss
  | Select of expr * expr * expr 
  | OT of expr * expr * expr 
  | Assign of id * expr 
  | Function of id * tau * expr

  | Appl of expr * expr
  | Record of (field * expr) list
  | Dot of expr * field
  | Let of id * expr * expr

  | At of Bool * client

  | Seq of expr * expr;;
