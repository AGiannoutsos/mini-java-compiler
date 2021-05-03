package src;

import java.util.Map;

public class SymbolClass extends Symbol{
    SymbolClass parentClass;
    SymbolTable methods;
    SymbolTable variables;
    int variablesOffset;
    int methodsOffset;
    int totalVariablesOffset;
    int totalMethodsOffset;

    public SymbolClass(String name, SymbolClass parentClass, int offset){
        super("class", name, offset);
        this.parentClass = parentClass;
        this.methods = new SymbolTable();
        this.variables = new SymbolTable();
        this.variablesOffset = 0;
        this.methodsOffset = 0;
        this.totalVariablesOffset = this.parentClass.variablesOffset;
        this.totalMethodsOffset = this.parentClass.variablesOffset;
    }

    public SymbolClass(String name, int offset){
        super("class", name, offset);
        this.parentClass = null;
        this.methods = new SymbolTable();
        this.variables = new SymbolTable();
        this.variablesOffset = 0;
        this.methodsOffset = 0;
        this.totalVariablesOffset = 0;
        this.totalMethodsOffset = 0;
    }

    @Override
    public String toString() {
        String string;
        if (this.parentClass == null){
            string = "Class " + this.name + "\n";
            for (Map.Entry<String, Symbol> entry : this.variables.getSorted()) 
                string += this.name+"." + entry.getValue().toString();
            for (Map.Entry<String, Symbol> entry : this.methods.getSorted()) 
                if (!(entry.getValue().overrided))
                    string += this.name+"." + entry.getValue().toString();
        }
        else{
            string = "Class " + this.name + " extends " + this.parentClass.name + "\n";
            // for (Map.Entry<String, Symbol> entry : this.parentClass.variables.getSorted())
            //     string += this.parentClass.name+"." + entry.getValue().name + " : "+entry.getValue().getOffset()+"\n";
            string += parentClass.toString();
            for (Map.Entry<String, Symbol> entry : this.variables.getSorted())
                string += this.name+"." + entry.getValue().toString();
            for (Map.Entry<String, Symbol> entry : this.methods.getSorted())
                if (!(entry.getValue().overrided))
                    string += this.name+"." + entry.getValue().toString();
        }
        return string;
    }

    @Override
    public Symbol putVariable(String key, Symbol variable){
        SymbolVariable var = (SymbolVariable) variable;
        int offset = 0;
        if (var.type.equals(Symbol.INT)){
            offset = 4;
        } else if (var.type.equals(Symbol.BOOL)){
            offset = 1;
        } else {
            offset = 8;
        }

        this.totalVariablesOffset = this.variablesOffset;
        if (parentClass!=null)
        this.totalVariablesOffset = this.variablesOffset + parentClass.variablesOffset;
        Symbol s = variables.put(key, new SymbolVariable(var.type, var.name, this.totalVariablesOffset));
        this.variablesOffset += offset;
        return s;
    }

    @Override
    public Symbol putMethod(String key, Symbol method){
        SymbolMethod meth = (SymbolMethod) method;
        int offset = 8;
        // if overrided
        if (meth.overrided)
            offset = 0;

        this.totalMethodsOffset = this.methodsOffset;
        if (parentClass!=null)
        this.totalMethodsOffset = this.methodsOffset + parentClass.methodsOffset;
        Symbol s = methods.put(key, new SymbolMethod(meth.type, meth.name, this.totalMethodsOffset, meth.overrided));
        this.methodsOffset += offset;
        return s;
    }


}