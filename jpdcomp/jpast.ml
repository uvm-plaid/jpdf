type cid  = Int

type field = string 
type fname = string

type sexp = String of string | SVar of string | Concat of sexp * sexp

type expr =
    Var of string 
  | Bool of Bool
  | Not of expr 
  | And of expr * expr 
  | Or of expr * expr 
  | Xor of expr * expr 
  | H of expr
  | Flip of cid * expr
  | View of cid * expr
  | Sexp of sexp
  | Select of expr * expr * expr 
  | OT of expr * expr * expr 
  | Assign of expr * expr 
  | Appl of fname * expr list
  | Record of (field * expr) list
  | Dot of expr * field
  | Let of expr * expr * expr
  | Seq of expr * expr;;

