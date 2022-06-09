# jpdf

Core
----

mpc-examples.ml : the current main entry point for the project, with discussion and examples. 
jpd.ml : passive security verification based on joint probability distributions. 

Parser tool
-----------

This is not directly connected to the verifier in jpd.ml but helps experimentation with 
example protocols (easier to write in concrete syntax).

Makefile : not a real makefile, but a list of commands to build parser top-level loop.
parse.ml : the parser top-level loop code. Takes concrete syntax as input, dumps ast as output. 
jpdast.ml : the ast type.
jpdlexer.mll : ocamllex lexer code
jpdparser.mly : ocamlyacc parser code
