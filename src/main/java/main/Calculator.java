package main;

import complexMath.Sets;

import java.awt.*;

class Calculator extends Thread{
    static Calculator[] calc;
    static boolean isOn = true;
    static boolean preCalculation = true;
    static private final double density = 5;
    private final int width;
    private final int height;
    private final int order;
    private final int index;
    private int precision = 0;
    private int n = 1;
    private boolean purpose = true;
    double[] c;
    double[] c2;
    Calculator(int width, int height, int order, int index){
        this.width = width;
        this.height = height;
        this.order = order;
        this.index = index;
    }
    @Override
    public void run(){
        while(purpose){
            if(isOn) {
                switch (Sets.mode){
                    case Sets.DIR -> dir();
                    case Sets.FRACTAL -> {
                        fractal();
                        n = Renderer.isPinpointPrecision() ? Sets.solN : 1;
                    }
                    case Sets.ABS -> abs();
                }
                precision++;
            }
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private void fractal(){
        for (int y = 0; y < height; y++) {
            for (int x = index; x < width; x += order) {
                if (!isOn) return;
                if (Renderer.getRGB(x, y) == 0xff000000) {
                    Renderer.draw_frac(x, y, precision, n);
                }
            }
        }
    }
    private void abs(){
        for (int y = 0; y < height; y++) {
            for (int x = index; x < width; x += order) {
                if(!isOn) return;
                Renderer.draw_abs(x, y, 0, precision+1);
            }
        }
    }
    private void dir(){
        double var = 2/(1000*Sets.topographicStep);
        var/=order;
        int l = precision;
        for (int i = -l; i <= l; i++) {
            for (int j = -l; j <= l; j++) {
                for (double v = i + index*var; v < (i+1); v += var) {
                    for (int k = j; k < (j+1); k++) {

                        if(!isOn) return;

                        //horizontal lines
                        c = Sets.calc_dir(v, k / density, Sets.setOfInterest);
                        if(Sets.connectLines) {
                            c2 = Sets.calc_dir(v + var, k / density, Sets.setOfInterest);
                            Renderer.draw_line(c, c2, Color.blue);
                        }else{
                            Renderer.draw_dot(c, Color.blue);
                        }

                        //vertical lines
                        c = Sets.calc_dir(k / density, v, Sets.setOfInterest);
                        if(Sets.connectLines) {
                            c2 = Sets.calc_dir(k / density, v + var, Sets.setOfInterest);
                            Renderer.draw_line(c, c2, Color.green);
                        }else{
                            Renderer.draw_dot(c, Color.green);
                        }
                    }
                }
                if (i > -l && i < l) {
                    j += l - 1;
                }
            }
        }
    }
    static void resetPrecisions(){
        int p = (preCalculation ? Renderer.preCalculateStartingPrecision() : 0);
        System.out.println(p);
        for(Calculator c : calc){
            c.resetPrecision(p);
        }
    }
    private void resetPrecision(int p){
        if(Sets.mode == Sets.FRACTAL) {
            boolean isPinpoint = Renderer.isPinpointPrecision();
            this.precision = (isPinpoint ? Sets.solN : p);
            this.n = (isPinpoint ? Sets.solN : p+1);
        }else{
            this.precision = 0;
        }
    }
    static void buildCalculatorSet(int width, int height, int nOfCalc, int priority){
        nOfCalc = Renderer.limitToRange(nOfCalc, 0, 12);
        calc = new Calculator[nOfCalc];
        for (int i = 0; i < nOfCalc; i++) {
            calc[i] = new Calculator(width, height, nOfCalc, i);
        }
        for (int i = 0; i < nOfCalc; i++) {
            calc[i].setPriority(Renderer.limitToRange(priority, 1, 10));
            calc[i].start();
        }
        Renderer.clearImage();
        System.out.println("Calculators started.");
    }
    static void destroyCalculatorSet(){
        try {
            for (Calculator c : Calculator.calc) {
                c.purpose = false;
            }
        }catch (NullPointerException e){
            //pass
        }
    }
    public int getPrecision(){
        return precision;
    }
}