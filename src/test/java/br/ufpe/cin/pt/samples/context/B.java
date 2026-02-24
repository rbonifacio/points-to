package br.ufpe.cin.pt.samples.context;

public class B {
    Object foo(Object x, A a) {
        a.f = x;
        Object t = a.f;
        System.out.println(t);
        return x;
    }
}
