package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

class Renderer extends Thread {
    static final int whiteRGB = -1;
    static BufferedImage img = new BufferedImage(Window.jpWidth, Window.jpHeight, BufferedImage.TYPE_INT_RGB);
    static Graphics2D img_g = (Graphics2D) img.getGraphics();
    static Graphics2D g = (Graphics2D) Window.Fractalizer.getGraphics();
    static int setOfInterest = 1;
    static double fromX = ((double) main.Window.jpWidth/ main.Window.jpHeight * -2);
    static double toX = ((double) main.Window.jpWidth/ main.Window.jpHeight * 2);
    static double fromY = -2;
    static double toY = 2;
    static final double[] initialCoordinates = new double[]{fromX, toX, fromY, toY};
    static int[] colors1 = {
            new Color(66, 30, 15).getRGB(),
            new Color(25, 7, 26).getRGB(),
            new Color(9, 1, 47).getRGB(),
            new Color(4, 4, 73).getRGB(),
            new Color(0, 7, 100).getRGB(),
            new Color(12, 44, 138).getRGB(),
            new Color(24, 82, 177).getRGB(),
            new Color(57, 125, 209).getRGB(),
            new Color(134, 181, 229).getRGB(),
            new Color(211, 236, 248).getRGB(),
            new Color(241, 233, 191).getRGB(),
            new Color(248, 201, 95).getRGB(),
            new Color(255, 170, 0).getRGB(),
            new Color(204, 128, 0).getRGB(),
            new Color(153, 87, 0).getRGB(),
            new Color(106, 52, 3).getRGB()
    };
    static int[] greenTone = makeTone(0, 1, 0, 50, 100, 0, 100, true, false);
    static int[] iceCoaledTone = makeTone(0, 1, 1, 0, 50, 255, 100, true, false);
    static int[] redToYellowTone = makeTone(1, 1, 0, 255, 50, 0, 100, true, false);
    static int[] blueToPurpleTone = makeTone(1, 0, 1, 50, 0, 255, 100, true, false);
    static int[] greenToYellowTone = makeTone(1, 1, 0, 50, 255, 0, 100, true, false);
    static int[] grayTone = makeTone(1, 1, 1, 60, 60, 60, 70, true, false);
    static int[] rainbowTone = rainbowMaker(250);
    static int[][] colorPalettes;
    static int whichColorPalette = 0;
    static boolean isOn = true;
    static Calculator[] calc;
    static boolean showPosition = false;
    static double topographicStep = .1;
    static boolean axis = false;
    static boolean night = false;
    static boolean preCalcRange = true;
    @Override
    public void run(){
        initialize();
        int[] np = readData();
        buildCalculatorSet(np[0], np[1]);
        while(true){


            g.drawImage(img, null, 0, 0);
            g.setColor(Color.white);
            if(axis) {
                g.drawLine(coorToPxX(0), coorToPxY(fromY), coorToPxX(0), coorToPxY(toY));
                g.drawLine(coorToPxX(fromX), coorToPxY(0), coorToPxX(toX), coorToPxY(0));
            }

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
    static void buildCalculatorSet(int nOfCalc, int priority){
        nOfCalc = limitToRange(nOfCalc, 0, 12);
        calc = new Calculator[nOfCalc];
        for (int i = 0; i < nOfCalc; i++) {
            calc[i] = new Calculator(nOfCalc, i);
        }
        for (int i = 0; i < nOfCalc; i++) {
            calc[i].setPriority(limitToRange(priority, 1, 10));
            calc[i].start();
        }
        clearImage();
        System.out.println("Calculators started.");
    }
    static void paintDot(double[] c, Color color){
        if(c[0] > Renderer.fromX && c[0] < Renderer.toX &&
                c[1] > Renderer.fromY && c[1] < Renderer.toY
        ) {
            int px = Renderer.coorToPxX(c[0]);
            int py = Renderer.coorToPxY(c[1]);
            if(px>=0 && px < img.getWidth() &&
                    py>=0 && py < img.getHeight()
            ){
                img.setRGB(px, py, color.getRGB());
            }
        }
    }
    static void paintLine(double[] c1, double[] c2, Color color){
        assert c1.length == 2;
        assert c2.length == 2;
        if(Double.isNaN(c1[0])) return;
        int px1 = Renderer.coorToPxX(c1[0]);
        int py1 = Renderer.coorToPxY(c1[1]);
        int px2 = Renderer.coorToPxX(c2[0]);
        int py2 = Renderer.coorToPxY(c2[1]);
        img_g.setColor(color);
        img_g.drawLine(px1, py1, px2, py2);
    }
    static void destroyCalculatorSet(){
        try {
            for (Calculator c : calc) {
                c.purpose = false;
            }
        }catch (NullPointerException e){
            //pass
        }
    }
    static int limitToRange(int value, int min, int max){
        return Math.max(Math.min(value, max), min);
    }
    static void draw_frac(int x, int y, int precision){
        if (Sets.calc_frac(x, y, precision)) {
            if (night) {
                img.setRGB(x, y, whiteRGB);
            } else {
                img.setRGB(
                        x, y,
                        colorPalettes[whichColorPalette] [
                            precision % colorPalettes[whichColorPalette].length
                        ]
                );
            }
        }
    }
    static void draw_abs(int x, int y){
        img.setRGB(
                x, y,
                colorPalettes[whichColorPalette] [
                        Math.floorMod((int)(Sets.calc_abs(x, y)/topographicStep), colorPalettes[whichColorPalette].length)
                        ]
        );
    }
    static void draw_dir(int x, int y){
        img.setRGB(
                x, y,
                getColorDir(Sets.calc_dir(x, y))
        );
    }
    static int getColorDir (double[] z){
        assert z.length == 2;
        double arg = Math.toDegrees(ComplexMath.arg1(z[0], z[1]));
        if(arg < 0) arg = 360 + arg;
        int r=1, g=1, b=1;
        if(arg >= 300 || arg <= 60){
            r = 255;
            if(arg >= 300){
                g = (int)(255*(360-arg)/60);
            } else {
                b = (int)(255*(arg)/60);
            }
        }
        if(arg >= 60 && arg <= 180){
            b = 255;
            if(arg <= 120){
                r = (int)(255*(120-arg)/60);
            } else {
                g = (int)(255*(arg-120)/60);
            }
        }
        if(arg >= 180 && arg <= 300){
            g = 255;
            if(arg <= 240){
                b = (int)(255*(240-arg)/60);
            } else {
                r = (int)(255*(arg-240)/60);
            }
        }
        double abs = Math.hypot(z[0], z[1]);
        double i = topographicStep*100;
        if(abs < i) {
            double k = abs / i;
            r = (int) (1d * r * k);
            g = (int) (1d * g * k);
            b = (int) (1d * b * k);
        }else{
            r += (int)(abs/(topographicStep*10));
            g += (int)(abs/(topographicStep*10));
            b += (int)(abs/(topographicStep*10));
        }
        if(r > 255) r = 255;
        if(g > 255) g = 255;
        if(b > 255) b = 255;
        if(r == 0 && r == g && r == b){
            r = 1;
        }
        return (r<<16)+(g<<8)+(b);
    }
    static boolean isDraw(int x, int y, int precision){
        return Sets.calc_frac(x, y, precision);
    }
    private static void initialize(){
        colorPalettes = new int[][]{
                colors1,
                greenTone,
                redToYellowTone,
                blueToPurpleTone,
                iceCoaledTone,
                greenToYellowTone,
                rainbowTone,
                grayTone
        };
        g = (Graphics2D) main.Window.Fractalizer.getGraphics();
        AffineTransform af = new AffineTransform();
        af.setToScale((double) main.Window.width * 1.25 / (double) main.Window.jpWidth,
                (double) main.Window.height * 1.25 / (double) main.Window.jpHeight
        );
        g.setTransform(af);

    }
    static void resetPrecision(){
        if(Sets.mode==Sets.FRACTAL) {
            int p = (preCalcRange ? findPrecision() : 0);
            for (Calculator c : calc) {
                c.precision = (night ? Sets.solN : p);
            }
        }else{
            for (Calculator c : calc) {
                c.precision =  0;
            }
        }
    }
    private static int findPrecision(){
        int p = 0;
        int sp = img.getWidth()/10;
        int max = (int)Math.log10(getZoom()) * 100;
        for(;;){
            for (int x = 0; x < img.getWidth(); x+=sp+1) {
                for (int y = 0; y < img.getHeight(); y+=sp+1) {
                    if(isDraw(x, y, p)) return p;
                }
            }
            p++;
            if(p > max) return max;
        }
    }
    static void clearImage(){
        isOn = false;
        resetPrecision();
        Graphics imgG = img.getGraphics();
        imgG.setColor(Color.black);
        imgG.fillRect(0,0, main.Window.jpWidth, main.Window.jpHeight);
        isOn = true;
    }
    private static void clearImage(BufferedImage image){
        Graphics imgG = img.getGraphics();
        imgG.drawImage(image, 0, 0, null);
    }
    static void resetRange(){
        fromX = initialCoordinates[0];
        toX = initialCoordinates[1];
        fromY = initialCoordinates[2];
        toY = initialCoordinates[3];
    }
    private static double[] getCenter(){
        return new double[]{
                (toX+fromX)/2,
                (toY+fromY)/2,
        };
    }
    static void zoomIn(double zoom){
        double[] c = getCenter();
        fromX += Math.abs(fromX-c[0]) * zoom;
        toX   -= Math.abs(toX-c[0]) * zoom;
        fromY += Math.abs(fromY-c[1]) * zoom;
        toY   -= Math.abs(toY-c[1]) * zoom;
        clearImage();
    }
    static void zoomOut(double zoom){
        double[] c = getCenter();
        fromX += (fromX-c[0]) * zoom;
        toX   += (toX-c[0]) * zoom;
        fromY += (fromY-c[1]) * zoom;
        toY   += (toY-c[1]) * zoom;
        clearImage();
    }
    static double getZoom(){
        return (4/(Renderer.toY-Renderer.fromY));
    }
    private static int[] makeTone(double r, double g, double b, int minimumR, int minimumG, int minimumB, int size, boolean back, boolean invert){
        minimumR = (int) ((double) minimumR * r);
        minimumG = (int) ((double) minimumG * g);
        minimumB = (int) ((double) minimumB * b);
        Color[] colors;
        if(back) {
            colors = new Color[(size * 2) - 1];
        }else{
            colors = new Color[size];
        }
        final double rk = (r * 255 - minimumR) / size;
        final double gk = (g * 255 - minimumG) / size;
        final double bk = (b * 255 - minimumB) / size;
        int rv, gv, bv;
        for (int i = 0; i < size; i++) {
            rv = (int) ((i+1) * rk) + minimumR;
            gv = (int) ((i+1) * gk) + minimumG;
            bv = (int) ((i+1) * bk) + minimumB;
            colors[i] = new Color(rv, gv, bv);
        }
        if(back) {
            int j = size;
            for (int i = size - 1; i > 0; i--) {
                colors[j] = colors[i];
                j++;
            }
        }
        if(invert){
            for (int i = 0; i < colors.length/2; i++) {
                Color kc1 = colors[colors.length-1-i];
                Color kc2 = colors[i];
                colors[colors.length-1-i] = kc2;
                colors[i] = kc1;
            }
        }
        int[] colorsn = new int[colors.length];
        int jk = 0;
        for(Color c : colors){
            colorsn[jk] = c.getRGB();
            jk++;
        }
        return colorsn;
    }
    private static int[] rainbowMaker(int size){
        size /= 6;
        final int[][] colorss = new int[][]{
                makeTone(1, 1, 0, 255, 0, 0, size, false, false),
                makeTone(1, 1, 0, 0, 255, 0, size, false, true),
                makeTone(0, 1, 1, 0, 255, 0, size, false, false),
                makeTone(0, 1, 1, 0, 0, 255, size, false, true),
                makeTone(1, 0, 1, 0, 0, 255, size, false, false),
                makeTone(1, 0, 1, 255, 0, 0, size, false, true)
        };
        int[] color = new int[size * 6];
        int i = 0;
        for(int[] bc : colorss){
            for(int c : bc){
                color[i] = c;
                i++;
            }
        }
        return color;
    }
    private static int getScreenshotNum(){
        return Integer.parseInt(getTextFile("/scn.txt"));
    }
    private static String getTextFile(String path){
        try {
            return new BufferedReader(new InputStreamReader(Renderer.class.getResource(path).openStream()))
                    .readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    static void screenshot(){
        int screenshotNum = getScreenshotNum();
        File file = new File("screenshot" + screenshotNum + ".png");
        try {
            ImageIO.write(img, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private static int[] readData(){
        ArrayList<Integer> n = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(Renderer.class.getResource("/data.txt").openStream()));
            for(String s : br.lines().toList()){
                try{
                    n.add(Integer.parseInt(s));
                }catch (NumberFormatException e){
                    //pass
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int[] na = new int[n.size()];
        for (int i = 0; i < n.size(); i++) {
            na[i] = n.get(i);
        }
        return na;
    }
    static double pxToCoorX(int px){
        return ((toX - fromX) * ((double)px/ Window.jpWidth))
                + fromX;
    }
    static double pxToCoorY(int px){
        return ((fromY - toY) * ((double)px / Window.jpHeight))
                + toY;
    }
    static int coorToPxX(double coor){
        return (int)((coor-fromX) * Window.jpWidth
                /(toX - fromX));
    }
    static int coorToPxY(double coor){
        return (int)((coor-toY) * Window.jpHeight
                        / (fromY - toY));
    }
    static void move(int dx, int dy){
        double dPixX = (toX-fromX)/ Window.jpWidth;
        double dPixY = (toY-fromY)/ Window.jpHeight;

        fromX -=  dPixX*dx;
        toX   -=  dPixX*dx;
        fromY -= -dPixY*dy;
        toY   -= -dPixY*dy;

        clearImage(moveImage(-dx, -dy, img));
    }
    private static BufferedImage moveImage(int dx, int dy, BufferedImage img){
        BufferedImage local = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        local.setData(img.getRaster());
        shiftX(dx, local);
        shiftY(dy, local);
        return local;
    }
    private static void shiftX(int dx, BufferedImage img){
        BufferedImage local = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < img.getWidth(); x++) {
            if(((x+dx) >= 0) && ((x+dx) < img.getWidth())){
                for (int y = 0; y < img.getHeight(); y++) {
                    local.setRGB(x, y, img.getRGB(x+dx, y));
                }
            }
        }
        img.setData(local.getRaster());
    }
    private static void shiftY(int dy, BufferedImage img){
        BufferedImage local = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            if(((y+dy) >= 0) && ((y+dy) < img.getHeight())){
                for (int x = 0; x < img.getWidth(); x++) {
                    local.setRGB(x, y, img.getRGB(x, y+dy));
                }
            }
        }
        img.setData(local.getRaster());
    }
    static String getInfo(){
        String ret = "";
        ret += "----------------------------------------------------------------"+"\n";
        ret += (Renderer.fromX+" -> "+Renderer.toX+", "+Renderer.fromY+"i -> "+Renderer.toY+"i"+"\n");
        ret += ("zoom: "+ Renderer.getZoom()+"\n");
        if(Sets.mode == Sets.FRACTAL) {
            ret += ("Set: " +
                    switch (Renderer.setOfInterest) {
                        case 1 -> "Mandelbrot Set";
                        case 2 -> "Burning Ship Set";
                        case 3 -> "Celtic Set";
                        case 4 -> "Inverse Mandelbrot Set";
                        default -> "Set not found.";
                    }
                    + "\n"
            );
            ret += ("Is Julia Set On: " + Sets.julia + "\n");
        }
        ret += ("Z: " + Sets.z1x + "+" + Sets.z1y + "i" + "\n");
        ret += ("----------------------------------------------------------------");
        return ret;
    }
}