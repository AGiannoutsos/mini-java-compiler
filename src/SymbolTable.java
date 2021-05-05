package src;

import java.util.*;
// import java.util.Comparator;

public class SymbolTable extends TreeMap<String, Symbol>{
    String name;

    // static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<String,V>> getSortedd(SymbolTable<V> map) {
    //     SortedSet<SymbolTable.Entry<V>> sortedEntries = new TreeSet<SymbolTable.Entry<V>>( new Comparator<SymbolTable.Entry<V>>() {
    //         @Override
    //         public int compare(SymbolTable.Entry<V> e1, SymbolTable.Entry<V> e2) {
    //             int res = e1.getValue().compareTo(e2.getValue());
    //             return res != 0 ? res : 1;
    //         }
    //     });
    //     sortedEntries.addAll(map.entrySet());
    //     return sortedEntries;
    // }
    
    public SymbolTable(String name){        
        // super(new Comparator<String>() {
        //     @Override public int compare(String s1, String s2) {
        //         return 0;
        //     }           
        // } );
        this.name = name;
    }
    public SymbolTable(){        
        this.name = null;
    }

    // public Symbol get(String key){
    //     System.out.println("oeorerer");
    //     if (key.startsWith("Class "))
    //         return super.get(key.replaceFirst("Class ", ""));
    //     else    
    //         return super.get(key);
    // }

    // code from here https://stackoverflow.com/questions/2864840/treemap-sort-by-value
    // erwthsh giati uparxei h java? 
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