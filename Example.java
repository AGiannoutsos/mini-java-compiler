	// The classes are basically the same as the BinaryTree 
	// file except the visitor classes and the accept method
	// in the Tree class

class Main{
	public static void main(String[] aa){
		D a;
		a = new D();
		System.out.println((a.a()));
	}
}

class A{
	int a;
	public int a() {return a;}
}

class B extends A{
	int b;
	public int b() {return a;}
	public int a() {return a;}
}

class C extends B{
	int c;
	public int c() {return a;}
	// public int a() {return a;}

}

class D extends C{
	int d;
	public int a() {return a;}
	public int d() {return d;}
}