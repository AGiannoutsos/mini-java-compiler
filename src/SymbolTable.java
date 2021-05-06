package src;

import java.util.*;
// import java.util.Comparator;

public class SymbolTable extends TreeMap<String, Symbol>{
    String name;

    public SymbolTable(String name){        
        this.name = name;
    }
    public SymbolTable(){        
        this.name = null;
    }

    // code from here https://stackoverflow.com/questions/2864840/treemap-sort-by-value
    public SortedSet<Map.Entry<String, Symbol>> getSorted(){

        SortedSet<Map.Entry<String, Symbol>> sortedEntries = new TreeSet<Map.Entry<String, Symbol>>( new Comparator<Map.Entry<String, Symbol>>() {
            public int compare(Map.Entry<String, Symbol> e1, Map.Entry<String, Symbol> e2) {
                int res = e1.getValue().compareTo(e2.getValue());
                return res != 0 ? res : 1;
            }

        });
        sortedEntries.addAll(super.entrySet());
        return sortedEntries;
    }

    @Override
    public String toString(){
        String string = "";
        string += "------------------------------------------------------------------\n-----------File " + this.name+"-----------\n------------------------------------------------------------------\n\n";
        for (Map.Entry<String, Symbol> entry : this.getSorted()) {
            string += entry.getValue().toString() +"\n";
        }
        return string;
    }

}