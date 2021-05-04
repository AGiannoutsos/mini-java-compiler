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
        super(name, name, offset);
        this.parentClass = parentClass;
        this.methods = new SymbolTable();
        this.variables = new SymbolTable();
        this.variablesOffset = 0;
        this.methodsOffset = 0;
        this.totalVariablesOffset = this.parentClass.variablesOffset;
        this.totalMethodsOffset = this.parentClass.variablesOffset;
    }

    public SymbolClass(String name, int offset){
        super(name, name, offset);
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
        String string = "";
        string += "-----------Class " + this.name+"-----------";
        if (this.parentClass == null){
            string += "\n---Variables---";
            for (Map.Entry<String, Symbol> entry : this.variables.getSorted()) 
                string += "\n"+this.name+"." + entry.getValue().toString();
            string += "\n---Methods---";
            for (Map.Entry<String, Symbol> entry : this.methods.getSorted()) 
                if (!(entry.getValue().overrided))
                    string += "\n"+this.name+"." + entry.getValue().toString();
        }
        else{
            string += " extends ";
            // for (Map.Entry<String, Symbol> entry : this.parentClass.variables.getSorted())
            //     string += this.parentClass.name+"." + entry.getValue().name + " : "+entry.getValue().getOffset()+"\n";
            string += parentClass.toString();
            string += "\n---Variables---";
            for (Map.Entry<String, Symbol> entry : this.variables.getSorted())
                string += "\n"+this.name+"." + entry.getValue().toString();
            string += "\n---Methods---";
            for (Map.Entry<String, Symbol> entry : this.methods.getSorted())
                if (!(entry.getValue().overrided))
                    string += "\n"+this.name+"." + entry.getValue().toString();
        }
        // debug
        // string += this.debugPrint();
        return string+"\n";
    }

    public String debugPrint() {
        String string = "";
        if (parentClass!=null){
            for (Map.Entry<String, Symbol> entry : this.parentClass.methods.getSorted())
                string += this.name+"." +entry.getValue().toString();
        }
        for (Map.Entry<String, Symbol> entry : this.methods.getSorted())
            string += this.name+"." +entry.getValue().toString();
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
        meth.offset = this.totalMethodsOffset;
        // Symbol s = methods.put(key, new SymbolMethod(meth.type, meth.name, this.totalMethodsOffset, meth.overrided, meth.methodClass));
        Symbol s = methods.put(key, meth);
        this.methodsOffset += offset;
        return s;
    }

    @Override
    public Symbol getMethod(String key){
        return this.methods.get(key);
    }

    public SymbolMethod getMethodd(String key){
        return (SymbolMethod)this.methods.get(key);
    }

    @Override
    public Symbol getMethod_r(String key){
        Symbol meth =  this.getMethod(key);
        if (meth == null)
            if (parentClass != null)
                meth = this.parentClass.getMethod_r(key);
            else
                meth = null;
        return meth;
    }

    @Override
    public Symbol getVariable(String key){
        return this.variables.get(key);
    }

    @Override
    public Symbol getVariable_r(String key){
        SymbolVariable var = (SymbolVariable) this.getVariable(key);
        if (var == null)
            if (parentClass != null)
                var = (SymbolVariable) this.parentClass.getVariable_r(key);
            else
                var = null;
        return var;
    }


}