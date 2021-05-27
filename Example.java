class MulInherit {
  public static void main(String[] args) {
    // int test;
    // int best;
    // boolean a;
    // a = true;
    // a = a && a;
    // test = 10;
    // if (test < 20) 
    //   System.out.println(1000);
    // else 
    //   System.out.println(10);
    // System.out.println((666+test)+1000);
    // System.out.println(777-10);
    // System.out.println((test*10));
    // System.out.println(best);
    // while(test < 5){
    //   test = test + 1;
    //   System.out.println(test);
    // }

    int[] arr;
    arr = new int[2];
    arr[1] = 100;
    // arr[(1-1)] = arr[0];
    // arr[(1-1)] = 20;
    System.out.println(arr[0]);
    System.out.println(arr[1]);

    
  }
}

class A{
  int a1;
  int a2;
  boolean a3;
  int a4;
  public int foo(int aa, boolean bb, C cc){ int a1;
                                         a2=666; 
                                         System.out.println(777);
                                         System.out.println(a2);
                                          return 1;}
  public int bla(){ int kk; kk=1; return 1;}
  public int[] foo2(int[] arr){ return new int[10];}
}

class B extends A{
  int a5;
  public int bla(){ C c; return 1;}
  public int bla2(){ C c; return 1;}
}


// define i32 @A.foo(i8* %this) {
  // 	ret i32 0
  // }
  
  // define i32 @A.bla(i8* %this) {
    // 	ret i32 0
    // }
    
    // define i32 @A.foo2(i8* %this) {
      // 	ret i32 0
      // }
      
      // define i32 @B.bla(i8* %this) {
        // 	ret i32 0
        // }
class C{
}