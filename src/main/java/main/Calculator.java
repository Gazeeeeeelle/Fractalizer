package main;

import java.awt.*;

import static main.Renderer.*;

class Calculator extends Thread{
    static Calculator[] calc;
    static boolean isOn = true;
    int order;
    int index;
    int precision = 0;
    int n = 1;
    boolean purpose = true;
    double[] c;
    double[] c2;
    double density = 5;
    Calculator(int order, int index){
        this.order = order;
        this.index = index;
    }
    @Override
    public void run(){
        while(purpose){
            if(isOn) {
                if(Sets.mode == Sets.DIST){
                    dir();
                } else {
                    frac();
                    n = (Renderer.night ? Sets.solN : 1);
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
    private void frac(){
        for (int y = 0; y < Window.jpHeight; y++) {
            for (int x = index; x < Window.jpWidth; x += order) {
                if (!isOn) return;
                //if the pixel isn't colored
                if (img.getRGB(x, y) == -16777216) {
                    if (Sets.mode == Sets.FRACTAL) {
                        Renderer.draw_frac(x, y, precision, n);
                    } else if (Sets.mode == Sets.ABS) {
                        Renderer.draw_dir(x, y);
                    }
                }
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
                        c = Sets.calc_dist(v, k / density, Sets.setOfInterest);
                        if(Sets.connectLines) {
                            c2 = Sets.calc_dist(v + var, k / density, Sets.setOfInterest);
                            Renderer.draw_line(c, c2, Color.blue);
                        }else{
                            Renderer.draw_dot(c, Color.blue);
                        }

                        //vertical lines
                        c = Sets.calc_dist(k / density, v, Sets.setOfInterest);
                        if(Sets.connectLines) {
                            c2 = Sets.calc_dist(k / density, v + var, Sets.setOfInterest);
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
    static void resetPrecision(){
        if(Sets.mode == Sets.FRACTAL) {
            int p = (Renderer.preCalcRange ? findPrecision() : 0);
            for (Calculator c : calc) {
                c.precision = (Renderer.night ? Sets.solN : p);
                c.n = (Renderer.night ? Sets.solN : p+1);
            }
        }else{
            for (Calculator c : calc) {
                c.precision =  0;
            }
        }
    }
    private static int findPrecision(){
        int p = 0;
        int sp = Renderer.w/10;
        int max = (int)Math.log10(Renderer.getZoom()) * 100;
        for(;;){
            for (int x = 0; x < img.getWidth(); x+=sp+1) {
                for (int y = 0; y < img.getHeight(); y+=sp+1) {
                    if(isDraw(x, y, p+1)) return p;
                }
            }
            p++;
            if(p > max) return max;
        }
    }
    static void buildCalculatorSet(int nOfCalc, int priority){
        nOfCalc = Renderer.limitToRange(nOfCalc, 0, 12);
        calc = new Calculator[nOfCalc];
        for (int i = 0; i < nOfCalc; i++) {
            calc[i] = new Calculator(nOfCalc, i);
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
}