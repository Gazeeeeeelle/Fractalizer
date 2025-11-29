package main;

import complexMath.Sets;
import static complexMath.Sets.clearCache;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Renderer extends Thread {
    private static final Window window = new Window();
    static boolean special = false;
    public static final int w = window.jpWidth;
    public static final int h = window.jpHeight;
    private static final BufferedImage
            img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB),
            img_black = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB),
            ovr = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB),
            ovr_blank = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    private static final Graphics2D
            img_g = (Graphics2D) img.getGraphics(), //BufferedImage's graphics
            ovr_g = (Graphics2D) ovr.getGraphics(), //Second BufferedImage's graphics
            g = (Graphics2D) window.fractalizer.getGraphics(); //Window's JPanel's graphics
    private static double //Rendering bounds:
            fromX = ((double) w / h * -2),
            toX = ((double) w / h * 2),
            fromY = -2,
            toY = 2;
    private static final double[] initialCoordinates = new double[]{fromX, toX, fromY, toY};
    private static boolean //options:
            showPosition = false,
            showAxis = false,
            pinpointPrecision = false;
    @Override
    public void run(){
        initialize();
        Calculator.buildCalculatorSet(w, h, 6, 10);//initial threads
        while(true){

            if(showAxis) {
                drawAxis();
                g.drawImage(imgUnion(img, ovr), 0, 0, null);
            }else{
                g.drawImage(img, 0, 0, null);
            }

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }
    private static BufferedImage imgUnion(BufferedImage... images){
        int w = images[0].getWidth();
        int h = images[0].getHeight();
        BufferedImage ret = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) ret.getGraphics();
        for (BufferedImage bi : images) {
            if(bi.getWidth() != w || bi.getHeight() != h){
                throw new IllegalArgumentException("Images do not match in size.");
            }
            g.drawImage(bi, 0, 0, null);
        }
        return ret;
    }
    private static void drawAxis(){
        ovr.setData(ovr_blank.getRaster());
        ovr_g.setColor(Color.white);
        ovr_g.drawLine(
                c2px(0),
                c2py(toY),
                c2px(0),
                c2py(fromY)
        );
        ovr_g.drawLine(
                c2px(fromX),
                c2py(0),
                c2px(toX),
                c2py(0)
        );
        numbers();
        g.drawImage(ovr, 0,0, null);
    }
    private static void numbers(){
        ovr_g.setColor(Color.white);
        double delta = findDistanceBetweenMarks();
        double dpx = p2cx(1)-p2cx(0);
        for (int x = 0; x < w; x++) {
            if(mod(p2cx(x), delta) <= dpx){
                ovr_g.drawString(String.valueOf(roundTo(p2cx(x), delta)), x, c2py(0));
            }
        }
        for (int y = 0; y < h; y++) {
            if(mod(p2cy(y), delta) <= dpx){
                ovr_g.drawString(roundTo(p2cy(y), delta)+"i", c2px(0), y);
            }
        }
    }
    private static double findDistanceBetweenMarks(){ //FIXME rename to something more suggestive
        int d = 2;
        return Math.pow(2, ((int)log2(toX-fromX) - d));
    }
    private static double log2(double a){
        return Math.log(a)/Math.log(2);
    }
    private static double difference(double a, double b){
        return Math.abs(a-b);
    }
    private static double roundTo(double a, double b){
        return ((int)(a/b))*b;
    }
    private static double mod(double a, double b){
        return difference(a,b*(int)(a/b));
    }
    static void draw_frac(int x, int y, int precision, int times){
        if (Sets.calc_frac(x, y, 0, times)) { //FIXME draw_mode:f ==> calc_mode:f
            if (pinpointPrecision) {
                 img.setRGB(x, y, -1);
            } else {
                 img.setRGB(x, y, Colors.getColorForPrecision(precision));
            }
        }
    }
    static void draw_abs(int x, int y, int index, int nth){
        if(Sets.calc_abs(x, y, index, nth)) { //FIXME draw_mode:f ==> calc_mode:f
            if(Sets.isJulia()){
                img.setRGB(
                        x, y,
                        Colors.getInverseColorDir(x, y, index)
                );
            }else {
                img.setRGB(
                        x, y,
                        Colors.getColorDir(Sets.getFromCache(x, y, index))
                );
            }
        }
    }
    static void draw_dot(double[] z, Color color){
        if(z[0] > Renderer.fromX && z[0] < Renderer.toX &&
                z[1] > Renderer.fromY && z[1] < Renderer.toY
        ) {
            int px = Renderer.c2px(z[0]);
            int py = Renderer.c2py(z[1]);
            if(px>=0 && px < w && py>=0 && py < h){ //FIXME this is the second time it checks if it is in bounds
                img.setRGB(px, py, color.getRGB());
            }
        }
    }
    static void draw_line(double[] c1, double[] c2, Color color){
        if(Double.isNaN(c1[0])) return;
        img_g.setColor(color);
        img_g.drawLine(c2px(c1[0]), c2py(c1[1]), c2px(c2[0]), c2py(c2[1]));
    }
    static boolean isDraw(int x, int y){
        return Sets.calc_frac(x, y, 1, 1);
    }
    private static void initialize(){
        AffineTransform af = new AffineTransform();
        af.setToScale((double) window.width * 1 / (double) w, //FIXME
                (double) window.height * 1 / (double) h
        );
        g.setTransform(af);
    }
    public static void clearImage(){
        turnOff();
        clearImage(img_black);
        turnOn();
    }
    private static void clearImage(BufferedImage image){
        Calculator.resetPrecisions();
        img_g.drawImage(image, 0, 0, null);
        clearCache(0, 1);
    }
    static void resetRange(){
        fromX = initialCoordinates[0];
        toX = initialCoordinates[1];
        fromY = initialCoordinates[2];
        toY = initialCoordinates[3];
        Renderer.clearImage();
    }
    private static int[] getPixelCenter(){
        return new int[]{
                w/2,
                h/2,
        };
    }
    static void centerAtPixel(){
        int[] pos = window.getMousePos();
        if(pos != null) {
            Renderer.move(
                    getPixelCenter()[0] - pos[0],
                    getPixelCenter()[1] - pos[1]
            );
        }
    }
    static void zoomIn() {
        turnOff();
        setBounds(
                p2cx(w / 4),
                p2cx(w - w / 4),
                p2cy(h - h / 4),
                p2cy(h / 4)
        );
        if(Sets.mode != complexMath.Mode.DIR) {
            clearImage(zoomInImage());
        }else{
            clearImage();
        }
        turnOn();
    }
    static void zoomOut(){
        turnOff();
        setBounds(
                p2cx(-w/2),
                p2cx(w + w/2),
                p2cy(h + h/2),
                p2cy(-h/2)
        );
        if(Sets.mode != complexMath.Mode.DIR) {
            clearImage(zoomOutImage());
        }else{
            clearImage();
        }
        turnOn();
    }
    static BufferedImage zoomInImage(){
        BufferedImage ret = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int dw = w/4;
        int dh = h/4;
        for (int x = 0; x < dw*2; x++) {
            for (int y = 0; y < dh*2; y++) {
                ret.setRGB(x*2, y*2, img.getRGB(dw + x, dh + y));
            }
        }
        return ret;
    }
    static BufferedImage zoomOutImage(){
        BufferedImage ret = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        int dw = w/4;
        int dh = h/4;
        for (int x = 0; x < dw*2; x++) {
            for (int y = 0; y < dh*2; y++) {
                ret.setRGB(dw+x, dh+y, img.getRGB(x * 2, y * 2));
            }
        }
        return ret;
    }
    public static double getZoom(){
        return (4/(Renderer.toY-Renderer.fromY));
    }
    static void screenshot(){
        File file = new File(
                LocalDate.now()+"_" +
                        LocalTime.now().toString()
                                .replaceAll("\\.", "-")
                                .replaceAll(":", ";")
                        + ".png");
        try {
            ImageIO.write(img, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static double p2cx(int px){
        return ((toX - fromX) * ((double)px/ w))
                + fromX;
    }
    public static double p2cy(int py){
        return ((fromY - toY) * ((double)py / h))
                + toY;
    }
    static int c2px(double coor){
        return (int)((coor-fromX) * w
                /(toX - fromX));
    }
    static int c2py(double coor){
        return (int)((coor-toY) * h
                        / (fromY - toY));
    }
    public static void chooseSet(int set){
        turnOff();
        clearImage();
        img_g.setColor(Color.black);
        img_g.fillRect(0,0, w, h);
        Sets.chooseSet(set);
        turnOn();
    }
    static void move(int dx, int dy){
        turnOff();
        shiftBounds(
                -dx*(toX-fromX)/w,
                dy*(toY-fromY)/h
        );
        clearImage(moveImage(-dx, -dy, img));
        turnOn();
    }
    static void shiftBounds(double dx, double dy){
        fromX += dx;
        toX   += dx;
        fromY += dy;
        toY   += dy;
    }
    static void setBounds(double x1, double x2, double y1, double y2){
        fromX = x1;
        toX   = x2;
        fromY = y1;
        toY   = y2;
    }
    private static BufferedImage moveImage(int dx, int dy, BufferedImage img){
        BufferedImage local = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        local.setData(img.getRaster());
        shiftX(dx, local);
        shiftY(dy, local);
        return local;
    }
    private static void shiftX(int dx, BufferedImage img){
        BufferedImage local = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < w; x++) {
            if(((x+dx) >= 0) && ((x+dx) < w)){
                for (int y = 0; y < h; y++) {
                    local.setRGB(x, y, img.getRGB(x+dx, y));
                }
            }
        }
        img.setData(local.getRaster());
    }
    private static void shiftY(int dy, BufferedImage img){
        BufferedImage local = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < h; y++) {
            if(((y+dy) >= 0) && ((y+dy) < h)){
                for (int x = 0; x < w; x++) {
                    local.setRGB(x, y, img.getRGB(x, y+dy));
                }
            }
        }
        img.setData(local.getRaster());
    }
    public static void turnOff(){
        Calculator.isOn = false;
    }
    public static void turnOn(){
        Calculator.isOn = true;
    }
    static void togglePosition(){
        showPosition ^= true;
    }
    static void toggleAxis(){
        showAxis ^= true;
    }
    static void togglePreCalculation(){
        Calculator.preCalculation ^= true;
    }
    static void togglePinpointPrecision(){
        pinpointPrecision ^= true;
    }
    static boolean isPinpointPrecision(){
        return pinpointPrecision;
    }
    public static double getFromX() {
        return fromX;
    }
    public static double getToX() {
        return toX;
    }
    public static double getFromY() {
        return fromY;
    }
    public static double getToY() {
        return toY;
    }
    public static int getRGB(int x, int y){
        return img.getRGB(x, y);
    }
    static int preCalculateStartingPrecision(){
        int horizontalSpacing = 9;
        int verticalSpacing = 9;
        int max = (int)Math.log10(getZoom()) * 100;
        int p = 0;
        clearCache(1);
        for(; p < max; p++){
            for (int x = 0; x < w; x += horizontalSpacing+1) {
                for (int y = 0; y < h; y += verticalSpacing+1) {
                    if(isDraw(x, y)) return p-1;
                }
            }
        }
        return p-1;
    }
}