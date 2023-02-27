input -> top_level {% id %}

top_level
    ->  top_level_expr
        {%  (data) => [data[0]] %}
    |  top_level_expr _ "\n" _ top_level
        {%  (data) => [data[0], ... data[4]] %}
    |  _ "\n" top_level
        {% (data) => data[2] %}
    |  _
        {%
            data => []
        %}


top_level_expr
    -> expr {% id %}
    | seq_expr {% id %}

expr
    -> flip_expr {% id %}
    | view_expr {% id %}
    | secret_expr {% id %}
    | not_expr {% id %}
    | and_expr {% id %}
    | select_expr {% id %}
    | xor_expr {% id %}
    | boolean_expr {% id %}
    | appl_expr {% id %}
    | h_expr {% id %}
    | concat_expr {% id %}
    | let_expr {% id %}
    | ot_expr {% id %}
    | dot_expr {% id %}
    | assign_expr {% id %}
    | fun_expr {% id %}


fun_expr
    -> fname_expr "(" _ parameter_list _ ")" _ "{" _ code_block _ "}" _ "\n"
    {%
            data => (["Fun", [data[0], data[3], data[9]]])
        %}
    | fname_expr "(" _ parameter_list _ ")" _ "\n" _ "{" _ "\n" _ code_block _ "}" _ "\n"
    {%
            data => (["Fun", [data[0], data[3], data[13]]])
        %}

parameter_list
    -> func_param {% id %}
    | _ func_param _ "," _ parameter_list _
        {% (data) => [data[1], data[5]] %}

func_param
    -> evar_expr _ ":" _ type_val _
    {% (data) => [data[0], data[4]] %}

code_block
    -> _ expr _ "\n" {% id %}
    | _ expr _ "\n" _ code_block _
    {% (data) => [data[0], data[4]] %}

flip_expr
    -> "flip" _ "[" _ val_expr _ "," _ val_expr _ "]"
        {%
            data => (["Flip",[data[4], data[8]]])
        %}

view_expr
    -> "v" _ "[" _ val_expr _ "," _ val_expr _ "]"
        {%
            d => (["View",[d[4], d[8]]])
        %}

secret_expr
    -> "s" _ "[" _ val_expr _ "," _ val_expr _ "]"
        {%
            d => (["Secret",[d[4], d[8]]])
        %}


ot_expr
    -> "OT" _ "[" _ val_expr _ "," _ val_expr _ "," _ val_expr _ "]"
        {%
            d => (["OT",[d[4], d[8], d[12]]]
            )
        %}

assign_expr
    -> expr _ ":=" _ var_assign
        {%
            d => (["Assign",[d[0], d[4]]]
            )
        %}
    | expr _ ":=" _ expr
    {%
            d => (["Assign",[d[0], d[4]]]
            )
        %}

var_assign
    -> evar_expr
        {%
            d => (["Var",[d[0]]]
            )
        %}

h_expr
    -> "H" _ "[" _ val_expr _ "]"
        {%
            d => (["H",[d[4]]])
        %}


select_expr
    -> "select" _ "[" _ val_expr _ "," _ val_expr _ "," _ val_expr _ "]"
        {%
            d => (["Select",[d[4], d[8], d[12]]])
        %}

concat_expr
    -> val_expr _ "||" _ val_expr
        {%
            d => (["Concat",[d[0], d[4]]])
        %}

let_expr
    -> "let" _ evar_expr _ "=" _ expr _ "in"
        {%
            d => (["Let",[d[2], d[6]]]
            )
        %}
    | "let" _ evar_expr _ "=" _ expr _ "in" _ expr
        {%
            d => (["Let",[d[2], d[6], d[10]]])
        %}

seq_expr
    -> expr _ ";"
        {%
            d => (["Seq",[d[0]]])
        %}

dot_expr
    -> evar_expr "." field_expr
        {%
            d => (["Dot",[d[0], d[2]]])
        %}

record_expr
    -> "{" _ record_vals _ "}"
        {%
            d => (["Record",[d[2]]])
        %}

record_vals
    -> _ record_val _
     {% id %}
    | _ record_val _ "," _ record_vals
     {% id %}

record_val
    -> _ field_expr _ "=" _ val_expr _
    {% (data) => [data[1], data[5]] %}


appl_expr
    -> fname_expr "(" _ values _ ")"
        {%
            d => (["Appl",[d[0], d[3]]])
        %}

values -> val_expr {% id %}
    | _ val_expr _ "," _ values _
        {% (data) => [data[1], data[5]] %}


not_expr
    -> "not" _ val_expr
        {%
            d => (["Not",[d[2]]])
        %}

and_expr
    -> val_expr _ "and" _ val_expr
        {%
            d => (["And",[d[0], d[4]]])
        %}


xor_expr
    -> val_expr _ "xor" _ val_expr
        {%
            d => (["Xor",[d[0], d[4]]])
        %}

val_expr -> expr {% id %}
    | boolean_expr {% id %}
    | cid_expr  {% id %}
    | string_expr {% id %}
    | evar_expr {% id %}

type_val
    -> cid_type {% id %}
    | string_type {% id %}
    | record_type {% id %}
    | jpd_type {% id %}

record_type
    -> "{" _ record_types _ "}"
    {% (data) => [data[2]] %}


record_types
    -> record_type {% id %}
    | _ record_type _ ";" _ record_types _
    {% (data) => [data[1], data[5]] %}

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
    -> "string" "(" _ var_assign _ ")"
        {%
            data => (["StringTy",[data[3]]]
            )
        %}

cid_type
    -> "cid" "(" _ var_assign _ ")"
    {%
            data => (["CidTy",[data[3]]])
        %}

jpd_type
    -> "jpd" "(" _ dvar_expr _ "," _ dvar_expr _ ")"
        {%
            data => (["JpdTy",[data[3], data[7]]]
            )
        %}


dvar_expr
    -> "'" alpha_char
    {%
            data => (["Dvar",[data[1]]]
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
            data => (["Evar",[data[0]]])
        %}

fname_expr
    -> alpha_char
    {%
            data => (["Fname", [data[0]]])
        %}

field_expr
    -> alpha_char
    {%
            data => (["Field",[data[0]]]
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
            data => (["String", [data[1]]])
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

_ -> [ \t]:*

