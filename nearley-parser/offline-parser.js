const nearley = require("nearley");
const grammar = require("./grammar.js");
const functions = require("./functions.js");

const funcParser = new nearley.Parser(nearley.Grammar.fromCompiled(functions));
const parser = new nearley.Parser(nearley.Grammar.fromCompiled(grammar));

const fs = require('fs');

let text = fs.readFileSync("./input.txt").toString('utf-8');

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
  console.log("\nparse failed", e.message);
}

let value = parser.results[0];

// console.log(value.length);

// for (let i = 0; i < value.length; i++){
//   console.log(value[i]);
// }

// console.log("\n");

progn(funcVals, value);

function progn(funcVals, value){
  var output = "(\n[\n";
  var funcs = 0;
  for (let i = 0; i < funcVals.length; i++){
      if(funcVals[i] != null) {
        let formattedstr = convert(funcVals[i], -1);
        if(funcs === 0){
          output += formattedstr;
        } else {
          output += ";\n" + formattedstr;
        }
        funcs++;
      }
  }
  output += "\n],\n";
  for (let i = 0; i < value.length; i++){
    if(value[i] != null){
      let formatedstr = convert(value[i]);
      output += formatedstr;
    //console.log(formatedstr);
    }
  }
    output += "\n);;";
    console.log(output);
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
        if(arr[i][0] === "Record" || arr[i][0] === "RecTy"){
          brackets = 2;
        }
        if(arr[i][0] === "Let" || arr[i][0] === "Seq") {
          result += "\n";
        }
        if (i === funcCount + 1){
          funcCount = -2;
          result += "[" + convert(arr[i], (brackets > 0 ? brackets - 1 : -1)) + "]";
        } else if(brackets === 0){
          result += "[" + convert(arr[i], (brackets > 0 ? brackets - 1 : -1)) + "]";
        } else {
          result += "(" + convert(arr[i], (brackets > 0 ? brackets - 1 : -1)) + ")";
        }
        brackets = -1;
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
    //i gave up trying to count depth in that loop since it skips i's
    var depth = 0;
    var bracketDepth = [];
    var laststr = "";
    for(var i = 0; i < newstr.length; i++){
      if (newstr[i] === "(" || newstr[i] === "["){
        depth++;
        if(newstr[i] === "["){
          bracketDepth.push(depth);
        }
      } else if (newstr[i] === ")" || newstr[i] === "]") {
        depth--;
        if(newstr[i] === "]"){
          bracketDepth.pop();
        }
      }
      if(newstr[i] === "," && (bracketDepth.includes(depth))){
        laststr += ";"
        continue;
      }
      laststr += newstr[i];
    }

    return laststr;
  }