package src;

import java.util.*;
// import java.util.Comparator;

public class VirtualTable extends TreeMap<Integer, SymbolMethod>{
    String name;

    public VirtualTable(String name){        
        this.name = name;
    }
    public VirtualTable(){        
        this.name = null;
    }


    public VirtualTable fillTable(VirtualTable otherVirtualTable){
        for (SymbolMethod otherMethod : otherVirtualTable.values()) {
            this.put(otherMethod.offset, otherMethod);
        }  
        return this;
    }

    @Override
    public String toString(){
        String methodName = "";
        String className = "";
        String llvmArgumentsTypes = "";
        String vtableString = "";
        for (SymbolMethod thisMethod : this.values()) {
            methodName = thisMethod.name;
            className  = thisMethod.methodClass.name;
            llvmArgumentsTypes = LLVMVisitor.argsJava2LLVMtype(thisMethod.getStringArguments());
            vtableString += "i8* bitcast (";
            vtableString += LLVMVisitor.java2LLVMtype(thisMethod.type)+" ";
            vtableString += llvmArgumentsTypes+" ";
            vtableString += "@"+className+"."+methodName+" to i8*), ";
        }  
        if(vtableString.endsWith(", ")) 
            vtableString = vtableString.substring(0, vtableString.length() - 2);
        return vtableString;
    }

}