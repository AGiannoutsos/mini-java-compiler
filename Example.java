class Example {
    public static void main(String[] args) {
    }
}

class A {
    int i;
    A a;
    A aa;
    A aaa;

    public int foo(int i, int j) { return i+j; }
    public int bar(){ return 1; }
}

class C {
    int i;
    A a;
    int ii;

    public int foo(int i, int j) { return i+j; }
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

    public int foo(int i, int j) { return i+j; }
    public int foobar(boolean k){ return 1; }
}
