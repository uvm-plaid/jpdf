input -> top_level {% id %}

top_level
    -> top_level_expr
        {%  (data) => [data[0]] %}
    |  _ top_level_expr _ "\n" _ top_level
        {%  (data) => [data[1], ... data[5]] %}
    |  _ "\n" top_level
        {% (data) => data[2] %}
    |  _
        {%
            data => []
        %}

top_level_expr
    -> fun_expr {% id %}
    | other_expr {% data => null %}

# top_level_expr
#     -> expr {% id %}
#     | seq_expr {% id %}

expr
    -> paren_expr {% id %}
    | flip_expr {% id %}
    | view_expr {% id %}
    | secret_expr {% id %}
    | let_expr {% id %}
    | not_expr {% id %}
    | and_expr {% id %}
    | select_expr {% id %}
    | xor_expr {% id %}
    | boolean_expr {% id %}
    | appl_expr {% id %}
    | h_expr {% id %}
    | concat_expr {% id %}
    | ot_expr {% id %}
    | dot_expr {% id %}
    | record_expr {% id %}
    | assign_expr {% id %}
    | fun_expr {% id %}
    | seq_expr {% id %}

other_expr
    -> let_expr
    | seq_expr 

fun_expr
    -> fname_expr "(" _ parameter_list _ ")" _ "{" _ code_block _ "}" _ "\n"
    {%
            data => ([[data[0], data[3], data[9]]])
        %}
    | fname_expr "(" _ parameter_list _ ")" _ "\n" _ "{" _ "\n" _ code_block _ "}" _ "\n"
    {%
            data => ([[data[0], data[3], data[13]]])
        %}

parameter_list
    -> func_param {% (data) => [data[0]] %}
    | _ func_param _ "," _ parameter_list _
        {% (data) => [data[1], ...data[5]] %}

func_param
    -> evar_expr _ ":" _ type_val _
    {% (data) => [data[0], data[4]] %}

code_block
    -> _ expr _ "\n"
    {% (data) => [data[1]] %}
    | _ expr _ "\n" _ code_block _
    {% (data) => [data[1], ...data[5]] %}

flip_expr
    -> "flip" _ "[" _ val_expr _ "," _ val_expr _ "]"
        {%
            data => (["F",[data[4], data[8]]])
        %}

view_expr
    -> "v" _ "[" _ val_expr _ "," _ val_expr _ "]"
        {%
            data => (["V",[data[4], data[8]]])
        %}


secret_expr
    -> "s" _ "[" _ val_expr _ "," _ val_expr _ "]"
        {%
            data => (["S",[data[4], data[8]]])
        %}


ot_expr
    -> "OT" _ "[" _ val_expr _ "," _ val_expr _ "," _ val_expr _ "]"
        {%
            data => (["OT",[data[4], data[8], data[12]]]
            )
        %}

assign_expr
    -> view_expr _ ":=" _ var_expr
        {%
            data => (["Assign",[data[0], data[4]]]
            )
        %}
    | view_expr _ ":=" _ expr
    {%
            data => (["Assign",[data[0], data[4]]]
            )
        %}

var_expr
    -> evar_expr
        {%
            data => (["Var",[data[0]]]
            )
        %}

h_expr
    -> "H" _ "[" _ val_expr _ "]"
        {%
            data => (["H",[data[4]]])
        %}


select_expr
    -> "select" _ "[" _ val_expr _ "," _ val_expr _ "," _ val_expr _ "]"
        {%
            data => (["Select",[data[4], data[8], data[12]]])
        %}

concat_expr
    -> val_expr _ "||" _ val_expr
        {%
            data => (["Concat",[data[0], data[4]]])
        %}

paren_expr
    -> "(" _ expr _ ")"
        {% (data) => data[2] %}

let_expr
    -> "let" _ evar_expr _ "=" _ expr _ "in" _ "\n" _ body_expr _
        {%
            data => (["Let",[data[2], data[6], data[12]]])
        %}
    | "let" _ evar_expr _ "=" _ expr _ "in" _ body_expr _ 
        {%
            data => (["Let",[data[2], data[6], data[10]]])
        %}

body_expr
    -> let_expr
    | seq_expr
    | assign_expr

    | record_expr
    | select_expr
    | flip_expr {% id %}
    | view_expr {% id %}
    | secret_expr {% id %}

seq_expr
    -> assign_expr _ ";" _ "\n" _ body_expr
        {%
            data => (["Seq",[data[0], data[6]]])
        %}

dot_expr
    -> var_expr "." field_expr
        {%
            data => (["Dot",[data[0], data[2]]])
        %}

record_expr
    -> "{" _ record_vals _ "}"
        {%
            data => (["Record",[data[2]]])
        %}

record_vals
    -> _ record_val _
     {% (data) => [data[1]] %}
    | _ record_val _ ";" _ record_vals
     {% (data) => [data[1], ...data[5]] %}

record_val
    -> _ field_expr _ "=" _ val_expr _
    {% data => [data[1], data[5]] %}


appl_expr
    -> fname_expr "(" _ values _ ")"
        {%
            data => (["Appl",[data[0], data[3]]])
        %}

values -> val_expr {% (data) => [data[0]] %}
    | _ val_expr _ "," _ values _
        {% (data) => [data[1], ...data[5]] %}


not_expr
    -> "not" _ val_expr
        {%
            data => (["Not",[data[2]]])
        %}

and_expr
    -> val_expr _ "and" _ val_expr
        {%
            data => (["And",[data[0], data[4]]])
        %}


xor_expr
    -> val_expr _ "xor" _ val_expr
        {%
            data => (["Xor",[data[0], data[4]]])
        %}

val_expr 
    -> expr {% id %}
    | boolean_expr {% id %}
    | cid_expr  {% id %}
    | string_expr {% id %}
    | var_expr {% id %}

type_val
    -> cid_type {% id %}
    | string_type {% id %}
    | record_type {% id %}
    | jpd_type {% id %}

record_type
    -> "{" _ record_types _ "}"
    {% (data) => [data[2]] %}

record_types
    -> record_type {% (data) => [data[0]] %}
    | _ record_type _ ";" _ record_types _
    {% (data) => [data[1], ...data[5]] %}

record_type
    -> _ field_expr _ ":" _ field_type _ 
        {%
            data => (["RecordTy",[data[1], data[5]]]
            )
        %}

field_type
    ->  alpha_char
    {%
            data => (["FieldTy",[data[0]]])
        %}


string_type
    -> "string" "(" _ var_expr _ ")"
        {%
            data => (["StringTy",[data[3]]]
            )
        %}

cid_type
    -> "cid" "(" _ var_expr _ ")"
    {%
            data => (["CidTy",[data[3]]])
        %}

jpd_type
    -> "jpd" "(" _ dvar_expr _ ")"
        {%
            data => (["Jpdf",[data[3]]]
            )
        %}


dvar_expr
    -> "'" alpha_char 
    {%
            data => (["DVar",["\"" + data[1] + "\""]]
            )
        %}


boolean_expr
    -> "true"
    {%
            data => (["Bool",[data[0]]])
        %}
    | "false"
        {%
            data => (["Bool",[data[0]]])
        %}

evar_expr
    -> alpha_char
    {%
            data => (["EVar",["\"" + data[0] + "\""]])
        %}

fname_expr
    -> alpha_char
    {%
            data => (["Fname", ["\"" + data[0] + "\""]])
        %}

field_expr
    -> alpha_char
    {%
            data => (["\"" + data[0] + "\""]
            )
        %}

cid_expr
    -> number
    {%
            data => (["Cid",[data[0]]]
            )
        %}

string_expr -> "\"" characters "\""
        {%
            data => (["String", ["\"" + data[1] + "\""]])
        %}

characters
    -> character {% id %}
    | character characters
        {% (data) => data[0] + data[1] %}

character
    -> [a-z|A-Z|0-9] {% id %}

alpha_char
    -> [a-z|A-Z] {% id %}
    | [a-z|A-Z] next_char
        {% (data) => data[0] + data[1] %}

next_char
    -> characters {% id %}

number
    -> digits
        {% (data) => Number(data.join("")) %}

digits -> digit {% id %}
        | digit digits
        {% (data) => Number(data.join("")) %}

digit
    -> [0-9] {% id %}

_ -> [ \t]:* {% function(d) {return null; } %}
