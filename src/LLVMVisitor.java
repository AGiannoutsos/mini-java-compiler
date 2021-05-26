package src;

import syntaxtree.*;
import visitor.*;
import java.io.PrintWriter;
import java.util.Map;

public class LLVMVisitor extends GJDepthFirst<String, Symbol> {

    SymbolTable table;
    int classOffsset;
    PrintWriter out;

    public LLVMVisitor(SymbolTable table) throws Exception {
        this.table = table;
        this.classOffsset = 0;

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
            return "i32";
        else if (javaType.equals("boolean"))
            return "i1";
        else if (javaType.equals("int[]"))
            return "i32*";
        else
            return "i8*";
    }

    String argsJava2LLVMtype(String argumentsString){
        String llmvArgs = "(i8*";
        String[] argumentsStrings = argumentsString.split(", ");
        if (argumentsStrings[0].equals("")){ //no arguments
            return llmvArgs += ")*";
        }
        for(String argument : argumentsStrings){
            llmvArgs += " "+java2LLVMtype(argument);
        }
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

    public String visit(Goal n, Symbol argu) throws Exception {
        n.f0.accept(this, argu);
        n.f1.accept(this, argu);
        n.f2.accept(this, argu);
        this.out.close();
        return null;
     }


    @Override
    public String visit(Identifier n, Symbol argu) throws Exception {
        return n.f0.toString();
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

        String classname = n.f1.accept(this, null);

        SymbolClass mainClass = (SymbolClass)table.get(classname);
        emit("define i32 @main() {\n");
        
        emit("\tret i32 0\n}\n\n");
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
            emit("\tstore "+java2LLVMtype(thisArgument.type)+" %."+thisArgument.name+", "+java2LLVMtype(thisArgument.type)+"* %"+thisArgument.name+"\n");
        }   

        // allocate for method variables
        SymbolVariable thisVariable = null;
        for (Map.Entry<String, Symbol> variableEntry : thisMethod.variables.getSorted()) {
            thisVariable = (SymbolVariable) variableEntry.getValue();
            // alloca
            emit("\t%"+thisVariable.name+" = alloca "+java2LLVMtype(thisVariable.type)+"\n");
        }   

        // statemants
        n.f8.accept(this, thisScope);

        // return expression
        

        emit("\tret "+java2LLVMtype(thisMethod.type)+" 0\n}\n\n");
        return null;
    }
  





}