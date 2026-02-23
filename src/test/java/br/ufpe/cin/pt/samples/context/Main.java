package br.ufpe.cin.pt.samples.context;

public class Main {
    public static void main(String[] args) {
        Object o1 = new Object();
        B b1 = new B();
        A a1 = new A();
        Object v1 = b1.foo(o1, a1);
        Object o2 = new Object();
        B b2 = new B();
        Object v2 = b2.foo(o2, a1);
        System.out.println(v1.equals(v2));
    }
}
