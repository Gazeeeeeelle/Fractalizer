package main;

public class Fractal{
    private final ComplexFunction f;
    Fractal(ComplexFunction f){
        this.f = f;
    }
    boolean f(int x, int y, double c1, double c2, int index){
        return f.cf(x, y, c1, c2, index);
    }
}
