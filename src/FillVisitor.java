package src;

import syntaxtree.*;
import visitor.*;
import java.util.Map;

public class FillVisitor extends GJDepthFirst<String, Symbol> {

    SymbolTable table;
    int classOffsset;
    SymbolTable currentMethodVariables;
    SymbolTable currentMethodArguments;
    SymbolTable currentClassVariables;
    SymbolTable currentParentClassVariables;
    SymbolClass currentClass;


    public FillVisitor(SymbolTable table) throws Exception {

        System.out.println("naii helooo");
        this.table = table;
        this.classOffsset = 0;
        currentMethodVariables = null;
        currentMethodArguments = null;
        currentClassVariables = null;
        currentParentClassVariables = null;
        currentClass = null;
    }


    @Override
    public String visit(Identifier n, Symbol argu) throws Exception {
        return n.f0.toString();
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    @Override
    public String visit(VarDeclaration n, Symbol argu) throws Exception {
        String type = n.f0.accept(this, argu);

        // check scope of variable name
        String name = n.f1.accept(this, argu);

        if ( (currentClassVariables != null && currentClassVariables.get(name) != null)   ||
             (currentMethodArguments != null && currentMethodArguments.get(name) != null) ||
             (currentMethodVariables != null && currentMethodVariables.get(name) != null)  )
            throw new Exception("Variable "+type+" "+name+" already declared in "+currentClass);

        currentClass.putVariable(name, new SymbolVariable(type, name, 0));

        return null;
    }

    /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
    @Override
    public String visit(Type n, Symbol argu) throws Exception {
        String type = n.f0.accept(this, argu);
        if (type.equals(Symbol.INT) || type.equals(Symbol.BOOL) || type.equals(Symbol.ARR)){
            return type;
        }
        else{
            if (table.get(type) != null)
                return type;
            else
                throw new Exception("Unknown type: "+type);
        }
    }

    /**
     * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */
    @Override
    public String visit(ArrayType n, Symbol argu) throws Exception {
        return "int[]";
    }

    /**
     * f0 -> "boolean"
    */
    @Override
    public String visit(BooleanType n, Symbol argu) throws Exception {
        return "boolean";
    }

    /**
     * f0 -> "int"
    */
    @Override
    public String visit(IntegerType n, Symbol argu) throws Exception {
        return "int";
    }

    /**
    * f0 -> "public"
    * f1 -> Type()
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( FormalParameterList() )?
    * f5 -> ")"
    * f6 -> "{"
    * f7 -> ( VarDeclaration() )*
    * f8 -> ( Statement() )*
    * f9 -> "return"
    * f10 -> Expression()
    * f11 -> ";"
    * f12 -> "}"
    */
    public String visit(MethodDeclaration n, Symbol argu) throws Exception {
        String type = n.f1.accept(this, argu);
        String name = n.f2.accept(this, argu);
        // check scope of method
        if (currentClass.methods.get(name) != null)
            throw new Exception("Method "+name+" already declared in "+currentClass);
        // ckeck for overriding
        SymbolMethod currentMethod;
        if ((currentClass.parentClass == null ) || (currentClass.parentClass.methods.get(name) == null))
            currentMethod = new SymbolMethod(type, name, false);
        else
            currentMethod = new SymbolMethod(type, name, true); // overrided  
            
        currentClass.putMethod(name, currentMethod);
    

        // n.f3.accept(this, argu);
        // n.f4.accept(this, argu);
        // n.f5.accept(this, argu);
        // n.f6.accept(this, argu);
        // n.f7.accept(this, argu);
        // n.f8.accept(this, argu);
        // n.f9.accept(this, argu);
        // n.f10.accept(this, argu);
        // n.f11.accept(this, argu);
        // n.f12.accept(this, argu);
        return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    @Override
    public String visit(MainClass n, Symbol argu) throws Exception {

        // String classname = n.f1.accept(this, null);
        // SymbolClass mainClass = new SymbolClass(classname, this.classOffsset);
        // if ( table.put(classname, mainClass) != null )
        //     throw new Exception(mainClass+" is already declared");
        // this.classOffsset++;

        SymbolClass mainClass = (SymbolClass) table.get(n.f1.accept(this, null));
        // fill main method
        SymbolMethod mainMethod = new SymbolMethod("void", "main", false);
        if ( mainClass.methods.put("main", mainMethod) != null )
            throw new Exception("main method is already declared");

        // fill argument
        String argument = n.f11.accept(this, null);
        SymbolVariable mainArgument = new SymbolVariable("void", argument);
        if ( mainMethod.arguments.put("main", mainMethod) != null )
            throw new Exception("main argument is already declared");
        

        // fill var declarations
        // currentMethodVariables = mainClass.methods.variables;
        // currentMethodArguments = mainMethod.arguments;
        // currentClassVariables = mainClass.variables;
        // currentParentClassVariables = null;

        return null;
    }

    /**
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    */
    @Override
    public String visit(ClassDeclaration n, Symbol argu) throws Exception {

        // SymbolClass classDeclaration = new SymbolClass(classname, this.classOffsset);
        // if ( table.put(classname, classDeclaration) != null )
        //     throw new Exception(classDeclaration+" is already declared");
        // this.classOffsset++;
        currentClass = (SymbolClass)table.get(n.f1.accept(this, null));
        // System.out.println(currentClass);


        // fill class variable declarations
        currentClassVariables = currentClass.variables;
        currentMethodArguments = null;
        currentMethodVariables = null;
        if (n.f3.present())
            n.f3.accept(this, currentClass);
            
        // fill methods
        if (n.f4.present())
            n.f4.accept(this, currentClass);



        
        return null;
     }
  
     /**
      * f0 -> "class"
      * f1 -> Identifier()
      * f2 -> "extends"
      * f3 -> Identifier()
      * f4 -> "{"
      * f5 -> ( VarDeclaration() )*
      * f6 -> ( MethodDeclaration() )*
      * f7 -> "}"
      */
    @Override
    public String visit(ClassExtendsDeclaration n, Symbol argu) throws Exception {

        currentClass = (SymbolClass)table.get(n.f1.accept(this, null));


        // fill class variable declarations
        currentClassVariables = currentClass.variables;
        currentMethodArguments = null;
        currentMethodVariables = null;
        if (n.f5.present())
            n.f5.accept(this, currentClass);

        // fill methods
        if (n.f6.present())
            n.f6.accept(this, currentClass);


        
        return null;
     }


}