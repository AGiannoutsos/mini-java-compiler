class MulInherit {
  public static void main(String[] args) {
    System.out.println(666);
  }
}

class A{
  public int foo(int aa, boolean bb, C cc){ return 1;}
  public int bla(){ int kk; kk=1; return 1;}
  public int[] foo2(int[] arr){ return new int[10];}
}

class B{
  public int bla(){ C c; return 1;}
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