// Generated automatically by nearley, version 2.20.1
// http://github.com/Hardmath123/nearley
(function () {
function id(x) { return x[0]; }
var grammar = {
    Lexer: undefined,
    ParserRules: [
    {"name": "input", "symbols": ["top_level"], "postprocess": id},
    {"name": "top_level", "symbols": ["top_level_expr"], "postprocess": (data) => [data[0]]},
    {"name": "top_level", "symbols": ["_", "top_level_expr", "_", {"literal":"\n"}, "_", "top_level"], "postprocess": (data) => [data[1], ... data[5]]},
    {"name": "top_level", "symbols": ["_", {"literal":"\n"}, "top_level"], "postprocess": (data) => data[2]},
    {"name": "top_level", "symbols": ["_"], "postprocess": 
        data => []
                },
    {"name": "top_level_expr", "symbols": ["fun_expr"], "postprocess": id},
    {"name": "top_level_expr", "symbols": ["other_expr"], "postprocess": data => null},
    {"name": "expr", "symbols": ["flip_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["view_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["secret_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["not_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["and_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["select_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["xor_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["boolean_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["appl_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["h_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["concat_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["let_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["ot_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["dot_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["record_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["assign_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["fun_expr"], "postprocess": id},
    {"name": "expr", "symbols": ["paren_expr"], "postprocess": id},
    {"name": "other_expr", "symbols": ["expr"]},
    {"name": "other_expr", "symbols": ["seq_expr"]},
    {"name": "fun_expr", "symbols": ["fname_expr", {"literal":"("}, "_", "parameter_list", "_", {"literal":")"}, "_", {"literal":"{"}, "_", "code_block", "_", {"literal":"}"}, "_", {"literal":"\n"}], "postprocess": 
        data => ([[data[0], data[3], data[9]]])
                },
    {"name": "fun_expr", "symbols": ["fname_expr", {"literal":"("}, "_", "parameter_list", "_", {"literal":")"}, "_", {"literal":"\n"}, "_", {"literal":"{"}, "_", {"literal":"\n"}, "_", "code_block", "_", {"literal":"}"}, "_", {"literal":"\n"}], "postprocess": 
        data => ([[data[0], data[3], data[13]]])
                },
    {"name": "parameter_list", "symbols": ["func_param"], "postprocess": (data) => [data[0]]},
    {"name": "parameter_list", "symbols": ["_", "func_param", "_", {"literal":","}, "_", "parameter_list", "_"], "postprocess": (data) => [data[1], ...data[5]]},
    {"name": "func_param", "symbols": ["evar_expr", "_", {"literal":":"}, "_", "type_val", "_"], "postprocess": (data) => [data[0], data[4]]},
    {"name": "code_block", "symbols": ["_", "expr", "_", {"literal":"\n"}], "postprocess": (data) => [data[1]]},
    {"name": "code_block", "symbols": ["_", "expr", "_", {"literal":"\n"}, "_", "code_block", "_"], "postprocess": (data) => [data[1], ...data[5]]},
    {"name": "flip_expr$string$1", "symbols": [{"literal":"f"}, {"literal":"l"}, {"literal":"i"}, {"literal":"p"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "flip_expr", "symbols": ["flip_expr$string$1", "_", {"literal":"["}, "_", "val_expr", "_", {"literal":","}, "_", "val_expr", "_", {"literal":"]"}], "postprocess": 
        data => (["F",[data[4], data[8]]])
                },
    {"name": "view_expr", "symbols": [{"literal":"v"}, "_", {"literal":"["}, "_", "val_expr", "_", {"literal":","}, "_", "val_expr", "_", {"literal":"]"}], "postprocess": 
        data => (["V",[data[4], data[8]]])
                },
    {"name": "secret_expr", "symbols": [{"literal":"s"}, "_", {"literal":"["}, "_", "val_expr", "_", {"literal":","}, "_", "val_expr", "_", {"literal":"]"}], "postprocess": 
        data => (["S",[data[4], data[8]]])
                },
    {"name": "ot_expr$string$1", "symbols": [{"literal":"O"}, {"literal":"T"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "ot_expr", "symbols": ["ot_expr$string$1", "_", {"literal":"["}, "_", "val_expr", "_", {"literal":","}, "_", "val_expr", "_", {"literal":","}, "_", "val_expr", "_", {"literal":"]"}], "postprocess": 
        data => (["OT",[data[4], data[8], data[12]]]
        )
                },
    {"name": "assign_expr$string$1", "symbols": [{"literal":":"}, {"literal":"="}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "assign_expr", "symbols": ["expr", "_", "assign_expr$string$1", "_", "var_assign"], "postprocess": 
        data => (["Assign",[data[0], data[4]]]
        )
                },
    {"name": "assign_expr$string$2", "symbols": [{"literal":":"}, {"literal":"="}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "assign_expr", "symbols": ["expr", "_", "assign_expr$string$2", "_", "expr"], "postprocess": 
        data => (["Assign",[data[0], data[4]]]
        )
                },
    {"name": "var_assign", "symbols": ["evar_expr"], "postprocess": 
        data => (["Var",[data[0]]]
        )
                },
    {"name": "h_expr", "symbols": [{"literal":"H"}, "_", {"literal":"["}, "_", "val_expr", "_", {"literal":"]"}], "postprocess": 
        data => (["H",[data[4]]])
                },
    {"name": "select_expr$string$1", "symbols": [{"literal":"s"}, {"literal":"e"}, {"literal":"l"}, {"literal":"e"}, {"literal":"c"}, {"literal":"t"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "select_expr", "symbols": ["select_expr$string$1", "_", {"literal":"["}, "_", "val_expr", "_", {"literal":","}, "_", "val_expr", "_", {"literal":","}, "_", "val_expr", "_", {"literal":"]"}], "postprocess": 
        data => (["Select",[data[4], data[8], data[12]]])
                },
    {"name": "concat_expr$string$1", "symbols": [{"literal":"|"}, {"literal":"|"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "concat_expr", "symbols": ["val_expr", "_", "concat_expr$string$1", "_", "val_expr"], "postprocess": 
        data => (["Concat",[data[0], data[4]]])
                },
    {"name": "paren_expr", "symbols": [{"literal":"("}, "_", "expr", "_", {"literal":")"}], "postprocess": (data) => data[2]},
    {"name": "let_expr$string$1", "symbols": [{"literal":"l"}, {"literal":"e"}, {"literal":"t"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "let_expr$string$2", "symbols": [{"literal":"i"}, {"literal":"n"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "let_expr", "symbols": ["let_expr$string$1", "_", "evar_expr", "_", {"literal":"="}, "_", "expr", "_", "let_expr$string$2", "_", {"literal":"\n"}, "_", "expr"], "postprocess": 
        data => (["Let",[data[2], data[6], data[12]]])
                },
    {"name": "let_expr$string$3", "symbols": [{"literal":"l"}, {"literal":"e"}, {"literal":"t"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "let_expr$string$4", "symbols": [{"literal":"i"}, {"literal":"n"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "let_expr", "symbols": ["let_expr$string$3", "_", "evar_expr", "_", {"literal":"="}, "_", "expr", "_", "let_expr$string$4", "_", "expr"], "postprocess": 
        data => (["Let",[data[2], data[6], data[10]]])
                },
    {"name": "seq_expr", "symbols": ["expr", "_", {"literal":";"}], "postprocess": 
        data => (["Seq",[data[0]]])
                },
    {"name": "dot_expr", "symbols": ["evar_expr", {"literal":"."}, "field_expr"], "postprocess": 
        data => (["Dot",[data[0], data[2]]])
                },
    {"name": "record_expr", "symbols": [{"literal":"{"}, "_", "record_vals", "_", {"literal":"}"}], "postprocess": 
        data => (["Record",[data[2]]])
                },
    {"name": "record_vals", "symbols": ["_", "record_val", "_"], "postprocess": (data) => [data[1]]},
    {"name": "record_vals", "symbols": ["_", "record_val", "_", {"literal":";"}, "_", "record_vals"], "postprocess": (data) => [data[1], ...data[5]]},
    {"name": "record_val", "symbols": ["_", "field_expr", "_", {"literal":"="}, "_", "val_expr", "_"], "postprocess": data => [data[1], data[5]]},
    {"name": "appl_expr", "symbols": ["fname_expr", {"literal":"("}, "_", "values", "_", {"literal":")"}], "postprocess": 
        data => (["Appl",[data[0], data[3]]])
                },
    {"name": "values", "symbols": ["val_expr"], "postprocess": (data) => [data[0]]},
    {"name": "values", "symbols": ["_", "val_expr", "_", {"literal":","}, "_", "values", "_"], "postprocess": (data) => [data[1], ...data[5]]},
    {"name": "not_expr$string$1", "symbols": [{"literal":"n"}, {"literal":"o"}, {"literal":"t"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "not_expr", "symbols": ["not_expr$string$1", "_", "val_expr"], "postprocess": 
        data => (["Not",[data[2]]])
                },
    {"name": "and_expr$string$1", "symbols": [{"literal":"a"}, {"literal":"n"}, {"literal":"d"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "and_expr", "symbols": ["val_expr", "_", "and_expr$string$1", "_", "val_expr"], "postprocess": 
        data => (["And",[data[0], data[4]]])
                },
    {"name": "xor_expr$string$1", "symbols": [{"literal":"x"}, {"literal":"o"}, {"literal":"r"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "xor_expr", "symbols": ["val_expr", "_", "xor_expr$string$1", "_", "val_expr"], "postprocess": 
        data => (["Xor",[data[0], data[4]]])
                },
    {"name": "val_expr", "symbols": ["expr"], "postprocess": id},
    {"name": "val_expr", "symbols": ["boolean_expr"], "postprocess": id},
    {"name": "val_expr", "symbols": ["cid_expr"], "postprocess": id},
    {"name": "val_expr", "symbols": ["string_expr"], "postprocess": id},
    {"name": "val_expr", "symbols": ["evar_expr"], "postprocess": id},
    {"name": "type_val", "symbols": ["cid_type"], "postprocess": id},
    {"name": "type_val", "symbols": ["string_type"], "postprocess": id},
    {"name": "type_val", "symbols": ["record_type"], "postprocess": id},
    {"name": "type_val", "symbols": ["jpd_type"], "postprocess": id},
    {"name": "record_type", "symbols": [{"literal":"{"}, "_", "record_types", "_", {"literal":"}"}], "postprocess": (data) => [data[2]]},
    {"name": "record_types", "symbols": ["record_type"], "postprocess": (data) => [data[0]]},
    {"name": "record_types", "symbols": ["_", "record_type", "_", {"literal":";"}, "_", "record_types", "_"], "postprocess": (data) => [data[1], ...data[5]]},
    {"name": "record_type", "symbols": ["_", "field_expr", "_", {"literal":":"}, "_", "field_type", "_"], "postprocess": 
        data => (["RecordTy",[data[1], data[5]]]
        )
                },
    {"name": "field_type", "symbols": ["alpha_char"], "postprocess": 
        data => (["FieldTy",[data[0]]])
                },
    {"name": "string_type$string$1", "symbols": [{"literal":"s"}, {"literal":"t"}, {"literal":"r"}, {"literal":"i"}, {"literal":"n"}, {"literal":"g"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "string_type", "symbols": ["string_type$string$1", {"literal":"("}, "_", "var_assign", "_", {"literal":")"}], "postprocess": 
        data => (["StringTy",[data[3]]]
        )
                },
    {"name": "cid_type$string$1", "symbols": [{"literal":"c"}, {"literal":"i"}, {"literal":"d"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "cid_type", "symbols": ["cid_type$string$1", {"literal":"("}, "_", "var_assign", "_", {"literal":")"}], "postprocess": 
        data => (["CidTy",[data[3]]])
                },
    {"name": "jpd_type$string$1", "symbols": [{"literal":"j"}, {"literal":"p"}, {"literal":"d"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "jpd_type", "symbols": ["jpd_type$string$1", {"literal":"("}, "_", "dvar_expr", "_", {"literal":")"}], "postprocess": 
        data => (["Jpdf",[data[3]]]
        )
                },
    {"name": "dvar_expr", "symbols": [{"literal":"'"}, "alpha_char"], "postprocess": 
        data => (["DVar",["\"" + data[1] + "\""]]
        )
                },
    {"name": "boolean_expr$string$1", "symbols": [{"literal":"t"}, {"literal":"r"}, {"literal":"u"}, {"literal":"e"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "boolean_expr", "symbols": ["boolean_expr$string$1"], "postprocess": 
        data => (["Bool",[data[0]]])
                },
    {"name": "boolean_expr$string$2", "symbols": [{"literal":"f"}, {"literal":"a"}, {"literal":"l"}, {"literal":"s"}, {"literal":"e"}], "postprocess": function joiner(d) {return d.join('');}},
    {"name": "boolean_expr", "symbols": ["boolean_expr$string$2"], "postprocess": 
        data => (["Bool",[data[0]]])
                },
    {"name": "evar_expr", "symbols": ["alpha_char"], "postprocess": 
        data => (["EVar",["\"" + data[0] + "\""]])
                },
    {"name": "fname_expr", "symbols": ["alpha_char"], "postprocess": 
        data => (["Fname", ["\"" + data[0] + "\""]])
                },
    {"name": "field_expr", "symbols": ["alpha_char"], "postprocess": 
        data => (["\"" + data[0] + "\""]
        )
                },
    {"name": "cid_expr", "symbols": ["number"], "postprocess": 
        data => (["Cid",[data[0]]]
        )
                },
    {"name": "string_expr", "symbols": [{"literal":"\""}, "characters", {"literal":"\""}], "postprocess": 
        data => (["String", ["\"" + data[1] + "\""]])
                },
    {"name": "characters", "symbols": ["character"], "postprocess": id},
    {"name": "characters", "symbols": ["character", "characters"], "postprocess": (data) => data[0] + data[1]},
    {"name": "character", "symbols": [/[a-z|A-Z|0-9]/], "postprocess": id},
    {"name": "alpha_char", "symbols": [/[a-z|A-Z]/], "postprocess": id},
    {"name": "alpha_char", "symbols": [/[a-z|A-Z]/, "next_char"], "postprocess": (data) => data[0] + data[1]},
    {"name": "next_char", "symbols": ["characters"], "postprocess": id},
    {"name": "number", "symbols": ["digits"], "postprocess": (data) => Number(data.join(""))},
    {"name": "digits", "symbols": ["digit"], "postprocess": id},
    {"name": "digits", "symbols": ["digit", "digits"], "postprocess": (data) => Number(data.join(""))},
    {"name": "digit", "symbols": [/[0-9]/], "postprocess": id},
    {"name": "_$ebnf$1", "symbols": []},
    {"name": "_$ebnf$1", "symbols": ["_$ebnf$1", /[ \t]/], "postprocess": function arrpush(d) {return d[0].concat([d[1]]);}},
    {"name": "_", "symbols": ["_$ebnf$1"], "postprocess": function(d) {return null; }}
]
  , ParserStart: "input"
}
if (typeof module !== 'undefined'&& typeof module.exports !== 'undefined') {
   module.exports = grammar;
} else {
   window.grammar = grammar;
}
})();
