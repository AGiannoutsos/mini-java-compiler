package src;

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



}