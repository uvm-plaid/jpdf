
# jpdf

Core
----

- mpc-examples.ml : the current main entry point for the project, with discussion and examples.

- jpd.ml : passive security verification based on joint probability distributions.


Parser tool
-----------

This is not directly connected to the verifier in jpd.ml but helps experimentation with

example protocols (easier to write in concrete syntax). It provides top-level loop where input

code is converted to ast.

- Makefile : not a real makefile, but a list of commands to build parser top-level loop.

- parse.ml : the parser top-level loop code. Takes concrete syntax as input, dumps ast as output.

- jpdast.ml : the ast type.

- jpdlexer.mll : ocamllex lexer code

- jpdparser.mly : ocamlyacc parser code


## Logic Program Transformations
  

#### Eliminate Negation

- To convert into a definite program, we remove negation via the creation of new auxiliary variables/atoms. For each negated variable, create a new associated variable to replace that and any other instance. Later, when iterating over the combinations of facts, ensure that any such associated variables are never both true or both false.

- In hybrid2.ml, we prepend "not" to any negated variables. This is performed at the bottom of `truth_tables` for the negated version of the view/head, and in `remove_negation_lists` for the body of each clause. Currently, this is applied to every head regardless of whether it's used in the body of some other clause.


#### Multiple Definitions

- To make the program singly defined, we ensure that every variable is only be defined by one clause with more than one atom in its body. Once again, we create auxiliary variables for each body of a multiply defined variable, then add a new rule which has the original head getting all the new variables delimited with or's. This is the same as multiple definitions with one atom in each body.

- In `hybrid2.ml`, this is performed in `multiple_defs`. The current convention is to append a `.x` to a variable name, where `x` is and integer incremented from 0 for each new auxiliary variable generated from that rule.


#### Example
Given the following program:
```
let f0 = flip[1,"0"] in
let f1 = flip[1,"1"] in
let f2 = select(s[1,"0"], f0, f1) in
v[0,"0"] := f2;
v[0,"1"] := f0
```

The corresponding logic program would be:
```
V_0_0       <- ¬F_1_0, F_1_1, ¬S_1_0
V_0_0       <- F_1_0, ¬F_1_1, S_1_0
V_0_0       <- F_1_0, F_1_1, ¬S_1_0
V_0_0       <- F_1_0, F_1_1, S_1_0
V_0_1       <- F_1_0
```

After running the step to make it a definite program:
```
V_0_0     <- notF_1_0, F_1_1, notS_1_0
V_0_0     <- F_1_0, notF_1_1, S_1_0
V_0_0     <- F_1_0, F_1_1, notS_1_0
V_0_0     <- F_1_0, F_1_1, S_1_0
notV_0_0  <- notF_1_0, notF_1_1, notS_1_0
notV_0_0  <- notF_1_0, notF_1_1, S_1_0
notV_0_0  <- notF_1_0, F_1_1, S_1_0
notV_0_0  <- F_1_0, notF_1_1, notS_1_0
V_0_1       <- F_1_0
notV_0_1    <- notF_1_0
```

Lastly, after the step to make it singly defined:
```
V_0_0.0     <- notF_1_0, F_1_1, notS_1_0
V_0_0.1     <- F_1_0, notF_1_1, S_1_0
V_0_0.2     <- F_1_0, F_1_1, notS_1_0
V_0_0.3     <- F_1_0, F_1_1, S_1_0
V_0_0       <- V_0_0.0
V_0_0       <- V_0_0.1
V_0_0       <- V_0_0.2
V_0_0       <- V_0_0.3
notV_0_0.0  <- notF_1_0, notF_1_1, notS_1_0
notV_0_0.1  <- notF_1_0, notF_1_1, S_1_0
notV_0_0.2  <- notF_1_0, F_1_1, S_1_0
notV_0_0.3  <- F_1_0, notF_1_1, notS_1_0
notV_0_0    <- notV_0_0.0
notV_0_0    <- notV_0_0.1
notV_0_0    <- notV_0_0.2
notV_0_0    <- notV_0_0.3
V_0_1       <- F_1_0
notV_0_1    <- notF_1_0
```