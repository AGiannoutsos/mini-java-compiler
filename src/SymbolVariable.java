package src;

public class SymbolVariable extends Symbol{

    public SymbolVariable(String type, String name, int offset){
        super(type, name, offset);
    }

    public SymbolVariable(String type, String name){
        super(type, name);
    }

    public String print(){
        return this.type + " " + this.name;
    }

}