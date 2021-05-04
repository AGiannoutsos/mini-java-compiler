package src;

import syntaxtree.*;
import visitor.*;
import java.util.Map;


public class CheckTypeVisitor extends GJDepthFirst<String, Symbol> {

    SymbolTable table;
    int classOffsset;


    public CheckTypeVisitor(SymbolTable table) throws Exception {
        this.table = table;
        this.classOffsset = 0;
    }

    public boolean checkType(Symbol symbol, String type){
        if (type.equals(Symbol.INT) || type.equals(Symbol.BOOL) || type.equals(Symbol.ARR)){
            return symbol.type.equals(type);
        }
        // check for inheritance type, symbol must be a class
        // get the class type of variable
        SymbolClass currentClass = (SymbolClass)table.get(type);
        if (currentClass.type.equals(symbol.type))
            return true;
        else 
            if (currentClass.parentClass != null)
                return currentClass.parentClass.type.equals(symbol.type);
            else
                return false;
    }


    @Override
    public String visit(Identifier n, Symbol currentScope) throws Exception {
        // in type checking identifiers = types
        // String identifier = n.f0.toString();
        // SymbolVariable currentVariable = (SymbolVariable)currentScope.getVariable_r(identifier);
        // return currentVariable.type;
        ////////////////////////////////////////
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    * f2 -> ";"
    */
    @Override
    public String visit(VarDeclaration n, Symbol currentScope) throws Exception {
        String type = n.f0.accept(this, currentScope);

        // check scope of variable name
        String name = n.f1.accept(this, currentScope);

        if ( currentScope.getVariable(name) != null)
           throw new Exception("Variable "+type+" "+name+" already declared in "+currentScope);
        
        currentScope.putVariable(name, new SymbolVariable(type, name, 0));

        return null;
    }

    /**
    * f0 -> ArrayType()
    *       | BooleanType()
    *       | IntegerType()
    *       | Identifier()
    */
    @Override
    public String visit(Type n, Symbol currentScope) throws Exception {
        String type = n.f0.accept(this, currentScope);
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
    public String visit(ArrayType n, Symbol currentScope) throws Exception {
        return "int[]";
    }

    /**
     * f0 -> "boolean"
    */
    @Override
    public String visit(BooleanType n, Symbol currentScope) throws Exception {
        return "boolean";
    }

    /**
     * f0 -> "int"
    */
    @Override
    public String visit(IntegerType n, Symbol currentScope) throws Exception {
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
    public String visit(Statement n, Symbol currentScope) throws Exception {
        return n.f0.accept(this, currentScope);
    }

    /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
   public String visit(AssignmentStatement n, Symbol currentScope) throws Exception {
        String name = n.f0.accept(this, currentScope);
        SymbolVariable currentVariable = (SymbolVariable) currentScope.getVariable_r(name);
        if (currentVariable == null)
            throw new Exception("Unknown identifier at assignment in "+currentScope);

        // get expression type
        String expressionType = n.f2.accept(this, currentScope);

        // check type
        if (!checkType(currentVariable, expressionType))
            throw new Exception("Wrong type assignment in "+currentScope);
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
    public String visit(Expression n, Symbol currentScope) throws Exception {            
            return n.f0.accept(this, currentScope);
    }

    /**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
    public String visit(AndExpression n, Symbol currentScope) throws Exception {

        n.f0.accept(this, currentScope);
        n.f1.accept(this, currentScope);
        n.f2.accept(this, currentScope);
        return null;
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    public String visit(CompareExpression n, Symbol currentScope) throws Exception {

        n.f0.accept(this, currentScope);
        n.f1.accept(this, currentScope);
        n.f2.accept(this, currentScope);
        return null;
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    public String visit(PlusExpression n, Symbol currentScope) throws Exception {

        n.f0.accept(this, currentScope);
        n.f1.accept(this, currentScope);
        n.f2.accept(this, currentScope);
        return null;
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    public String visit(MinusExpression n, Symbol currentScope) throws Exception {

        n.f0.accept(this, currentScope);
        n.f1.accept(this, currentScope);
        n.f2.accept(this, currentScope);
        return null;
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    public String visit(TimesExpression n, Symbol currentScope) throws Exception {
    
        n.f0.accept(this, currentScope);
        n.f1.accept(this, currentScope);
        n.f2.accept(this, currentScope);
        return null;
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    public String visit(ArrayLookup n, Symbol currentScope) throws Exception {
    
        n.f0.accept(this, currentScope);
        n.f1.accept(this, currentScope);
        n.f2.accept(this, currentScope);
        n.f3.accept(this, currentScope);
        return null;
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
    public String visit(ArrayLength n, Symbol currentScope) throws Exception {
    
        n.f0.accept(this, currentScope);
        n.f1.accept(this, currentScope);
        n.f2.accept(this, currentScope);
        return null;
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
    public String visit(MessageSend n, Symbol currentScope) throws Exception {
    
        n.f0.accept(this, currentScope);
        n.f1.accept(this, currentScope);
        n.f2.accept(this, currentScope);
        n.f3.accept(this, currentScope);
        n.f4.accept(this, currentScope);
        n.f5.accept(this, currentScope);
        return null;
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
    public String visit(PrimaryExpression n, Symbol currentScope) throws Exception {
        // check for type of primary expression
        // if type is not known then type is from identifier
        // so we need to look up for it
        String primaryExpressionType = n.f0.accept(this, currentScope);
        return ////////////////////////////////////////
    }

    /**
     * f0 -> <INTEGER_LITERAL>
    */
    public String visit(IntegerLiteral n, Symbol currentScope) throws Exception {
        return Symbol.INT;
    }

    /**
    * f0 -> "true"
    */
    public String visit(TrueLiteral n, Symbol currentScope) throws Exception {
        return Symbol.BOOL;
    }

    /**
     * f0 -> "false"
    */
    public String visit(FalseLiteral n, Symbol currentScope) throws Exception {
        return Symbol.BOOL;
    }

    /**
    * f0 -> "this"
    */
    public String visit(ThisExpression n, Symbol currentScope) throws Exception {
        // this is methodClass type
        SymbolMethod currentMethod = (SymbolMethod)currentScope;
        return currentMethod.methodClass.type;
    }

    /**
     * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public String visit(ArrayAllocationExpression n, Symbol currentScope) throws Exception {
        return Symbol.ARR;
    }

    /**
     * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
    public String visit(AllocationExpression n, Symbol currentScope) throws Exception {
        String _ret=null;
        n.f0.accept(this, currentScope);
        n.f1.accept(this, currentScope);
        n.f2.accept(this, currentScope);
        n.f3.accept(this, currentScope);
        return _ret;
    }

    /**
     * f0 -> "!"
    * f1 -> PrimaryExpression()
    */
    public String visit(NotExpression n, Symbol currentScope) throws Exception {
        String _ret=null;
        n.f0.accept(this, currentScope);
        n.f1.accept(this, currentScope);
        return _ret;
    }

    /**
     * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    public String visit(BracketExpression n, Symbol currentScope) throws Exception {
        String _ret=null;
        n.f0.accept(this, currentScope);
        n.f1.accept(this, currentScope);
        n.f2.accept(this, currentScope);
        return _ret;
    }

    /**
    * f0 -> Type()
    * f1 -> Identifier()
    */
    // @Override
    // public String visit(FormalParameter n, Symbol currentScope) throws Exception {
	// 	String type = n.f0.accept(this, currentScope);
	// 	String name = n.f1.accept(this, currentScope);
        
    //     if (currentScope.getArgument(name) != null)
    //         throw new Exception("Argument "+name+" already declared in "+currentScope);
        
	// 	SymbolVariable argument = new SymbolVariable(type, name);
    //     currentScope.putArgument(name, argument);

	// 	return null;
	// }

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
    public String visit(MethodDeclaration n, Symbol currentScope) throws Exception {
        String type = n.f1.accept(this, currentScope);
        String name = n.f2.accept(this, currentScope);

        // get symbol method
        SymbolClass currentClass = (SymbolClass)currentScope;
        SymbolMethod currentMethod = (SymbolMethod)currentClass.getMethod_r(name);
        if (currentMethod == null)
            throw new Exception("Method "+name+" not found in "+currentScope);
            

        // // fill parameters
        // if (n.f4.present())
        //     n.f4.accept(this, currentMethod);
            
        // // fill variable declarations
        // if (n.f7.present())
        //     n.f7.accept(this, currentMethod);
        currentMethod.print();

        // check statments
        if (n.f8.present())
            n.f8.accept(this, currentMethod);


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
    public String visit(MainClass n, Symbol currentScope) throws Exception {

        // SymbolClass mainClass = (SymbolClass) table.get(n.f1.accept(this, null));
        // SymbolMethod mainMethod = (SymbolMethod) mainClass.getMethod("main");

        // // fill main method
        // if ( mainClass.getMethod("main") != null )
        //     throw new Exception("main method is already declared");

        // SymbolMethod mainMethod = new SymbolMethod("void", "main", false);
        // mainClass.putMethod("main", mainMethod);

        // // fill arguments
        // String argument = n.f11.accept(this, null);
        // if ( mainMethod.getArgument(argument) != null )
        //     throw new Exception("main argument is already declared");
        
        // SymbolVariable mainArgument = new SymbolVariable("void", argument);
        // mainMethod.putArgument(argument, mainArgument);
        
        // // fill variable declarations
        // if (n.f14.present())
        //     n.f14.accept(this, mainMethod);
        // mainMethod.print();

        // check main statments
        // if (n.f15.present())
        //     n.f15.accept(this, mainMethod);

        

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
    public String visit(ClassDeclaration n, Symbol currentScope) throws Exception {

        SymbolClass currentClass = (SymbolClass)table.get(n.f1.accept(this, null));

        // // fill class variable declarations
        // if (n.f3.present())
        //     n.f3.accept(this, currentClass);
            
        // check methods
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
    public String visit(ClassExtendsDeclaration n, Symbol currentScope) throws Exception {

        SymbolClass currentClass = (SymbolClass)table.get(n.f1.accept(this, null));

        // // fill class variable declarations
        // if (n.f5.present())
        //     n.f5.accept(this, currentClass);

        // ckeck methods
        if (n.f6.present())
            n.f6.accept(this, currentClass);

        return null;
     }


}