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

## Setting up cvc5

These instructions are for Debian-based Linux distributions. The steps for
Windows or MacOS will vary.

The steps about Java 8 are for an older version of `cvc5`. The newer versions
are compatible with more recent versions of Java.

I also think, but haven't tested, that without `--prefix` that the `cvc5`
shared object files will get dumped into somewhere that is already on the Java
system library path (and then you wouldn't have to move the `.so` flies or
tell Maven about where it can find them.

```
# Install some build tools
sudo apt install cmake m4

# Install an old JDK compatible with cmake or cvc5 or whatever
sudo apt install openjdk-8-jdk

# Use this JDK for the cvc5 build process
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64

# Clone the cvc5 repo from github
git clone https://github.com/cvc5/cvc5
cd cvc5

# Switch to latest stable branch
# Replace the version number if needed
git checkout cvc5-1.1.2

# Build the project and java bindings
./configure.sh production --java-bindings --auto-download --prefix=build/install
cd build
make
make install

# If the libraries have been moved to a standard location
java -jar target/MyProject-1.0-SNAPSHOT-jar-with-dependencies.jar

# If the libraries have not been moved
java -Djava.library.path="/home/bob/src/cvc5/build/install/lib" -jar target/MyProject-1.0-SNAPSHOT-jar-with-dependencies.jar
```

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


