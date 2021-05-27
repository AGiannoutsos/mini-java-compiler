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
    A a;
    int i;
    boolean boo;
    // arr = new int[2];
    // arr[0] = 100;
    // arr[(1-1)] = arr[0];
    // // arr[(1-1)] = 20;
    // System.out.println(arr[0]);
    // System.out.println(arr[1]);
    // System.out.println(arr.length);

    a = new A();
    
    i = (a.foo(74, (new A()).foo(1, new A(), new C()), new C())).bla();
    System.out.println(i);
    
    boo = (a.bo1()) && (a.bo2());
    if (boo) 
      System.out.println(3333);
    else 
      System.out.println(4444);

  }
}

class A{
  int a1;
  int a2;
  boolean a3;
  int a4;

  public boolean bo1(){
    System.out.println(1111);
    return true;
  } 
  public boolean bo2(){
    System.out.println(2222);
    return false;
  } 
  public A foo(int aa, A bb, C cc){ 
                                        A a;
                                        a1=aa; 
                                        System.out.println(777);
                                        System.out.println(a2);
                                        // a.a1 = aa;
                                        a = new A();
                                        return a;}
  public int bla(){ int kk; kk=44; return 55;}
  public int[] foo2(int[] arr){ return new int[10];}
}

class B extends A{
  int a5;
  public int bla(){ C c; return 66;}
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