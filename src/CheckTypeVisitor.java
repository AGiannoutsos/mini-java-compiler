package src;

import syntaxtree.*;
import visitor.*;
import java.util.Map;
import java.util.ArrayDeque;
import java.util.Deque;


public class CheckTypeVisitor extends GJDepthFirst<String, Symbol> {

    SymbolTable table;
    int classOffsset;
    Deque<String> argumentStack;


    public CheckTypeVisitor(SymbolTable table) throws Exception {
        this.table = table;
        this.classOffsset = 0;
        this.argumentStack = new ArrayDeque<String>();
    }

    public boolean checkType(Symbol symbol, String type) throws Exception {
        if (type.equals(Symbol.INT) || type.equals(Symbol.BOOL) || type.equals(Symbol.ARR)){
            return symbol.type.equals(type);
        }
        // check for inheritance type, symbol must be a class
        // get the class type of variable
        SymbolClass thisClass = (SymbolClass)table.get(type);
        if (thisClass == null)
            throw new Exception("Unknown Identifier type "+type);
        if (thisClass.type.equals(symbol.type))
            return true;
        else { // check in the chain of inheritance 
            thisClass = thisClass.parentClass;
            while (thisClass != null){
                if (thisClass.type.equals(symbol.type))
                    return true;
                thisClass = thisClass.parentClass;
            }
            return false;
        }
    }


    @Override
    public String visit(Identifier n, Symbol thisScope) throws Exception {
        return n.f0.toString();
    }

    // /**
    // * f0 -> Type()
    // * f1 -> Identifier()
    // * f2 -> ";"
    // */
    // @Override
    // public String visit(VarDeclaration n, Symbol thisScope) throws Exception {
    //     String type = n.f0.accept(this, thisScope);

    //     // check scope of variable name
    //     String name = n.f1.accept(this, thisScope);

    //     if ( thisScope.getVariable(name) != null)
    //        throw new Exception("Variable "+type+" "+name+" already declared in "+thisScope);
        
    //     thisScope.putVariable(name, new SymbolVariable(type, name, 0));

    //     return null;
    // }

    // /**
    // * f0 -> ArrayType()
    // *       | BooleanType()
    // *       | IntegerType()
    // *       | Identifier()
    // */
    // @Override
    // public String visit(Type n, Symbol thisScope) throws Exception {
    //     String type = n.f0.accept(this, thisScope);
    //     if (type.equals(Symbol.INT) || type.equals(Symbol.BOOL) || type.equals(Symbol.ARR)){
    //         return type;
    //     }
    //     else{
    //         if (table.get(type) != null)
    //             return type;
    //         else
    //             throw new Exception("Unknown type: "+type);
    //     }
    // }

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
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
    public String visit(Statement n, Symbol thisScope) throws Exception {
        return n.f0.accept(this, thisScope);
    }

    /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public String visit(AssignmentStatement n, Symbol thisScope) throws Exception {
        String name = n.f0.accept(this, thisScope);
        SymbolMethod thisMethod = (SymbolMethod) thisScope;
        SymbolVariable thisVariable = (SymbolVariable) thisMethod.getVariable_r(name);
        if (thisVariable == null)
            throw new Exception("Unknown Identifier "+name+" at Assignment in "+thisScope.name+"()");

        // get expression type
        String expressionType = n.f2.accept(this, thisScope);
        // System.out.println(expressionType);
        // check type
        if (!checkType(thisVariable, expressionType))
            throw new Exception("Wrong type "+expressionType+" at Assignment in "+thisScope.name+"()");
        return null;
    }

    /**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
    public String visit(ArrayAssignmentStatement n, Symbol thisScope) throws Exception {
        String name = n.f0.accept(this, thisScope);
        SymbolMethod thisMethod = (SymbolMethod) thisScope;
        SymbolVariable thisVariable = (SymbolVariable) thisMethod.getVariable_r(name);
        if (thisVariable == null)
            throw new Exception("Unknown Identifier "+name+" at Array Assignment in "+thisScope.name+"()");

        // check for identifier type
        if (!checkType(thisVariable, Symbol.ARR))
            throw new Exception("Wrong type "+thisVariable.type+" at Array Assignment in "+thisScope.name+"()");
        
        // check index expression type
        String expressionType = n.f2.accept(this, thisScope);
        if (!expressionType.equals(Symbol.INT))
            throw new Exception("Wrong expression type "+expressionType+" at Array Assignment index in "+thisScope.name+"()");

        // check assignment expression type
        String returnExpressionType = n.f5.accept(this, thisScope);
        if (!returnExpressionType.equals(Symbol.INT))
            throw new Exception("Wrong assignment expression type "+returnExpressionType+" at Array Assignment in "+thisScope.name+"()");

        return null;
    }

    /**
     * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
    public String visit(IfStatement n, Symbol thisScope) throws Exception {
        // check index expression type
        String expressionType = n.f2.accept(this, thisScope);
        if (!expressionType.equals(Symbol.BOOL))
            throw new Exception("Wrong expression type "+expressionType+" at If Statement "+thisScope.name+"()");
        n.f4.accept(this, thisScope);
        n.f6.accept(this, thisScope);
        return null;
    }

    /**
     * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
    public String visit(WhileStatement n, Symbol thisScope) throws Exception {
        // check index expression type
        String expressionType = n.f2.accept(this, thisScope);
        if (!expressionType.equals(Symbol.BOOL))
            throw new Exception("Wrong expression type "+expressionType+" at While Statement "+thisScope.name+"()");
        n.f4.accept(this, thisScope);
        return null;
    }

    /**
     * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
    public String visit(PrintStatement n, Symbol thisScope) throws Exception {
        // check index expression type
        String expressionType = n.f2.accept(this, thisScope);
        if (!expressionType.equals(Symbol.INT))
            throw new Exception("Wrong expression type "+expressionType+" at Print Statement "+thisScope.name+"()");
        n.f4.accept(this, thisScope);
        return null;
    }

    /**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | PrimaryExpression()
    */
    public String visit(Expression n, Symbol thisScope) throws Exception {            
            return n.f0.accept(this, thisScope);
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
    public String visit(AndExpression n, Symbol thisScope) throws Exception {
        String type1 = n.f0.accept(this, thisScope);
        String type2 = n.f2.accept(this, thisScope);

        if (type1.equals(Symbol.BOOL) && type2.equals(Symbol.BOOL))
            return Symbol.BOOL;
        else
            throw new Exception("Wrong type "+type1+" && "+type2+" at And Expression in "+thisScope);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    public String visit(CompareExpression n, Symbol thisScope) throws Exception {
        String type1 = n.f0.accept(this, thisScope);
        String type2 = n.f2.accept(this, thisScope);

        if (type1.equals(Symbol.INT) && type2.equals(Symbol.INT))
            return Symbol.BOOL;
        else
            throw new Exception("Wrong type "+type1+" < "+type2+" at Copmare Expression in "+thisScope);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    public String visit(PlusExpression n, Symbol thisScope) throws Exception {
        String type1 = n.f0.accept(this, thisScope);
        String type2 = n.f2.accept(this, thisScope);

        if (type1.equals(Symbol.INT) && type2.equals(Symbol.INT))
            return Symbol.INT;
        else
            throw new Exception("Wrong type "+type1+" + "+type2+" at Plus Expression in "+thisScope);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    public String visit(MinusExpression n, Symbol thisScope) throws Exception {
        String type1 = n.f0.accept(this, thisScope);
        String type2 = n.f2.accept(this, thisScope);

        if (type1.equals(Symbol.INT) && type2.equals(Symbol.INT))
            return Symbol.INT;
        else
            throw new Exception("Wrong type "+type1+" - "+type2+" at Minus Expression in "+thisScope);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    public String visit(TimesExpression n, Symbol thisScope) throws Exception {
        String type1 = n.f0.accept(this, thisScope);
        String type2 = n.f2.accept(this, thisScope);

        if (type1.equals(Symbol.INT) && type2.equals(Symbol.INT))
            return Symbol.INT;
        else
            throw new Exception("Wrong type "+type1+" * "+type2+" at Times Expression in "+thisScope);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    public String visit(ArrayLookup n, Symbol thisScope) throws Exception {
        String type1 = n.f0.accept(this, thisScope);
        String type2 = n.f2.accept(this, thisScope);

        if (type1.equals(Symbol.ARR) && type2.equals(Symbol.INT))
            return Symbol.INT;
        else
            throw new Exception("Wrong type "+type1+"["+type2+"] at Array Lookup Expression in "+thisScope);
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
    public String visit(ArrayLength n, Symbol thisScope) throws Exception {
        String type1 = n.f0.accept(this, thisScope);

        if (type1.equals(Symbol.ARR))
            return Symbol.INT;
        else
            throw new Exception("Wrong type "+type1+" at Array Length Expression in "+thisScope);
    }

    /**
    * f0 -> Expression()
    * f1 -> ExpressionTail()
    */
    public String visit(ExpressionList n, Symbol thisScope) throws Exception {
        String argumentsTypes = n.f0.accept(this, thisScope);
        argumentStack.push(argumentsTypes);
        // get the rest parameters
        n.f1.accept(this, thisScope);
        // pop last from stack
        return argumentStack.pop();
    }

    /**
     * f0 -> ","
    * f1 -> Expression()
    */
    public String visit(ExpressionTerm n, Symbol thisScope) throws Exception {
        String argumentsTypes = argumentStack.pop();
        argumentsTypes += ", " + n.f1.accept(this, thisScope);
        argumentStack.push(argumentsTypes);
        return argumentsTypes;
    }


    /**
     * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    public String visit(MessageSend n, Symbol thisScope) throws Exception {

        // check if primary expression is class type
        String type = n.f0.accept(this, thisScope);
        SymbolClass thisClass = (SymbolClass) table.get(type);
        if (thisClass == null) 
            throw new Exception("Operator . unavailable on type: "+type+" in "+thisScope);
        
        // check if method is availble on that class
        String method = n.f2.accept(this, thisScope);
        SymbolMethod thisMethod = (SymbolMethod) thisClass.getMethod_r(method);
        if (thisMethod == null) 
            throw new Exception("Method "+method+"() is unavailable on type: "+type+" in "+thisScope);
        

        // check for arguments on method
        String argumentString = "";
        String[] argumentStrings;
        String declaredArgumentString = thisMethod.getStringArguments();
        String[] declaredArgumentStrings = thisMethod.getStringArguments().split(", ");
        if (n.f4.present()) {
            argumentString = n.f4.accept(this, thisScope);
            argumentStrings = n.f4.accept(this, thisScope).split(", ");
            

            // System.out.println(argumentString);
            // System.out.println(argumentString.length());
            // System.out.println(declaredArgumentString);
            // System.out.println(argumentString.length());
            // if (!declaredArgumentString.equals(argumentString))
            if(argumentStrings.length == declaredArgumentStrings.length){
                int arg = 0;
                for (Map.Entry<String, Symbol> entry : thisMethod.arguments.getSorted()) {
                    if(!checkType(entry.getValue(), argumentStrings[arg]))
                        throw new Exception("Argument types do not match at Method "+method+"() in "+thisScope);
                    arg++;
                }
            } else{
                throw new Exception("Argument types do not match at Method "+method+"() in "+thisScope);
            }
        }
            
        

        
        return thisMethod.type;
    }

    /**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | NotExpression()
    *       | BracketExpression()
    */
    public String visit(PrimaryExpression n, Symbol thisScope) throws Exception {
        // check for type of primary expression
        String type = n.f0.accept(this, thisScope);
        if (type.equals(Symbol.INT) || type.equals(Symbol.BOOL) || type.equals(Symbol.ARR)){
            return type;
        }
        // else{
        //     if (table.get(type) != null)
        //         return type;
        // }
        if (type.startsWith("Class ")){
            if (table.get(type.replaceFirst("Class ", "")) != null)
                return type.replaceFirst("Class ", "");
            else
                throw new Exception("Unknown Expression Class Identifier: "+type+" type in "+thisScope);
        }
        if (type.startsWith("Bracket ")){
            return type.replaceFirst("Bracket ", "");
        }
        // if type is not known then type comes from identifier as variable
        // so we need to look up for it
        String identifier = type;
        SymbolMethod thisMethod = (SymbolMethod)thisScope;
        SymbolVariable thisVariable = (SymbolVariable)thisMethod.getVariable_r(identifier);
        // if identifier is not defined the type is not known
        if (thisVariable == null)
            throw new Exception("Unknown Expression Identifier: "+type+" type: "+type+" in "+thisScope);
        else
            return thisVariable.type;
        
    }

    /**
     * f0 -> <INTEGER_LITERAL>
    */
    public String visit(IntegerLiteral n, Symbol thisScope) throws Exception {
        return Symbol.INT;
    }

    /**
    * f0 -> "true"
    */
    public String visit(TrueLiteral n, Symbol thisScope) throws Exception {
        return Symbol.BOOL;
    }

    /**
     * f0 -> "false"
    */
    public String visit(FalseLiteral n, Symbol thisScope) throws Exception {
        return Symbol.BOOL;
    }

    /**
    * f0 -> "this"
    */
    public String visit(ThisExpression n, Symbol thisScope) throws Exception {
        // this is methodClass type
        SymbolMethod thisMethod = (SymbolMethod)thisScope;
        return "Class "+thisMethod.methodClass.type;
    }

    /**
     * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public String visit(ArrayAllocationExpression n, Symbol thisScope) throws Exception {
        String type = n.f3.accept(this, thisScope);
        if (!type.equals(Symbol.INT))
            throw new Exception("Expression must be Integer type and not "+type+" in Array Allocation in "+thisScope);
        return Symbol.ARR;
    }

    /**
     * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    public String visit(AllocationExpression n, Symbol thisScope) throws Exception {
        String type = n.f1.accept(this, thisScope);
        // type must be class type
        SymbolClass thisClass = (SymbolClass) table.get(type);
        if (thisClass == null)
            throw new Exception("Wrong type "+type+" at Allocation Expression in "+thisScope);
        return "Class "+thisClass.type;
    }

    /**
     * f0 -> "!"
    * f1 -> PrimaryExpression()
    */
    public String visit(NotExpression n, Symbol thisScope) throws Exception {
        String type = n.f1.accept(this, thisScope);
        if (!type.equals(Symbol.BOOL))
            throw new Exception("Expression must be boolean and not "+type+" type in Not Expression in "+thisScope);
        return Symbol.BOOL;
    }

    /**
     * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    public String visit(BracketExpression n, Symbol thisScope) throws Exception {
        return "Bracket "+n.f1.accept(this, thisScope);
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
        String name = n.f2.accept(this, thisScope);

        // get symbol method
        SymbolClass thisClass = (SymbolClass)thisScope;
        SymbolMethod thisMethod = (SymbolMethod)thisClass.getMethod_r(name);
        if (thisMethod == null)
            throw new Exception("Method "+name+" not found in Class "+thisScope.name);

        // check statments
        if (n.f8.present())
            n.f8.accept(this, thisMethod);

        // check for method return type
        String returnMethodType = n.f10.accept(this, thisMethod);
        if (!thisMethod.type.equals(returnMethodType))
            throw new Exception("Wrong return type "+returnMethodType+" while Method "+thisMethod.name+"() returns "+thisMethod.type+" in Class "+thisScope.name);


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
        SymbolMethod mainMethod = (SymbolMethod) mainClass.getMethod("main");
           
        // check statments
        if (n.f15.present())
            n.f15.accept(this, mainMethod);

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
           
        // check methods
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

        // ckeck methods
        if (n.f6.present())
            n.f6.accept(this, thisClass);

        return null;
     }


}