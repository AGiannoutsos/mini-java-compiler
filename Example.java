class Example {
    public static void main(String[] args) {
        int aaaa;
    }
}

class A {
    int i;
    A a;
    A aa;
    A aaa;
    C c;
    B b;

    public int foo(int i, int j) {int k; int kk; int klk; a = this;  return i+j; }
    public int bar(){ return 1; }
}

class C {
    int i;
    A a;
    int ii;

    public int foo(int i, int j) { int koo;return i+j; }
    public int bar(){ return 1; }
}

class D {
    int i;
    A a;
    int ii;

    public int foo(int i, int j) { return i+j; }
    public int bar(){ return 1; }
}


class B extends A {
    int i;
    boolean k;
    A a;

    public int foo(int i, int j, boolean ii) {boolean iii; return i+j; }
    public int foobar(boolean k){ B aa; a = this; return 1; }
}
