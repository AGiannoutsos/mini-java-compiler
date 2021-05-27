import syntaxtree.*;
import visitor.*;
import src.*;

// import java.util.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws Exception {
        if(args.length == 0){
            System.err.println("Usage: java Main <inputFile1> <inputFile3> <inputFile3> ...");
            System.exit(1);
        }


        FileInputStream fis = null;

        for (String arg : args){

            try{
                fis = new FileInputStream(arg);
                MiniJavaParser parser = new MiniJavaParser(fis);
                Goal root = parser.Goal();
                System.err.println("Program ("+ arg +") parsed successfully.");
    
                // Symbol table init
                SymbolTable table = new SymbolTable(arg);
                
                // Check Class Declarations
                ClassDeclarationVisitor classDeclarationVisitor = new ClassDeclarationVisitor(table);
                root.accept(classDeclarationVisitor, null);
    
                // Fill Class Methods and Fields
                FillVisitor fillVisitor = new FillVisitor(table);
                root.accept(fillVisitor, null);
                
                // Type check
                CheckTypeVisitor checkTypeVisitor = new CheckTypeVisitor(table);
                root.accept(checkTypeVisitor, null);
                
                // Print offsets
                System.out.println(table);

                // LLVM visitor generates LLVM IR
                LLVMVisitor llvmVisitor = new LLVMVisitor(table);
                root.accept(llvmVisitor, null);

    
            }
            catch(ParseException ex){
                System.err.println(ex.getMessage());
            }
            catch(FileNotFoundException ex){
                System.err.println(ex.getMessage());
            }
            catch(Exception ex){
                System.err.println(ex.getMessage());
                System.err.println(ex.getStackTrace()[0].getLineNumber());
            }
            finally{
                try{
                    if(fis != null) fis.close();
                }
                catch(IOException ex){
                    System.err.println(ex.getMessage());
                }
            }
        }
    }
}



