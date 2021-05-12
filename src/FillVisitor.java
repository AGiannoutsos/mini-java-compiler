package src;

import syntaxtree.*;
import visitor.*;
import java.util.Map;

public class FillVisitor extends GJDepthFirst<String, Symbol> {

    SymbolTable table;
    int classOffsset;
    int argumentVarOffsset;


    public FillVisitor(SymbolTable table) throws Exception {
        this.table = table;
        this.classOffsset = 0;
        this.argumentVarOffsset = 0;
    }


    @Override
    public String visit(Identifier n, Symbol thisScope) throws Exception {
        return n.f0.toString();
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    @Override
    public String visit(VarDeclaration n, Symbol thisScope) throws Exception {
        String type = n.f0.accept(this, thisScope);

        // check scope of variable name
        String name = n.f1.accept(this, thisScope);

        if ( thisScope.getVariable(name) != null)
           throw new Exception("Variable "+type+" "+name+" already declared in "+thisScope.name+"()");
        
        thisScope.putVariable(name, new SymbolVariable(type, name, this.argumentVarOffsset++));

        return null;
    }

    /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
    @Override
    public String visit(Type n, Symbol thisScope) throws Exception {
        String type = n.f0.accept(this, thisScope);
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
    public String visit(ArrayType n, Symbol thisScope) throws Exception {
        return "int[]";
    }

    /**
     * f0 -> "boolean"
    */
    @Override
    public String visit(BooleanType n, Symbol thisScope) throws Exception {
        return "boolean";
    }

    /**
     * f0 -> "int"
    */
    @Override
    public String visit(IntegerType n, Symbol thisScope) throws Exception {
        return "int";
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
    @Override
    public String visit(FormalParameter n, Symbol thisScope) throws Exception {
		String type = n.f0.accept(this, thisScope);
		String name = n.f1.accept(this, thisScope);
        
        if (thisScope.getArgument(name) != null)
            throw new Exception("Argument "+name+" already declared in "+thisScope.name+"()");
        
		SymbolVariable argument = new SymbolVariable(type, name, this.argumentVarOffsset++);
        thisScope.putArgument(name, argument);

		return null;
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
    @Override
    public String visit(MethodDeclaration n, Symbol thisScope) throws Exception {
        String type = n.f1.accept(this, thisScope);
        String name = n.f2.accept(this, thisScope);

        // check scope of method
        if (thisScope.getMethod(name) != null)
            throw new Exception("Method "+name+"() already declared in Class "+thisScope.name);
        // ckeck for overriding
        SymbolClass thisClass = (SymbolClass)thisScope;
        SymbolClass parentClass = thisClass.parentClass;
        SymbolMethod thisMethod = (SymbolMethod)thisScope.getMethod(name);
        SymbolMethod overritedMethod;
        
        if ((thisClass.parentClass == null ) || (thisClass.parentClass.getMethod(name) == null))
            thisMethod = new SymbolMethod(type, name, false, thisClass);
        else
            thisMethod = new SymbolMethod(type, name, true, thisClass); // overrided  
            
        thisClass.putMethod(name, thisMethod);

        // fill parameters
        this.argumentVarOffsset = 0;
        if (n.f4.present())
            n.f4.accept(this, thisMethod);
            
        // fill variable declarations
        this.argumentVarOffsset = 0;
        if (n.f7.present())
            n.f7.accept(this, thisMethod);
        // thisMethod.print();

        // after parameters have been initialized check for correct overriding
        while(parentClass != null){
            if (parentClass.getMethod(name) != null){
                overritedMethod = (SymbolMethod)parentClass.getMethod(name);
                // check if return type matches
                if (!CheckTypeVisitor.checkType(thisMethod, overritedMethod.type, table))
                    throw new Exception("Overrided method "+name+"() has wrong return type declared in Class "+parentClass.name);

                // check for arguments
                String[] argumentStrings = overritedMethod.getStringArguments().split(", ");
                String[] declaredArgumentStrings = thisMethod.getStringArguments().split(", ");
                if(argumentStrings.length == declaredArgumentStrings.length){
                    int arg = 0;
                    for (Map.Entry<String, Symbol> entry : thisMethod.arguments.getSorted()) {
                        if(!CheckTypeVisitor.checkType(entry.getValue(), argumentStrings[arg], table))
                            throw new Exception("Argument types do not match in overrided method "+thisMethod.name+"() in Class "+thisScope.name);
                        arg++;
                    }
                } else{
                    throw new Exception("Argument types do not match in overrided method "+thisMethod.name+"() in Class "+thisScope.name);
                }
            }
            parentClass = parentClass.parentClass;
        } 

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
    public String visit(MainClass n, Symbol thisScope) throws Exception {

        SymbolClass mainClass = (SymbolClass) table.get(n.f1.accept(this, null));
        // fill main method
        if ( mainClass.getMethod("main") != null )
            throw new Exception("main method is already declared");

        SymbolMethod mainMethod = new SymbolMethod("void", "main", false, mainClass);
        mainClass.putMethod("main", mainMethod);

        // fill arguments
        String argument = n.f11.accept(this, null);
        if ( mainMethod.getArgument(argument) != null )
            throw new Exception("main argument is already declared");
        
        SymbolVariable mainArgument = new SymbolVariable("String[]", argument);
        mainMethod.putArgument(argument, mainArgument);
        
        // fill variable declarations
        if (n.f14.present())
            n.f14.accept(this, mainMethod);
        // mainMethod.print();
        

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
    public String visit(ClassDeclaration n, Symbol thisScope) throws Exception {

        SymbolClass thisClass = (SymbolClass)table.get(n.f1.accept(this, null));

        // fill class variable declarations
        if (n.f3.present())
            n.f3.accept(this, thisClass);
            
        // fill methods
        if (n.f4.present())
            n.f4.accept(this, thisClass);
        
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
    public String visit(ClassExtendsDeclaration n, Symbol thisScope) throws Exception {

        SymbolClass thisClass = (SymbolClass)table.get(n.f1.accept(this, null));

        // fill class variable declarations
        if (n.f5.present())
            n.f5.accept(this, thisClass);

        // fill methods
        if (n.f6.present())
            n.f6.accept(this, thisClass);

        return null;
     }


}