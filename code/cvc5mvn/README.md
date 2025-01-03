# Prelude-Overture

-----
## Overview
- This is source codes for Prelude-Overture project.
- Both Prelude and Overture are languages for multi-party computation.
- Prelude is a high-level language that sketches protocols and circuits. This evaluates to a low-level language, Overture, where we can check correctness using an SMT-solver in CVC5.
- The project contains:
    1) Prelude/Overture grammar including its lexer and parser
    2) Prelude interpreter that evaluates to Overture
    3) TermFactory that translates Overture protocol into CVC5 language
    4) Verifier that checks if an Overture protocol entails a certain constraint
- We used ANTLR to write the grammars/lexer/parser.
- Java and Scala are the primary languages for this project implementation.
----
## Directories
- [**src**](src)
    - [**main**](src/main)
        - [**antlr4**](src/main/antlr4)
            - [**plaid**](src/main/antlr4/plaid): Prelude/Overture and Constraints Grammars
        - [**java**](src/main/java)
            - [**plaid**](src/main/java/plaid)
                - [**antlr**](src/main/java/plaid/antlr): AST translations
                - [**cvc**](src/main/java/plaid/cvc): CVC5 transformations
                - [**eval**](src/main/java/plaid/eval): Evaluation
                - [**App.java**](src/main/java/plaid/App.java): Main Test
        - [**prelude-overture**](src/main/prelude-overture): Prelude src examples
        - [**scala**](src/main/scala)
            - [**plaid**](src/main/scala/plaid)
                - [**ast**](src/main/scala/plaid/ast): AST for Prelude/Overture
                  - [**constraints.ast**](src/main/scala/plaid/constraints.ast): AST for Constraints
                - [**eval**](src/main/scala/plaid/eval): OvertureChecker
    - [**test**](src/test): Unit tests

------
## Installation & Running

Maven is required to build this project.
```
mvn package
```

CVC5 needs to be installed to run this project, along with its Java bindings. The path to the CVC5 libraries then need to be added to Java's library path. The actual path will depend on how the CVC5 build was configured.

On MacOS,
```
export DYLD_LIBRARY_PATH=/usr/local/lib
```

On Linux,
```
export LD_LIBRARY_PATH=/usr/local/lib
```

To run the tool on an example,
```
java -jar target/prelude.jar src/main/prelude-overture/beaver-pre.txt
```

Further documentation about supported parameters is available via
```
java -jar target/prelude.jar --help
```

-------
## License
PLAID Lab, University of Vermont


