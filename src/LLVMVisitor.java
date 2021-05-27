package src;

import syntaxtree.*;
import visitor.*;
import java.io.PrintWriter;
import java.util.Map;

public class LLVMVisitor extends GJDepthFirst<String, Symbol> {

    SymbolTable table;
    int classOffsset;
    PrintWriter out;
    int uniqueRegiserCounter;
    int uniqueLabelCounter;

    static String LLVM_INT  = "i32";
    static String LLVM_BOOL = "i1";
    static String LLVM_ARR  = "i32*";
    static String LLVM_PTR  = "i8*";

    public LLVMVisitor(SymbolTable table) throws Exception {
        this.table = table;
        this.classOffsset = 0;
        this.uniqueRegiserCounter = 0;
        this.uniqueLabelCounter = 0;

        // llvm outputfile
        String filename = table.name;
        filename = filename.substring(0, filename.lastIndexOf(".java"));
        filename = filename+".ll";
        System.out.println(filename);
        this.out = new PrintWriter(filename);

        // create v_table
        VtableGenerator();

        // helper methods
        String helper_methods = 
        "declare i8* @calloc(i32, i32)\n"+
        "declare i32 @printf(i8*, ...)\n"+
        "declare void @exit(i32)\n\n"+
        
        "@_cint = constant [4 x i8] c\"%d\\0a\\00\"\n"+
        "@_cOOB = constant [15 x i8] c\"Out of bounds\\0a\\00\"\n"+
        "define void @print_int(i32 %i) {\n"+
            "\t%_str = bitcast [4 x i8]* @_cint to i8*\n"+
            "\tcall i32 (i8*, ...) @printf(i8* %_str, i32 %i)\n"+
            "\tret void\n"+
        "}\n\n"+
        
        "define void @throw_oob() {\n"+
            "\t%_str = bitcast [15 x i8]* @_cOOB to i8*\n"+
            "\tcall i32 (i8*, ...) @printf(i8* %_str)\n"+
            "\tcall void @exit(i32 1)\n"+
            "\tret void\n"+
        "}\n\n";
        emit(helper_methods);
        // out.close();
    }

    static public String java2LLVMtype(String javaType){
        if (javaType.equals("int"))
            return LLVM_INT;
        else if (javaType.equals("boolean"))
            return LLVM_BOOL;
        else if (javaType.equals("int[]"))
            return LLVM_ARR;
        else
            return LLVM_PTR;
    }

    void STORE(String type, String item, String to) throws Exception{
        emit("\tstore "+type+" "+item+", "+type+"* "+to+"\n");
    }

    void LOAD(String type, String from, String to) throws Exception{
        emit("\t"+to+" = load "+type+", "+type+"* "+from+"\n");
    }

    void ADD(String type, String to, String a, String b) throws Exception {
        emit("\t"+to+" = add "+type+" "+a+", "+b+"\n");
    }

    void XOR(String type, String to, String a, String b) throws Exception {
        emit("\t"+to+" = xor "+type+" "+a+", "+b+"\n");
    }

    void SUB(String type, String to, String a, String b) throws Exception {
        emit("\t"+to+" = sub "+type+" "+a+", "+b+"\n");
    }

    void MUL(String type, String to, String a, String b) throws Exception {
        emit("\t"+to+" = mul "+type+" "+a+", "+b+"\n");
    }

    void AND(String type, String to, String a, String b) throws Exception {
        emit("\t"+to+" = and "+type+" "+a+", "+b+"\n");
    }

    void BR(String case_, String if_, String else_) throws Exception {
        emit("\tbr i1 "+case_+", label %"+if_+", label %"+else_+"\n");
    }

    void GOTO(String label) throws Exception {
        emit("\tbr label %"+label+"\n");
    }

    void CMP(String to, String a, String b) throws Exception {
        emit("\t"+to+" = icmp slt i32 "+a+", "+b+"\n");
    }

    void PRINT(String expression) throws Exception {
        emit("\tcall void (i32) @print_int(i32 "+expression+")\n");
    }

    void THROWO_OOB() throws Exception {
        emit("\tcall void @throw_oob()\n");
    }

    void BITCAST(String from_type, String from_address, String to_type, String to_address) throws Exception {
        emit("\t"+to_address+" = bitcast "+from_type+" "+from_address+" to "+to_type+"* \n");
    }

    void GETELEMTHIS(String to, int offset) throws Exception {
        emit("\t"+to+" = getelementptr i8, i8* %this, i32 "+offset+"\n");
    }

    void GETELEMARR(String from, String to, String index) throws Exception {
        emit("\t"+to+" = getelementptr i32, i32* "+from+", i32 "+index+"\n");
    }

    void CALLOC(String to, String size, String elements) throws Exception {
        emit("\t"+to+" = call i8* @calloc(i32 "+size+", i32 "+elements+")\n");
    }

    String currentRegister() {
        return "%_"+this.uniqueRegiserCounter;
    }

    String getRegister() {
        this.uniqueRegiserCounter++;
        return currentRegister();
    }

    void resetRegister() {
        this.uniqueRegiserCounter = 0;
    }

    String currentLabel() {
        return "if"+this.uniqueLabelCounter;
    }

    String getLabel() {
        this.uniqueLabelCounter++;
        return currentLabel();
    }

    void resetLabel() {
        this.uniqueLabelCounter = 0;
    }

    void emitLabel() throws Exception {
        emit(currentLabel()+":\n");
    }

    void emitLabel(String label) throws Exception {
        emit(label+":\n");
    }

    String argsJava2LLVMtype(String argumentsString){
        String llmvArgs = "(i8*";
        String[] argumentsStrings = argumentsString.split(", ");
        if (argumentsStrings[0].equals("")){ //no arguments
            return llmvArgs += ")*";
        }
        for(String argument : argumentsStrings){
            llmvArgs += ", "+java2LLVMtype(argument);
        }
        if(llmvArgs.endsWith(", ")) 
            llmvArgs = llmvArgs.substring(0, llmvArgs.length() - 2);
        return llmvArgs += ")*";
    }

    void emit(String buffer) throws Exception {
        this.out.print(buffer);
    }

    void VtableGenerator() throws Exception{
        
        SymbolClass thisClass        = null;
        SymbolTable thisClassMethods = null;
        String className             = null;
        SymbolMethod thisMethod     = null;
        int methodsNum              = 0;
        int methodNum               = 0;
        String methodName           = null;
        String llvmArgumentsTypes   = null;
        for (Map.Entry<String, Symbol> classEntry : this.table.getSorted()) {
            System.out.println(classEntry.getValue().name);
            // System.out.println(thisClassMethods.size());
            
            thisClass        = (SymbolClass) classEntry.getValue();
            className        = thisClass.name;
            thisClassMethods = thisClass.methods;
            methodsNum       = thisClassMethods.size();
            emit("@."+className+"_table = ");
            if (thisClass.getMethod("main") != null && methodsNum == 1){ //main method
                emit("global [0 x i8*] [");
            } else {
                emit("global ["+methodsNum+" x i8*] [");
                methodNum = 0;
                for (Map.Entry<String, Symbol> methodEntry : thisClassMethods.getSorted()) {
                    thisMethod = (SymbolMethod) methodEntry.getValue();
                    methodName = thisMethod.name;
                    llvmArgumentsTypes = argsJava2LLVMtype(thisMethod.getStringArguments());
                    emit("i8* bitcast (");
                    emit(java2LLVMtype(thisMethod.type)+" ");
                    emit(llvmArgumentsTypes+" ");
                    emit("@"+className+"."+methodName+" to i8*)");
                    methodNum++;
                    if (methodNum != methodsNum)
                        emit(", ");
                }   
            }
            emit("]\n");
        }
        emit("\n\n");
    }

    String[] getTypeRegister(String identifier, SymbolMethod thisMethod, boolean ptr) throws Exception {
        String[] typeRegister = new String[2];
        // search for id in local method variables
        SymbolVariable thisVariable = null; 
        thisVariable = (SymbolVariable) thisMethod.getVariable(identifier);
        if (thisVariable != null){
            if(!ptr){ //get value and not pointer, load
                LOAD(java2LLVMtype(thisVariable.type), "%"+thisVariable.name, getRegister());
                typeRegister[0] = java2LLVMtype(thisVariable.type);
                typeRegister[1] = currentRegister();
            } else{
                typeRegister[0] = java2LLVMtype(thisVariable.type);
                typeRegister[1] = "%"+thisVariable.name;
            }
            return typeRegister;
        } 
        // else the variable is laceted in the class scope
        thisVariable = (SymbolVariable) thisMethod.getVariable_r(identifier);
        if (thisVariable != null){
            // %_1 = getelementptr i8, i8* %this, i32 24
            GETELEMTHIS(getRegister(), thisVariable.offset);
	        // %_2 = bitcast i8* %_1 to i32*
            BITCAST(LLVM_PTR, currentRegister(), java2LLVMtype(thisVariable.type), getRegister());
            if(!ptr){ //get value and not pointer, load
                LOAD(java2LLVMtype(thisVariable.type), currentRegister(), getRegister());
            }
            typeRegister[0] = java2LLVMtype(thisVariable.type);
            typeRegister[1] = currentRegister();
            return typeRegister;
        }
        return null;
    }

    // check fir index and return array register or its index
    String getElemArrSafe(String arrayRegister, String indexRegister, boolean ptr) throws Exception {

        // get array size
        GETELEMARR(arrayRegister, getRegister(), "0");
        LOAD(LLVM_INT, currentRegister(), getRegister());
        String sizeRegister = currentRegister();
        
        // chech it
        String label1 = "Ar"+getLabel();
        String label2 = "Ar"+getLabel();
        CMP(getRegister(), indexRegister, sizeRegister);
        String upperBound = currentRegister();
        CMP(getRegister(), "-1", indexRegister);
        String lowerBound = currentRegister();
        AND(LLVM_BOOL, getRegister(), lowerBound, upperBound);
        
        BR(currentRegister(), label2, label1);
        emitLabel(label1);
        // bad index
        THROWO_OOB();
        GOTO(label2);
        emitLabel(label2);
        
        // add 1 to index to much fot the size
        ADD(LLVM_INT, getRegister(), indexRegister, "1");
        indexRegister = currentRegister();
        // get array element
        GETELEMARR(arrayRegister, getRegister(), indexRegister);
        
        // load if value than pointer is needed
        if (!ptr)
            LOAD(LLVM_INT, currentRegister(), getRegister());

        return currentRegister();
    }

    public String visit(Goal n, Symbol argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        this.out.close();
        return null;
     }


    @Override
    public String visit(MainClass n, Symbol thisClass) throws Exception {

        String classname = n.f1.accept(this, null);

        SymbolClass mainClass = (SymbolClass)table.get(classname);
        emit("define i32 @main() {\n");

        SymbolMethod thisMethod = (SymbolMethod)mainClass.getMethod("main");
        // allocate for method variables
        SymbolVariable thisVariable = null;
        for (Map.Entry<String, Symbol> variableEntry : thisMethod.variables.getSorted()) {
            thisVariable = (SymbolVariable) variableEntry.getValue();
            // alloca
            emit("\t%"+thisVariable.name+" = alloca "+java2LLVMtype(thisVariable.type)+"\n");
        }   

        // statments
        if (n.f15.present())
            n.f15.accept(this, thisMethod);

        emit("\tret i32 0\n}\n\n");
        return null;
    }


    @Override
    public String visit(ClassDeclaration n, Symbol thisScope) throws Exception {

        SymbolClass thisClass = (SymbolClass)table.get(n.f1.accept(this, null));
           
        // check methods
        if (n.f4.present())
            n.f4.accept(this, thisClass);

        return null;
    }
  

    @Override
    public String visit(ClassExtendsDeclaration n, Symbol thisScope) throws Exception {

        SymbolClass thisClass = (SymbolClass)table.get(n.f1.accept(this, null));

        // ckeck methods
        if (n.f6.present())
            n.f6.accept(this, thisClass);

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
        String name = n.f2.accept(this, thisScope);

        // get symbol method
        SymbolClass thisClass = (SymbolClass)thisScope;
        SymbolMethod thisMethod = (SymbolMethod)thisClass.getMethod(name);

        // define method and arguments
        emit("define "+java2LLVMtype(thisMethod.type)+" @"+thisClass.name+"."+thisMethod.name+"(i8* %this");
        String llvmTypeArgs = thisMethod.getLLVMStringArguments();
        if(llvmTypeArgs.equals(""))
            emit(") {\n");
        else
            emit(", "+llvmTypeArgs+") {\n");

        // allocate space for arguments and store them
        SymbolVariable thisArgument = null;
        for (Map.Entry<String, Symbol> argumentEntry : thisMethod.arguments.getSorted()) {
            thisArgument = (SymbolVariable) argumentEntry.getValue();
            // alloca
            emit("\t%"+thisArgument.name+" = alloca "+java2LLVMtype(thisArgument.type)+"\n");
            // store
            // emit("\tstore "+java2LLVMtype(thisArgument.type)+" %."+thisArgument.name+", "+java2LLVMtype(thisArgument.type)+"* %"+thisArgument.name+"\n");
            STORE(java2LLVMtype(thisArgument.type), "%."+thisArgument.name, "%"+thisArgument.name);
        }   

        // allocate for method variables
        SymbolVariable thisVariable = null;
        for (Map.Entry<String, Symbol> variableEntry : thisMethod.variables.getSorted()) {
            thisVariable = (SymbolVariable) variableEntry.getValue();
            // alloca
            emit("\t%"+thisVariable.name+" = alloca "+java2LLVMtype(thisVariable.type)+"\n");
        }   

        // statemants
        n.f8.accept(this, thisMethod);

        // return expression
        String expRegister = n.f10.accept(this, thisScope);
        emit("\tret "+java2LLVMtype(thisMethod.type)+" "+expRegister+"\n}\n\n");

        return null;
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

    @Override
    // get a register for the identifier
    public String visit(Identifier n, Symbol thisScope) throws Exception {
        return n.f0.toString();
    }


    /**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
    public String visit(AssignmentStatement n, Symbol thisScope) throws Exception {
        String identifier = n.f0.accept(this, thisScope);
        String[] typeRegister = getTypeRegister(identifier, (SymbolMethod)thisScope, true);
        String expRegister = n.f2.accept(this, thisScope);
        System.out.println("EDOOOOOOOOOOOOOOOOO");
        System.out.println(typeRegister[0]);
        System.out.println(typeRegister[1]);

        // store 
        STORE(typeRegister[0], expRegister, typeRegister[1]);
        
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
        String identifier    = n.f0.accept(this, thisScope);
        String[] idRegister  = getTypeRegister(identifier, (SymbolMethod)thisScope, false);
        String arrayRegister = idRegister[1];
        String indexRegister = n.f2.accept(this, thisScope);
        String expRegister   = n.f5.accept(this, thisScope);

        String arrayPointerRegister = getElemArrSafe(arrayRegister, indexRegister, true);
        STORE(LLVM_INT, expRegister, arrayPointerRegister);

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
        // check index expression
        String expressionRegister = n.f2.accept(this, thisScope);
        String label1 = getLabel();
        String label2 = getLabel();
        String label3 = getLabel();

        BR(expressionRegister, label1, label2);
        emitLabel(label1);
        n.f4.accept(this, thisScope);
        GOTO(label3);
        emitLabel(label2);
        n.f6.accept(this, thisScope);
        GOTO(label3);
        emitLabel(label3);
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
        // check index expression
        String label1 = "L"+getLabel();
        String label2 = "L"+getLabel();
        String label3 = "L"+getLabel();
        
        // first label then expression
        GOTO(label1);
        emitLabel(label1);
        String expressionRegister = n.f2.accept(this, thisScope);
        BR(expressionRegister, label2, label3);
        emitLabel(label2);
        n.f4.accept(this, thisScope);
        GOTO(label1);
        emitLabel(label3);
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
        String expressionRegister = n.f2.accept(this, thisScope);
        PRINT(expressionRegister);
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
        String expressionRegister1 = n.f0.accept(this, thisScope);
        String expressionRegister2 = n.f2.accept(this, thisScope);

        AND(LLVM_BOOL, getRegister(), expressionRegister1, expressionRegister2);
        return currentRegister();
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "<"
    * f2 -> PrimaryExpression()
    */
    public String visit(CompareExpression n, Symbol thisScope) throws Exception {
        String expressionRegister1 = n.f0.accept(this, thisScope);
        String expressionRegister2 = n.f2.accept(this, thisScope);

        CMP(getRegister(), expressionRegister1, expressionRegister2);
        return currentRegister();
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
    public String visit(PlusExpression n, Symbol thisScope) throws Exception {
        String expressionRegister1 = n.f0.accept(this, thisScope);
        String expressionRegister2 = n.f2.accept(this, thisScope);

        ADD(LLVM_INT, getRegister(), expressionRegister1, expressionRegister2);
        return currentRegister();
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
    public String visit(MinusExpression n, Symbol thisScope) throws Exception {
        String expressionRegister1 = n.f0.accept(this, thisScope);
        String expressionRegister2 = n.f2.accept(this, thisScope);

        SUB(LLVM_INT, getRegister(), expressionRegister1, expressionRegister2);
        return currentRegister();
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
    public String visit(TimesExpression n, Symbol thisScope) throws Exception {
        String expressionRegister1 = n.f0.accept(this, thisScope);
        String expressionRegister2 = n.f2.accept(this, thisScope);

        MUL(LLVM_INT, getRegister(), expressionRegister1, expressionRegister2);
        return currentRegister();
    }

    /**
     * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
    public String visit(ArrayLookup n, Symbol thisScope) throws Exception {
        String arrayRegister = n.f0.accept(this, thisScope);
        String indexRegister = n.f2.accept(this, thisScope);

        return getElemArrSafe(arrayRegister, indexRegister, false); 
    }


    // /**
    //  * f0 -> PrimaryExpression()
    // * f1 -> "."
    // * f2 -> "length"
    // */
    // public String visit(ArrayLength n, Symbol thisScope) throws Exception {
    //     String type1 = n.f0.accept(this, thisScope);

    //     if (type1.equals(Symbol.ARR))
    //         return Symbol.INT;
    //     else
    //         throw new Exception("Wrong type "+type1+" at Array Length Expression in "+thisScope);
    // }

    // /**
    // * f0 -> Expression()
    // * f1 -> ExpressionTail()
    // */
    // public String visit(ExpressionList n, Symbol thisScope) throws Exception {
    //     String argumentsTypes = n.f0.accept(this, thisScope);
    //     argumentStack.push(argumentsTypes);
    //     // get the rest parameters
    //     n.f1.accept(this, thisScope);
    //     // pop last from stack
    //     return argumentStack.pop();
    // }

    // /**
    //  * f0 -> ","
    // * f1 -> Expression()
    // */
    // public String visit(ExpressionTerm n, Symbol thisScope) throws Exception {
    //     String argumentsTypes = argumentStack.pop();
    //     argumentsTypes += ", " + n.f1.accept(this, thisScope);
    //     argumentStack.push(argumentsTypes);
    //     return argumentsTypes;
    // }


    // /**
    //  * f0 -> PrimaryExpression()
    // * f1 -> "."
    // * f2 -> Identifier()
    // * f3 -> "("
    // * f4 -> ( ExpressionList() )?
    // * f5 -> ")"
    // */
    // public String visit(MessageSend n, Symbol thisScope) throws Exception {

    //     // check if primary expression is class type
    //     String type = n.f0.accept(this, thisScope);
    //     SymbolClass thisClass = (SymbolClass) table.get(type);
    //     if (thisClass == null) 
    //         throw new Exception("Operator . unavailable on type: "+type+" in "+thisScope);
        
    //     // check if method is availble on that class
    //     String method = n.f2.accept(this, thisScope);
    //     SymbolMethod thisMethod = (SymbolMethod) thisClass.getMethod_r(method);
    //     if (thisMethod == null) 
    //         throw new Exception("Method "+method+"() is unavailable on type: "+type+" in "+thisScope);
        

    //     // check for arguments on method
    //     String argumentString = "";
    //     String[] argumentStrings;
    //     String declaredArgumentString = thisMethod.getStringArguments();
    //     String[] declaredArgumentStrings = thisMethod.getStringArguments().split(", ");
    //     if (n.f4.present()) {
    //         argumentString = n.f4.accept(this, thisScope);
    //         argumentStrings = n.f4.accept(this, thisScope).split(", ");
            

    //         // System.out.println(argumentString);
    //         // System.out.println(argumentString.length());
    //         // System.out.println(declaredArgumentString);
    //         // System.out.println(argumentString.length());
    //         // if (!declaredArgumentString.equals(argumentString))
    //         if(argumentStrings.length == declaredArgumentStrings.length && !declaredArgumentStrings[0].equals("")){
    //             int arg = 0;
    //             for (Map.Entry<String, Symbol> entry : thisMethod.arguments.getSorted()) {
    //                 if(!checkType(entry.getValue(), argumentStrings[arg], table))
    //                     throw new Exception("Argument types do not match at Method "+method+"() in "+thisScope);
    //                 arg++;
    //             }
    //         } else{
    //             throw new Exception("Argument count do not match at Method "+method+"() in "+thisScope);
    //         }
    //     } 
    //     else{
    //         if (!declaredArgumentStrings[0].equals(""))
    //             throw new Exception("Argument counts do not match at Method "+method+"() in "+thisScope);
    //     }
            
        

        
    //     return thisMethod.type;
    // }

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
        String expRegister = n.f0.accept(this, thisScope);
        int returnPrimaryExpressionType = n.f0.which;
		if (returnPrimaryExpressionType == 3) {
			// get identifier
            String[] typeRegister = getTypeRegister(expRegister, (SymbolMethod)thisScope, false);
            // System.out.println(typeRegister[0]);
            // System.out.println(typeRegister[1]);
            return typeRegister[1];
		}
		return expRegister;
    }

    /**
     * f0 -> <INTEGER_LITERAL>
    */
    public String visit(IntegerLiteral n, Symbol thisScope) throws Exception {
        String integer = n.f0.toString();
        ADD(LLVM_INT, getRegister(), "0", integer);
        return currentRegister();
    }

    /**
    * f0 -> "true"
    */
    public String visit(TrueLiteral n, Symbol thisScope) throws Exception {
        ADD(LLVM_BOOL, getRegister(), "0", "1");
        return currentRegister();
    }

    /**
     * f0 -> "false"
    */
    public String visit(FalseLiteral n, Symbol thisScope) throws Exception {
        ADD(LLVM_BOOL, getRegister(), "0", "0");
        return currentRegister();
    }

    // /**
    // * f0 -> "this"
    // */
    // public String visit(ThisExpression n, Symbol thisScope) throws Exception {
    //     // this is methodClass type
    //     SymbolMethod thisMethod = (SymbolMethod)thisScope;
    //     return "Class "+thisMethod.methodClass.type;
    // }

    /**
     * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
    public String visit(ArrayAllocationExpression n, Symbol thisScope) throws Exception {
        String expressionSizeRegister = n.f3.accept(this, thisScope);
        
        // check of expression of allocation is greater than 0
        String label1 = "Ar"+getLabel();
        String label2 = "Ar"+getLabel();
        CMP(getRegister(), expressionSizeRegister, "0");
        BR(currentRegister(), label1, label2);
        emitLabel(label1);
        // bad allocattion
        THROWO_OOB();
        GOTO(label2);
        emitLabel(label2);

        // add 1 for the size of the array
        ADD(LLVM_INT, getRegister(), expressionSizeRegister, "1");
        String sizeRegister = currentRegister();
        CALLOC(getRegister(), "4", sizeRegister); // allocate array
        BITCAST(LLVM_PTR, currentRegister(), LLVM_INT, getRegister());
        String arrayRegister = currentRegister();
        // set the size to the fisr element in the array
        GETELEMARR(arrayRegister, getRegister(), "0");
        STORE(LLVM_INT, expressionSizeRegister, currentRegister());

        return arrayRegister;
    }


    // /**
    //  * f0 -> "new"
    // * f1 -> Identifier()
    // * f2 -> "("
    // * f3 -> ")"
    // */
    // public String visit(AllocationExpression n, Symbol thisScope) throws Exception {
    //     String type = n.f1.accept(this, thisScope);
    //     // type must be class type
    //     SymbolClass thisClass = (SymbolClass) table.get(type);
    //     if (thisClass == null)
    //         throw new Exception("Wrong type "+type+" at Allocation Expression in "+thisScope);
    //     return "Class "+thisClass.type;
    // }

    /**
     * f0 -> "!"
    * f1 -> PrimaryExpression()
    */
    public String visit(NotExpression n, Symbol thisScope) throws Exception {
        String expressionRegister = n.f1.accept(this, thisScope);
        XOR(LLVM_BOOL, getRegister(), "1", expressionRegister);
        return currentRegister();
    }

    /**
     * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
    public String visit(BracketExpression n, Symbol thisScope) throws Exception {
        return n.f1.accept(this, thisScope);
    }


}