const nearley = require("nearley");
const grammar = require("./grammar.js");

const parser = new nearley.Parser(nearley.Grammar.fromCompiled(grammar));

const fs = require('fs');

let text = fs.readFileSync("./test1.txt").toString('utf-8');
//console.log(text);

try {
    //let input = readline.question("input:");
    parser.feed(text);
    //console.log("parse success", parser.results);
}
catch(e){
    console.log("parse failed", e.message);
}

let value = parser.results.flat();

for (let i = 0; i < value.length; i++){
    //console.log(JSON.stringify(value[i]));
    let formattedstr = convert(value[i]);
    console.log(formattedstr);
}


//console.log(JSON.stringify(parser.results));


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
    //console.log(newstr);
    return newstr;
  }
