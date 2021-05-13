class Main {
    public static void main(String[] a) {}
  }
  
  class A {
    public C foo(A a) { return new C(); }
  }
  
  class B extends A {
    
  }
  
  class C extends B {

    public C bla(A c) {return this;}
    public C foo(A c) { 
      
      return this.bla(this);

    }
  }

  