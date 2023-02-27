
### Instructions

- npm install -g nearley
- npm install -g browserify
- npm init -y && npm i moo nearley
- nearleyc grammar.ne -o grammar.js
- browserify online-parser.js -o output.js -s parse

For the offline parser, put the program to be parsed in the input.txt file.