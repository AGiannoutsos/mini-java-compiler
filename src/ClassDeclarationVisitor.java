package src;

import syntaxtree.*;
import visitor.*;


public class ClassDeclarationVisitor extends GJDepthFirst<String, String> {

    SymbolTable table;
    int classOffsset;

    public ClassDeclarationVisitor(SymbolTable table) throws Exception {
        this.table = table;
        this.classOffsset = 0;
    }


    @Override
    public String visit(Identifier n, String argu) throws Exception {
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
    public String visit(MainClass n, String argu) throws Exception {

        String classname = n.f1.accept(this, null);
        SymbolClass mainClass = new SymbolClass(classname, this.classOffsset);
        if ( table.put(classname, mainClass) != null )
            throw new Exception(mainClass.name+" is already declared");
        this.classOffsset++;
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
    public String visit(ClassDeclaration n, String argu) throws Exception {

        String classname = n.f1.accept(this, null);
        SymbolClass classDeclaration = new SymbolClass(classname, this.classOffsset);
        if ( table.put(classname, classDeclaration) != null )
            throw new Exception(classDeclaration.name+" is already declared");
        this.classOffsset++;
        
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
    public String visit(ClassExtendsDeclaration n, String argu) throws Exception {

        String classname = n.f1.accept(this, null);
        String parentClassname = n.f3.accept(this, argu);
        
        // cjeck if parent class exists
        SymbolClass parentClass = (SymbolClass) table.get(parentClassname);
        if ( parentClass == null )
            throw new Exception("Parent Class "+parentClassname+" in Class "+classname+" is not declared");
        
        SymbolClass classExtendsDeclaration = new SymbolClass(classname, parentClass, this.classOffsset);

        // check if class already declared
        if ( table.put(classname, classExtendsDeclaration) != null )
            throw new Exception(classExtendsDeclaration.name+" is already declared");
        this.classOffsset++;
        
        return null;
     }
  





}