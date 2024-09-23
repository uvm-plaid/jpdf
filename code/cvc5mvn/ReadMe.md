# Prelude-Overture
-----
## Overview
- This is source codes for Prelude-Overture project.
- Both Prelude and Overture are languages for multi-party computation.
- Prelude is a high-level language that sketches protocols and circuits. This evaluates to a low-level language, Overture, where we can check correctness using an SMT-solver in CVC5.
- The project contains:
    1) Prelude grammar including its lexer and parser
    2) Prelude interpreter that evaluates to Overture
    3) Overture grammar including its lexer and parser
    4) Overture solver that translates Overture program into SMT solver language
- We used ANTLR to write the grammars/lexer/parser.
- Java is the primary language for this project implementation.

----
## Directories
- `/src/main/java/plaid`: Java source code
- `/src/main/antlr4/plaid`: Antlr4 grammars
- `/src/test/java/plaid`: Unit tests
- `pom.xml`: Pom file

------
## Installation & Running
- Maven is required to run this project.
- CVC5 should be also installed, and then environment variable (CVC5_LIB_PATH) for CVC5 library path should be added in `pom.xml`.
- `mvn exec:exec` runs main test, which is in /src/main/java/plaid/App.java.
- `mvn test exec:exec` runs the main test along with unit tests.

-------
## License
PLAID Lab, University of Vermont
