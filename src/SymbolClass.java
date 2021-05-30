package src;

import java.util.Map;

public class SymbolClass extends Symbol{
    SymbolClass parentClass;
    SymbolTable methods;
    SymbolTable variables;
    int variablesOffset;
    int methodsOffset;
    boolean variablesOffsetChecked;
    boolean methodsOffsetChecked;
    boolean virtualTableChecked;

    // virtual table for llvm ir
    VirtualTable virtualTable;

    public SymbolClass(String name, SymbolClass parentClass, int offset){
        super(name, name, offset);
        this.parentClass = parentClass;
        this.methods = new SymbolTable();
        this.variables = new SymbolTable();
        this.variablesOffset = 0;
        this.methodsOffset = 0;
        this.variablesOffsetChecked = false;
        this.methodsOffsetChecked = false;
        this.virtualTableChecked = false;

        this.virtualTable = new VirtualTable(name);
    }

    public SymbolClass(String name, int offset){
        super(name, name, offset);
        this.parentClass = null;
        this.methods = new SymbolTable();
        this.variables = new SymbolTable();
        this.variablesOffset = 0;
        this.methodsOffset = 0;
        this.variablesOffsetChecked = false;
        this.methodsOffsetChecked = false;

        this.virtualTable = new VirtualTable(name);
    }

    void updateVirtualTable_r(int offset, SymbolMethod method){
        if (this.virtualTableChecked == false){
            if(this.parentClass!=null)
                this.virtualTable = parentClass.updateVirtualTable(this.virtualTable);
            this.virtualTableChecked = true;
        }
        this.virtualTable.put(offset, method);
    }

    VirtualTable updateVirtualTable(VirtualTable childClassVirtualTable){
        return childClassVirtualTable.fillTable(this.virtualTable);
    }

    @Override
    public String toString() {
        String string = "";
        string += "-----------Class " + this.name+"-----------";
        // if (this.parentClass == null){
            string += "\n---Variables---";
            for (Map.Entry<String, Symbol> entry : this.variables.getSorted()) 
                string += "\n"+this.name+"." + entry.getValue().toString();
            string += "\n---Methods---";
            for (Map.Entry<String, Symbol> entry : this.methods.getSorted()) 
                // if (!(entry.getValue().overrided))
                string += "\n"+this.name+"." + entry.getValue().toString();

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

    public int getOffset() {
        if (this.variablesOffsetChecked == false){
            if(this.parentClass!=null)
                this.variablesOffset += this.parentClass.variablesOffset;
            this.variablesOffsetChecked = true;
        }
        return this.variablesOffset;
    }

    @Override
    public Symbol putVariable(String key, Symbol variable){
        // check parent offset
        if (this.variablesOffsetChecked == false){
            if(this.parentClass!=null)
                this.variablesOffset += this.parentClass.variablesOffset;
            this.variablesOffsetChecked = true;
        }
        SymbolVariable var = (SymbolVariable) variable;
        int offset = 0;
        if (var.type.equals(Symbol.INT)){
            offset = 4;
        } else if (var.type.equals(Symbol.BOOL)){
            offset = 1;
        } else {
            offset = 8;
        }

        Symbol s = variables.put(key, new SymbolVariable(var.type, var.name, this.variablesOffset));
        this.variablesOffset += offset;
        return s;
    }

    @Override
    public Symbol putMethod(String key, Symbol method){
        // check parent offset
        if (this.methodsOffsetChecked == false){
            if(this.parentClass!=null)
                this.methodsOffset += this.parentClass.methodsOffset;
            this.methodsOffsetChecked = true;
        }
        SymbolMethod meth = (SymbolMethod) method;
        int offset = 8;
        // if overrided
        if (meth.overrided){
            offset = 0;
            // assign virtual offset of the v table
            // get overidden offset
            meth.offset = this.getMethod_r(meth.name).offset;
            Symbol s = methods.put(key, meth);
            updateVirtualTable_r(meth.offset, meth);
            this.methodsOffset += offset;
            return s;
        }
        

        meth.offset = this.methodsOffset;
        Symbol s = methods.put(key, meth);
        // put also in virtual table based on the offset
        updateVirtualTable_r(meth.offset, meth);
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