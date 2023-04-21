(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}g.parse = f()}})(function(){var define,module,exports;return (function(){function r(e,n,t){function o(i,f){if(!n[i]){if(!e[i]){var c="function"==typeof require&&require;if(!f&&c)return c(i,!0);if(u)return u(i,!0);var a=new Error("Cannot find module '"+i+"'");throw a.code="MODULE_NOT_FOUND",a}var p=n[i]={exports:{}};e[i][0].call(p.exports,function(r){var n=e[i][1][r];return o(n||r)},p,p.exports,r,e,n,t)}return n[i].exports}for(var u="function"==typeof require&&require,i=0;i<t.length;i++)o(t[i]);return o}return r})()({1:[function(require,module,exports){
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
        data => (["View",[data[4], data[8]]])
                },
    {"name": "secret_expr", "symbols": [{"literal":"s"}, "_", {"literal":"["}, "_", "val_expr", "_", {"literal":","}, "_", "val_expr", "_", {"literal":"]"}], "postprocess": 
        data => (["Secret",[data[4], data[8]]])
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
    {"name": "let_expr", "symbols": ["let_expr$string$1", "_", "evar_expr", "_", {"literal":"="}, "_", "expr", "_", "let_expr$string$2"], "postprocess": 
        data => (["Let",[data[2], data[6]]]
        )
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
        data => (["JpdTy",[data[3]]]
        )
                },
    {"name": "dvar_expr", "symbols": [{"literal":"'"}, "alpha_char"], "postprocess": 
        data => (["Dvar",["\"" + data[1] + "\""]]
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
        data => (["Evar",["\"" + data[0] + "\""]])
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

},{}],2:[function(require,module,exports){
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
    {"name": "top_level_expr", "symbols": ["fun_expr"], "postprocess": data => null},
    {"name": "top_level_expr", "symbols": ["other_expr"], "postprocess": id},
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
        data => (["View",[data[4], data[8]]])
                },
    {"name": "secret_expr", "symbols": [{"literal":"s"}, "_", {"literal":"["}, "_", "val_expr", "_", {"literal":","}, "_", "val_expr", "_", {"literal":"]"}], "postprocess": 
        data => (["Secret",[data[4], data[8]]])
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
    {"name": "let_expr", "symbols": ["let_expr$string$1", "_", "evar_expr", "_", {"literal":"="}, "_", "expr", "_", "let_expr$string$2"], "postprocess": 
        data => (["Let",[data[2], data[6]]]
        )
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
        data => (["JpdTy",[data[3]]]
        )
                },
    {"name": "dvar_expr", "symbols": [{"literal":"'"}, "alpha_char"], "postprocess": 
        data => (["Dvar",["\"" + data[1] + "\""]]
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
        data => (["Evar",["\"" + data[0] + "\""]])
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

},{}],3:[function(require,module,exports){
(function(root, factory) {
    if (typeof module === 'object' && module.exports) {
        module.exports = factory();
    } else {
        root.nearley = factory();
    }
}(this, function() {

    function Rule(name, symbols, postprocess) {
        this.id = ++Rule.highestId;
        this.name = name;
        this.symbols = symbols;        // a list of literal | regex class | nonterminal
        this.postprocess = postprocess;
        return this;
    }
    Rule.highestId = 0;

    Rule.prototype.toString = function(withCursorAt) {
        var symbolSequence = (typeof withCursorAt === "undefined")
                             ? this.symbols.map(getSymbolShortDisplay).join(' ')
                             : (   this.symbols.slice(0, withCursorAt).map(getSymbolShortDisplay).join(' ')
                                 + " ● "
                                 + this.symbols.slice(withCursorAt).map(getSymbolShortDisplay).join(' ')     );
        return this.name + " → " + symbolSequence;
    }


    // a State is a rule at a position from a given starting point in the input stream (reference)
    function State(rule, dot, reference, wantedBy) {
        this.rule = rule;
        this.dot = dot;
        this.reference = reference;
        this.data = [];
        this.wantedBy = wantedBy;
        this.isComplete = this.dot === rule.symbols.length;
    }

    State.prototype.toString = function() {
        return "{" + this.rule.toString(this.dot) + "}, from: " + (this.reference || 0);
    };

    State.prototype.nextState = function(child) {
        var state = new State(this.rule, this.dot + 1, this.reference, this.wantedBy);
        state.left = this;
        state.right = child;
        if (state.isComplete) {
            state.data = state.build();
            // Having right set here will prevent the right state and its children
            // form being garbage collected
            state.right = undefined;
        }
        return state;
    };

    State.prototype.build = function() {
        var children = [];
        var node = this;
        do {
            children.push(node.right.data);
            node = node.left;
        } while (node.left);
        children.reverse();
        return children;
    };

    State.prototype.finish = function() {
        if (this.rule.postprocess) {
            this.data = this.rule.postprocess(this.data, this.reference, Parser.fail);
        }
    };


    function Column(grammar, index) {
        this.grammar = grammar;
        this.index = index;
        this.states = [];
        this.wants = {}; // states indexed by the non-terminal they expect
        this.scannable = []; // list of states that expect a token
        this.completed = {}; // states that are nullable
    }


    Column.prototype.process = function(nextColumn) {
        var states = this.states;
        var wants = this.wants;
        var completed = this.completed;

        for (var w = 0; w < states.length; w++) { // nb. we push() during iteration
            var state = states[w];

            if (state.isComplete) {
                state.finish();
                if (state.data !== Parser.fail) {
                    // complete
                    var wantedBy = state.wantedBy;
                    for (var i = wantedBy.length; i--; ) { // this line is hot
                        var left = wantedBy[i];
                        this.complete(left, state);
                    }

                    // special-case nullables
                    if (state.reference === this.index) {
                        // make sure future predictors of this rule get completed.
                        var exp = state.rule.name;
                        (this.completed[exp] = this.completed[exp] || []).push(state);
                    }
                }

            } else {
                // queue scannable states
                var exp = state.rule.symbols[state.dot];
                if (typeof exp !== 'string') {
                    this.scannable.push(state);
                    continue;
                }

                // predict
                if (wants[exp]) {
                    wants[exp].push(state);

                    if (completed.hasOwnProperty(exp)) {
                        var nulls = completed[exp];
                        for (var i = 0; i < nulls.length; i++) {
                            var right = nulls[i];
                            this.complete(state, right);
                        }
                    }
                } else {
                    wants[exp] = [state];
                    this.predict(exp);
                }
            }
        }
    }

    Column.prototype.predict = function(exp) {
        var rules = this.grammar.byName[exp] || [];

        for (var i = 0; i < rules.length; i++) {
            var r = rules[i];
            var wantedBy = this.wants[exp];
            var s = new State(r, 0, this.index, wantedBy);
            this.states.push(s);
        }
    }

    Column.prototype.complete = function(left, right) {
        var copy = left.nextState(right);
        this.states.push(copy);
    }


    function Grammar(rules, start) {
        this.rules = rules;
        this.start = start || this.rules[0].name;
        var byName = this.byName = {};
        this.rules.forEach(function(rule) {
            if (!byName.hasOwnProperty(rule.name)) {
                byName[rule.name] = [];
            }
            byName[rule.name].push(rule);
        });
    }

    // So we can allow passing (rules, start) directly to Parser for backwards compatibility
    Grammar.fromCompiled = function(rules, start) {
        var lexer = rules.Lexer;
        if (rules.ParserStart) {
          start = rules.ParserStart;
          rules = rules.ParserRules;
        }
        var rules = rules.map(function (r) { return (new Rule(r.name, r.symbols, r.postprocess)); });
        var g = new Grammar(rules, start);
        g.lexer = lexer; // nb. storing lexer on Grammar is iffy, but unavoidable
        return g;
    }


    function StreamLexer() {
      this.reset("");
    }

    StreamLexer.prototype.reset = function(data, state) {
        this.buffer = data;
        this.index = 0;
        this.line = state ? state.line : 1;
        this.lastLineBreak = state ? -state.col : 0;
    }

    StreamLexer.prototype.next = function() {
        if (this.index < this.buffer.length) {
            var ch = this.buffer[this.index++];
            if (ch === '\n') {
              this.line += 1;
              this.lastLineBreak = this.index;
            }
            return {value: ch};
        }
    }

    StreamLexer.prototype.save = function() {
      return {
        line: this.line,
        col: this.index - this.lastLineBreak,
      }
    }

    StreamLexer.prototype.formatError = function(token, message) {
        // nb. this gets called after consuming the offending token,
        // so the culprit is index-1
        var buffer = this.buffer;
        if (typeof buffer === 'string') {
            var lines = buffer
                .split("\n")
                .slice(
                    Math.max(0, this.line - 5), 
                    this.line
                );

            var nextLineBreak = buffer.indexOf('\n', this.index);
            if (nextLineBreak === -1) nextLineBreak = buffer.length;
            var col = this.index - this.lastLineBreak;
            var lastLineDigits = String(this.line).length;
            message += " at line " + this.line + " col " + col + ":\n\n";
            message += lines
                .map(function(line, i) {
                    return pad(this.line - lines.length + i + 1, lastLineDigits) + " " + line;
                }, this)
                .join("\n");
            message += "\n" + pad("", lastLineDigits + col) + "^\n";
            return message;
        } else {
            return message + " at index " + (this.index - 1);
        }

        function pad(n, length) {
            var s = String(n);
            return Array(length - s.length + 1).join(" ") + s;
        }
    }

    function Parser(rules, start, options) {
        if (rules instanceof Grammar) {
            var grammar = rules;
            var options = start;
        } else {
            var grammar = Grammar.fromCompiled(rules, start);
        }
        this.grammar = grammar;

        // Read options
        this.options = {
            keepHistory: false,
            lexer: grammar.lexer || new StreamLexer,
        };
        for (var key in (options || {})) {
            this.options[key] = options[key];
        }

        // Setup lexer
        this.lexer = this.options.lexer;
        this.lexerState = undefined;

        // Setup a table
        var column = new Column(grammar, 0);
        var table = this.table = [column];

        // I could be expecting anything.
        column.wants[grammar.start] = [];
        column.predict(grammar.start);
        // TODO what if start rule is nullable?
        column.process();
        this.current = 0; // token index
    }

    // create a reserved token for indicating a parse fail
    Parser.fail = {};

    Parser.prototype.feed = function(chunk) {
        var lexer = this.lexer;
        lexer.reset(chunk, this.lexerState);

        var token;
        while (true) {
            try {
                token = lexer.next();
                if (!token) {
                    break;
                }
            } catch (e) {
                // Create the next column so that the error reporter
                // can display the correctly predicted states.
                var nextColumn = new Column(this.grammar, this.current + 1);
                this.table.push(nextColumn);
                var err = new Error(this.reportLexerError(e));
                err.offset = this.current;
                err.token = e.token;
                throw err;
            }
            // We add new states to table[current+1]
            var column = this.table[this.current];

            // GC unused states
            if (!this.options.keepHistory) {
                delete this.table[this.current - 1];
            }

            var n = this.current + 1;
            var nextColumn = new Column(this.grammar, n);
            this.table.push(nextColumn);

            // Advance all tokens that expect the symbol
            var literal = token.text !== undefined ? token.text : token.value;
            var value = lexer.constructor === StreamLexer ? token.value : token;
            var scannable = column.scannable;
            for (var w = scannable.length; w--; ) {
                var state = scannable[w];
                var expect = state.rule.symbols[state.dot];
                // Try to consume the token
                // either regex or literal
                if (expect.test ? expect.test(value) :
                    expect.type ? expect.type === token.type
                                : expect.literal === literal) {
                    // Add it
                    var next = state.nextState({data: value, token: token, isToken: true, reference: n - 1});
                    nextColumn.states.push(next);
                }
            }

            // Next, for each of the rules, we either
            // (a) complete it, and try to see if the reference row expected that
            //     rule
            // (b) predict the next nonterminal it expects by adding that
            //     nonterminal's start state
            // To prevent duplication, we also keep track of rules we have already
            // added

            nextColumn.process();

            // If needed, throw an error:
            if (nextColumn.states.length === 0) {
                // No states at all! This is not good.
                var err = new Error(this.reportError(token));
                err.offset = this.current;
                err.token = token;
                throw err;
            }

            // maybe save lexer state
            if (this.options.keepHistory) {
              column.lexerState = lexer.save()
            }

            this.current++;
        }
        if (column) {
          this.lexerState = lexer.save()
        }

        // Incrementally keep track of results
        this.results = this.finish();

        // Allow chaining, for whatever it's worth
        return this;
    };

    Parser.prototype.reportLexerError = function(lexerError) {
        var tokenDisplay, lexerMessage;
        // Planning to add a token property to moo's thrown error
        // even on erroring tokens to be used in error display below
        var token = lexerError.token;
        if (token) {
            tokenDisplay = "input " + JSON.stringify(token.text[0]) + " (lexer error)";
            lexerMessage = this.lexer.formatError(token, "Syntax error");
        } else {
            tokenDisplay = "input (lexer error)";
            lexerMessage = lexerError.message;
        }
        return this.reportErrorCommon(lexerMessage, tokenDisplay);
    };

    Parser.prototype.reportError = function(token) {
        var tokenDisplay = (token.type ? token.type + " token: " : "") + JSON.stringify(token.value !== undefined ? token.value : token);
        var lexerMessage = this.lexer.formatError(token, "Syntax error");
        return this.reportErrorCommon(lexerMessage, tokenDisplay);
    };

    Parser.prototype.reportErrorCommon = function(lexerMessage, tokenDisplay) {
        var lines = [];
        lines.push(lexerMessage);
        var lastColumnIndex = this.table.length - 2;
        var lastColumn = this.table[lastColumnIndex];
        var expectantStates = lastColumn.states
            .filter(function(state) {
                var nextSymbol = state.rule.symbols[state.dot];
                return nextSymbol && typeof nextSymbol !== "string";
            });

        if (expectantStates.length === 0) {
            lines.push('Unexpected ' + tokenDisplay + '. I did not expect any more input. Here is the state of my parse table:\n');
            this.displayStateStack(lastColumn.states, lines);
        } else {
            lines.push('Unexpected ' + tokenDisplay + '. Instead, I was expecting to see one of the following:\n');
            // Display a "state stack" for each expectant state
            // - which shows you how this state came to be, step by step.
            // If there is more than one derivation, we only display the first one.
            var stateStacks = expectantStates
                .map(function(state) {
                    return this.buildFirstStateStack(state, []) || [state];
                }, this);
            // Display each state that is expecting a terminal symbol next.
            stateStacks.forEach(function(stateStack) {
                var state = stateStack[0];
                var nextSymbol = state.rule.symbols[state.dot];
                var symbolDisplay = this.getSymbolDisplay(nextSymbol);
                lines.push('A ' + symbolDisplay + ' based on:');
                this.displayStateStack(stateStack, lines);
            }, this);
        }
        lines.push("");
        return lines.join("\n");
    }
    
    Parser.prototype.displayStateStack = function(stateStack, lines) {
        var lastDisplay;
        var sameDisplayCount = 0;
        for (var j = 0; j < stateStack.length; j++) {
            var state = stateStack[j];
            var display = state.rule.toString(state.dot);
            if (display === lastDisplay) {
                sameDisplayCount++;
            } else {
                if (sameDisplayCount > 0) {
                    lines.push('    ^ ' + sameDisplayCount + ' more lines identical to this');
                }
                sameDisplayCount = 0;
                lines.push('    ' + display);
            }
            lastDisplay = display;
        }
    };

    Parser.prototype.getSymbolDisplay = function(symbol) {
        return getSymbolLongDisplay(symbol);
    };

    /*
    Builds a the first state stack. You can think of a state stack as the call stack
    of the recursive-descent parser which the Nearley parse algorithm simulates.
    A state stack is represented as an array of state objects. Within a
    state stack, the first item of the array will be the starting
    state, with each successive item in the array going further back into history.

    This function needs to be given a starting state and an empty array representing
    the visited states, and it returns an single state stack.

    */
    Parser.prototype.buildFirstStateStack = function(state, visited) {
        if (visited.indexOf(state) !== -1) {
            // Found cycle, return null
            // to eliminate this path from the results, because
            // we don't know how to display it meaningfully
            return null;
        }
        if (state.wantedBy.length === 0) {
            return [state];
        }
        var prevState = state.wantedBy[0];
        var childVisited = [state].concat(visited);
        var childResult = this.buildFirstStateStack(prevState, childVisited);
        if (childResult === null) {
            return null;
        }
        return [state].concat(childResult);
    };

    Parser.prototype.save = function() {
        var column = this.table[this.current];
        column.lexerState = this.lexerState;
        return column;
    };

    Parser.prototype.restore = function(column) {
        var index = column.index;
        this.current = index;
        this.table[index] = column;
        this.table.splice(index + 1);
        this.lexerState = column.lexerState;

        // Incrementally keep track of results
        this.results = this.finish();
    };

    // nb. deprecated: use save/restore instead!
    Parser.prototype.rewind = function(index) {
        if (!this.options.keepHistory) {
            throw new Error('set option `keepHistory` to enable rewinding')
        }
        // nb. recall column (table) indicies fall between token indicies.
        //        col 0   --   token 0   --   col 1
        this.restore(this.table[index]);
    };

    Parser.prototype.finish = function() {
        // Return the possible parsings
        var considerations = [];
        var start = this.grammar.start;
        var column = this.table[this.table.length - 1]
        column.states.forEach(function (t) {
            if (t.rule.name === start
                    && t.dot === t.rule.symbols.length
                    && t.reference === 0
                    && t.data !== Parser.fail) {
                considerations.push(t);
            }
        });
        return considerations.map(function(c) {return c.data; });
    };

    function getSymbolLongDisplay(symbol) {
        var type = typeof symbol;
        if (type === "string") {
            return symbol;
        } else if (type === "object") {
            if (symbol.literal) {
                return JSON.stringify(symbol.literal);
            } else if (symbol instanceof RegExp) {
                return 'character matching ' + symbol;
            } else if (symbol.type) {
                return symbol.type + ' token';
            } else if (symbol.test) {
                return 'token matching ' + String(symbol.test);
            } else {
                throw new Error('Unknown symbol type: ' + symbol);
            }
        }
    }

    function getSymbolShortDisplay(symbol) {
        var type = typeof symbol;
        if (type === "string") {
            return symbol;
        } else if (type === "object") {
            if (symbol.literal) {
                return JSON.stringify(symbol.literal);
            } else if (symbol instanceof RegExp) {
                return symbol.toString();
            } else if (symbol.type) {
                return '%' + symbol.type;
            } else if (symbol.test) {
                return '<' + String(symbol.test) + '>';
            } else {
                throw new Error('Unknown symbol type: ' + symbol);
            }
        }
    }

    return {
        Parser: Parser,
        Grammar: Grammar,
        Rule: Rule,
    };

}));

},{}],4:[function(require,module,exports){
const nearley = require("nearley");
const grammar = require("./grammar.js");
const functions = require("./functions.js");

const funcParser = new nearley.Parser(nearley.Grammar.fromCompiled(functions));
const parser = new nearley.Parser(nearley.Grammar.fromCompiled(grammar));

function parse(text){

try {
    funcParser.feed(text);
}
catch(e){
    console.log("parse failed", e.message);
}

let funcVals = funcParser.results[0];

try {
  parser.feed(text);
}
catch(e){
  console.log("parse failed", e.message);
}

let value = parser.results[0];

return progn(funcVals, value);
}

function progn(funcVals, value){
  var output = "(\n[\n";
  var funcs = 0;
  for (let i = 0; i < funcVals.length; i++){
      if(funcVals[i] != null) {
        let formattedstr = convert(funcVals[i], -1);
        console.log(formattedstr);
        if(funcs === 0){
          output += formattedstr;
        } else {
          output += ";\n" + formattedstr;
        }
        funcs++;
      }
  }
  output += "\n],";
  for (let i = 1; i < value.length; i++){

    let formatedstr = convert(value[i]);
    output += formatedstr;
    console.log(formatedstr);
  }
    output += "\n);;";
    console.log(output);
  
  return output;
  }

function convert(arr, brackets){
  var result = "";
  var funcCount = -2;
  for(var i = 0; i < arr.length; i++){
      if(Array.isArray(arr[i])){
        if(arr[i][0] === "Fname"){
          funcCount = i;
        }
        //since the nested structure always? makes it 2 parentheses down
        if(arr[i][0] === "Record"){
          brackets = 2;
        }
        if (i === funcCount + 1){
          funcCount = -2;
          result += "[" + convert(arr[i], (brackets > 0 ? brackets - 1 : -1)) + "]";
        } else if(brackets === 0){
          result += "[" + convert(arr[i], (brackets > 0 ? brackets - 1 : -1)) + "]";
        } else {
          result += "(" + convert(arr[i], (brackets > 0 ? brackets - 1 : -1)) + ")";
        }
      } else {
          result += arr[i];
      }
      if(i < arr.length - 1){
          result += ",";
      }
  }
  return format(result);
}


function format(str){
    var newstr = "";
    for(var i = 0; i < str.length; i++){
      if(str[i] === "," && str[i+1] === "("){
        newstr += "(";
        i++;
        depth++;
      }
      else if((str[i] === ")" || str[i] === "]") && str[i + 1] === "," && (str[i + 2] === "(" || str[i + 2] === "[")){
        newstr += str[i] + "," + str[i + 2];
        i += 2;
      }
      else{
        newstr += str[i];
      }
    }
    //separate pass through to replace certain commas
    var depth = 0;
    var bracketDepth = -1;
    var laststr = "";
    for(var i = 0; i < newstr.length; i++){
      if (newstr[i] === "(" || newstr[i] === "["){
        depth++;
        if(newstr[i] === "["){
          bracketDepth = depth;
        }
      } else if (newstr[i] === ")" || newstr[i] === "]") {
        depth--;
        if(newstr[i] === "]"){
          bracketDepth = -1;
        }
      }
      if(newstr[i] === "," && (depth === bracketDepth)){
        laststr += ";"
        continue;
      }
      laststr += newstr[i];
    }

    return laststr;
  }

module.exports = parse;
},{"./functions.js":1,"./grammar.js":2,"nearley":3}]},{},[4])(4)
});
