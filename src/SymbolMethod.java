package src;

import java.util.Map;

public class SymbolMethod extends Symbol{

    SymbolTable arguments;
    SymbolTable variables;

    public SymbolMethod(String type, String name, boolean overrided){
        super(type, name, overrided);
        this.arguments = new SymbolTable();
        this.variables = new SymbolTable();
    }

    public SymbolMethod(String type, String name, int offset, boolean overrided){
        super(type, name, offset, overrided);
        this.arguments = new SymbolTable();
        this.variables = new SymbolTable();
    }

    @Override
    public Symbol putArgument(String key, Symbol argument) {
        return this.arguments.put(key, argument);
    }

    @Override
    public Symbol putVariable(String key, Symbol variable) {
        return this.variables.put(key, variable);
    }

    @Override
    public Symbol getArgument(String key) {
        return this.arguments.get(key);
    }

    @Override
    public Symbol getVariable(String key) {

        SymbolVariable arg = (SymbolVariable)this.arguments.get(key);
        SymbolVariable var = (SymbolVariable)this.variables.get(key);
        if (arg != null)
            return arg;
        if (var != null)
            return var;

        return null;
    }

    public String print(){
        String string = "";
        string += this.name+"(";
        for (Map.Entry<String, Symbol> entry : this.arguments.getSorted()) 
                string += entry.getValue().toString() + ", ";
        string += ")\n";
        for (Map.Entry<String, Symbol> entry : this.variables.getSorted()) 
                string += entry.getValue().toString() + " ";
        string += "\n";
        System.out.println(string);
        return string;
    }

}