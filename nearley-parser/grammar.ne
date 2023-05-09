input -> top_level {% id %}

top_level 
    -> _ top_level_expr _
        {%  (data) => [data[1]] %} 
    |  _ top_level_expr _ "\n" top_level 
        {%  (data) => [data[1], ... data[4]] %}
    |  _ "\n" top_level
        {% (data) => data[2] %}
    |  _
        {%
            data => []
        %}

top_level_expr
    -> fun_expr {% data => null  %}
    | expr {% id %}

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
    | seq_expr {% id %}
    | var_expr {% id %}

fun_expr 
    -> fname_expr "(" parameter_list ")" _ "{" code_block "}" _ "\n" 
    {% 
            data => ([[data[0], data[2], data[6]]]) 
        %} 
    | fname_expr "(" parameter_list ")" _ "\n" _ "{" _ "\n" code_block "}" _ "\n" 
    {% 
            data => ([[data[0], data[2], data[10]]]) 
        %} 

parameter_list 
    -> func_param {% (data) => [data[0]] %} 
    | func_param "," parameter_list 
        {% (data) => [data[0], ...data[2]] %} 

func_param 
    -> _ evar_expr _ ":" _ type_val _ 
    {% (data) => [data[1], data[5]] %} 
  
code_block 
    -> _ expr _ "\n" 
    {% (data) => [data[1]] %} 
    | _ expr _ "\n" code_block 
    {% (data) => [data[1], ...data[4]] %}

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

val_expr 
    -> cid_expr  {% id %}
    | string_expr {% id %}
    | var_expr {% id %}
    | concat_expr {% id %}

ot_expr
    -> "OT" _ "[" _ expr _ "," _ expr _ "," _ expr _ "]"
        {%
            data => (["OT",[data[4], data[8], data[12]]]
            )
        %}

assign_expr
    -> view_expr _ ":=" _ expr
        {%
            data => (["Assign",[data[0], data[4]]]
            )
        %}
    | view_expr _ ":=" _ var_expr
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
    -> "select" _ "[" _ expr _ "," _ expr _ "," _ expr _ "]"
        {%
            data => (["Select",[data[4], data[8], data[12]]])
        %}

concat_expr
    -> concatable_expr _ "||" _ concatable_expr
        {%
            data => (["Concat",[data[0], data[4]]])
        %}

concatable_expr
    -> paren_expr {% id %}
    | var_expr {% id %}
    | string_expr {% id %}
    | concat_expr {% id %}

paren_expr
    -> "(" _ expr _ ")"
        {% (data) => data[2] %}

let_expr
    -> "let" _ evar_expr _ "=" _ expr _ "in" _ "\n" _ expr
        {%
            data => (["Let",[data[2], data[6], data[12]]])
        %}
    | "let" _ evar_expr _ "=" _ expr _ "in" _ expr
        {%
            data => (["Let",[data[2], data[6], data[10]]])
        %}

seq_expr
    -> assign_expr _ ";" _ "\n" _ expr
        {%
            data => (["Seq",[data[0], data[6]]])
        %}

dot_expr
    -> dot_val "." field_expr
        {%
            data => (["Dot",[data[0], data[2]]])
        %}

dot_val
    -> dot_expr {% id %}
    | var_expr {% id %}

record_expr
    -> "{" record_vals "}"
        {%
            data => (["Record",[data[1]]])
        %}

record_vals
    -> record_val
     {% (data) => [data[0]] %}
    | record_val ";" record_vals
     {% (data) => [data[0], ...data[2]] %}

record_val
    -> _ field_expr _ "=" _ expr _
    {% data => [data[1], data[5]] %}


appl_expr
    -> fname_expr "(" values ")"
        {%
            data => (["Appl",[data[0], data[2]]])
        %}

values -> _ expr _
        {% (data) => [data[1]] %}
    | _ expr _ "," values
        {% (data) => [data[1], ...data[4]] %}
    | _ val_expr _
        {% (data) => [data[1]] %}
    | _ val_expr _ "," values
        {% (data) => [data[1], ...data[4]] %}

not_expr
    -> "not" _ expr
        {%
            data => (["Not",[data[2]]])
        %}

and_expr
    -> expr _ "and" _ expr
        {%
            data => (["And",[data[0], data[4]]])
        %}


xor_expr
    -> expr _ "xor" _ expr
        {%
            data => (["Xor",[data[0], data[4]]])
        %}

type_val
    -> cid_type {% id %}
    | string_type {% id %}
    | record_type {% id %}
    | jpd_type {% id %}

record_type
    -> "{" record_types "}"
    {% (data) => ["RecTy", [data[1]]] %}

record_types
    -> record_part {% (data) => [data[0]] %}
    | record_part ";" record_types
    {% (data) => [data[0], ...data[2]] %}

record_part
    -> _ field_expr _ ":" _ type_val _ 
        {%
            data => ([data[1], data[5]]
            )
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
    -> "~true"
    {%
            data => (["Bool",["true"]])
        %}
    | "~false"
        {%
            data => (["Bool",["false"]])
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
