const nearley = require("nearley");
const grammar = require("./jpdftwo.js");

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
    console.log(JSON.stringify(value[i]));
}
