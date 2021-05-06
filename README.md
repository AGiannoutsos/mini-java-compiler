# mini-java-compiler
Mini-Java compiler semester project for the compilers course at the University of Athens 


## Compilation and run
`make`

`java Main <inputFile1> <inputFile3> <inputFile3> ...`

or run the experiments with
`./run.sh`

## About the implementation
All the classes, variables, and methods are stored on the `SumbolTable`. The `SymbolTable` class inherits from the `TreeMap` data structure in java and it stores objects of the `Symbol` type.
`Symbol` class is an abstract class that all the classes, variables, and methods inherit from. It shares the common information about these fields such as name, type, and offset.
In `SymbolCLass` class are stored all the classes. There are fields for the methods and the variables of the classes. These fields are stored in a SymbolTable too. 
In `SumbolMethods` are stored all the methods of the class. This object also stores the variables and the arguments of the method. Finally  `SymbolVariable` stores the variables. 
Both `SYmbolClass` and `SymbolMethod` implement setters and getters according to their needs.

As for the visitors, there are 3 of them that implement different processes of semantic checking. Firstly the parse tree is visited by the `ClassDeclarationVisitor` is responsible 
for adding to the `SymbolTable` all 
the class declarations. Then the parse tree is visited by the `FillVisitor` that fills all the classes with their variables and methods and also fills the arguments and the variables of the methods too.
Finally, the parse tree is visited by the `CheckTypeVisitor` which performs type checking of the expressions and the statements of the methods.
