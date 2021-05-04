package src;

abstract public class Symbol{

    String type;
    String name;
    int offset;
    boolean overrided;

    static String INT = "int";
    static String BOOL = "boolean";
    static String ARR = "int[]";

    public Symbol(String type, String name, int offset){
        this.type = type;
        this.name = name;
        this.offset = offset;
        this.overrided = false;
    }

    public Symbol(String type, String name){
        this.type = type;
        this.name = name;
        this.offset = 0;
        this.overrided = false;
    }

    public Symbol(String type, String name, int offset, boolean overrided){
        this.type = type;
        this.name = name;
        this.offset = offset;
        this.overrided = overrided;
    }

    public Symbol(String type, String name, boolean overrided){
        this.type = type;
        this.name = name;
        this.offset = 0;
        this.overrided = overrided;
    }

    public int getOffset(){
        return this.offset;
    }

    public int compareTo(Symbol otherSymbol) {
        return (this.offset - otherSymbol.getOffset());
    }

    public String toString() {
        return this.name+" : "+this.offset+"\n";
    }

    public Symbol putVariable(String key, Symbol variable) {return variable;}

    public Symbol putMethod(String key, Symbol method) {return method;}

    public Symbol putArgument(String key, Symbol argument) {return argument;}

    public Symbol getVariable(String key) {return null;}

    public Symbol getMethod(String key) {return null;}

    public Symbol getArgument(String key) {return null;}


}