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
- Maven is required to run this project.
- CVC5 should be also installed, and then environment variable (CVC5_LIB_PATH) for CVC5 library path should be added in `pom.xml`.
- `mvn exec:exec` runs main test, which is in /src/main/java/plaid/App.java.
- `mvn test exec:exec` runs the main test along with unit tests.

-------
## License
PLAID Lab, University of Vermont


