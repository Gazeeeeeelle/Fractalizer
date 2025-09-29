package main;

import java.awt.*;

class Calculator extends Thread{
    int order;
    int index;
    int precision = 0;
    int n = 1;
    boolean purpose = true;
    Calculator(int order, int index){
        this.order = order;
        this.index = index;
    }
    @Override
    public void run(){
        long time = System.currentTimeMillis();
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
                                        Renderer.draw_line(c, c2, Color.blue);
                                    }else{
                                        Renderer.draw_dot(c, Color.blue);
                                    }

                                    //vertical lines
                                    c = Sets.calc_dist(k / density, v, Renderer.setOfInterest);
                                    if(Sets.connectLines) {
                                        c2 = Sets.calc_dist(k / density, v + var, Renderer.setOfInterest);
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
                    if (Renderer.isOn) precision++;
                } else {
                    for (int y = 0; y < Window.jpHeight; y++) {
                        for (int x = index; x < Window.jpWidth; x += order) {
                            if (!Renderer.isOn) break;
                            //if the pixel isn't colored
                            if (Renderer.img.getRGB(x, y) == -16777216) {
                                if (Sets.mode == Sets.FRACTAL) {
                                    Renderer.draw_frac(x, y, precision, n);
                                } else if (Sets.mode == Sets.ABS) {
                                    Renderer.draw_dir(x, y);
                                }
                            }
                        }
                    }
                    n = (Renderer.night ? Sets.solN : 1);
//                    n = (Renderer.night ? Sets.solN : 100000);
                    if (Renderer.isOn && (Sets.mode == Sets.FRACTAL)) precision++;

//                    if(System.currentTimeMillis() - time > 100){
//                        System.out.println("DONE: " + (System.currentTimeMillis() - time) + "ms");
//                        time = System.currentTimeMillis();
//                        Renderer.clearImage();
//                    }

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