ocamllex jpdlexer.mll       
ocamlyacc jpdparser.mly
ocamlc -c jpdast.ml
ocamlc -c jpdparser.mli
ocamlc -c jpdlexer.ml
ocamlc -c jpdparser.ml
ocamlc -c parse.ml
ocamlc -o parse jpdast.cmo jpdlexer.cmo jpdparser.cmo parse.cmo
