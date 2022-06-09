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

