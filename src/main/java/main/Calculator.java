package main;

import java.awt.*;

class Calculator extends Thread{
    int order;
    int index;
    int precision = 0;
    boolean purpose = true;
    static boolean angle = true;
    Calculator(int order, int index){
        this.order = order;
        this.index = index;
    }
    @Override
    public void run(){
        double[] c;
        double[] c2;
        double density = 5;
        while(purpose){
            if(Renderer.isOn) {
                if(Sets.mode == Sets.DIST){
                    double var = 2/(1000*Renderer.topographicStep);
                    var/=order;
                    int l = precision;
                    for (int i = -l; i <= l; i++) {
                        for (int j = -l; j <= l; j++) {
                            for (double v = i + index*var; v < (i+1); v += var) {
                                for (int k = j; k < (j+1); k++) {

                                    if(!Renderer.isOn) break;

                                    //horizontal lines
                                    c = Sets.calc_dist(v, k / density, Renderer.setOfInterest);
                                    if(Sets.connectLines) {
                                        c2 = Sets.calc_dist(v + var, k / density, Renderer.setOfInterest);
                                        Renderer.paintLine(c, c2, Color.blue);
                                    }else{
                                        Renderer.paintDot(c, Color.blue);
                                    }

                                    //vertical lines
                                    c = Sets.calc_dist(k / density, v, Renderer.setOfInterest);
                                    if(Sets.connectLines) {
                                        c2 = Sets.calc_dist(k / density, v + var, Renderer.setOfInterest);
                                        Renderer.paintLine(c, c2, Color.green);
                                    }else{
                                        Renderer.paintDot(c, Color.green);
                                    }
                                }
                            }
                            if (i > -l && i < l) {
                                j += l - 1;
                            }
                        }
                    }
                    if (Renderer.isOn) precision++;
                }else {
                    for (int y = 0; y < Window.jpHeight; y++) {
                        for (int x = index; x < Window.jpWidth; x += order) {
                            if (!Renderer.isOn) break;
                            if (Renderer.img.getRGB(x, y) == -16777216) {
                                if (Sets.mode == Sets.FRACTAL) {
                                    Renderer.draw_frac(x, y, precision);
                                } else if (Sets.mode == Sets.ABS) {
                                    if(angle){
                                        Renderer.draw_dir(x, y);
                                    }else {
                                        Renderer.draw_abs(x, y);
                                    }
                                }
                            }
                        }
                    }
                    if (Renderer.isOn && (Sets.mode == Sets.FRACTAL)) precision++;
                }
            }
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
