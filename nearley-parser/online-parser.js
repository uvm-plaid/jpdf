const nearley = require("nearley");
const grammar = require("./jpdftwo.js.js");

const parser = new nearley.Parser(nearley.Grammar.fromCompiled(grammar));

function parse(text){

try {
    parser.feed(text);
}
catch(e){
    console.log("parse failed", e.message);
}

let value = parser.results.flat();

    var newstring = "";

for (let i = 0; i < value.length; i++){

    let formatedstr = convert(value[i]);
    newstring += formatedstr;
    console.log(formatedstr);
}

return newstring;

}


function convert(arr){
    var result = "";
    for(var i = 0; i < arr.length; i++){
        if(Array.isArray(arr[i])){
            result += "(" + convert(arr[i]) + ")";
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
      }
      else if(str[i] === ")" && str[i+1] === "," && str[i+2] === "("){
        newstr += "),(";
        i += 2;
      }
      else{
        newstr += str[i];
      }
    }
    return newstr;
  }

module.exports = parse;