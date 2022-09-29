type cid  = int
type id = SVar of string | TVar of string | Fname of string
type field = string 

type expr =
    Var of id 
  | Bool of bool
  | Not of expr 
  | And of expr * expr 
  | Or of expr * expr 
  | Xor of expr * expr 
  | H of expr
  | Flip of cid * expr
  | View of cid * expr
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

