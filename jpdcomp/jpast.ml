type vartype = Secret | View | Flip;;

type w = string

type svar = string

type ss = w | svar | Prod of ss * ss

type id = vartype * Int * ss

(* type function_ident = *)

(* type bool = Bool *)

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
  | Function of id list * expr

  | Appl of funct_identif * expr list
  | Record of (field * expr) list
  | Dot of expr * field
  | Let of id * expr * expr
  | Str of ss
  | At of Bool * client

  | Seq of expr * expr;;

