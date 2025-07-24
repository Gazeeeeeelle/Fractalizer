package main;

abstract class Sets {
    static final int FRACTAL = 1;
    static final int ABS = 2;
    static final int DIST = 3;
    static boolean julia = false;
    static double z1x, z1y = 0.0;
    static int solN = 100;
    static int mode = FRACTAL;
    static boolean connectLines = false;
    static double[] calc_dist(double x, double y, int set){
        return switch (set) {
            case 1 ->
                    ComplexMath.complexPow(x, y, z1x, z1y);
            case 2 ->
                    ComplexMath.complexPow(x, y, x, y);
            case 3 ->
                    ComplexMath.cos(x, y);
            case 4 ->
                    ComplexMath.sin(x, y);
            case 5 ->
                    ComplexMath.zetaFunction(x, y, 100000);
            case 6 ->
                    new double[]{y, ComplexMath.zetaFunction(x, y, (int)z1x)[0]};
            case 7 ->
                    new double[]{y, ComplexMath.zetaFunction(x, y, (int)z1x)[1]};
            default -> new double[]{0, 0};
        };
    }
    static double calc_abs(int pixelX, int pixelY){
        double x = Renderer.pxToCoorX(pixelX);
        double y = Renderer.pxToCoorY(pixelY);
        return switch (Renderer.setOfInterest) {
            case 1 ->
                    plot_fibonacci_abs(x, y);
            case 2 ->
                    plot_zz(x, y);
            case 3 ->
                    ComplexMath.abs(ComplexMath.cos(x, y));
            case 4 ->
                    ComplexMath.abs(ComplexMath.sin(x, y));
            case 5 ->
                    ComplexMath.absComplexPow(x, y, z1x, z1y);
            case 6 ->
                    ComplexMath.abs(ComplexMath.zetaFunction(x, y, 50));
            case 7 ->
                    ComplexMath.zetaFunction(x, y, 50)[0];
            case 8 ->
                    ComplexMath.zetaFunction(x, y, 50)[1];
            default -> 0;
        };
    }
    static double[] calc_dir(int pixelX, int pixelY){
        double x = Renderer.pxToCoorX(pixelX);
        double y = Renderer.pxToCoorY(pixelY);
        return switch (Renderer.setOfInterest) {
            case 1 ->
                    ComplexMath.fibonacci_C(x, y);
            case 2 ->
                    ComplexMath.complexPow(x, y, x, y);
            case 3 ->
                    ComplexMath.cos(x, y);
            case 4 ->
                    ComplexMath.sin(x, y);
            case 5 ->
                    ComplexMath.zetaFunction(x, y, 300);
            case 6 ->
                    ComplexMath.complexPow(x, y, z1x, z1y);
            default -> new double[]{0, 0};
        };
    }
    static boolean calc_frac(int pixelX, int pixelY, int precision){
        return !julia ?
                switch (Renderer.setOfInterest) {
                    case 1 ->
                            mandelbrotFunction(z1x, z1y, Renderer.pxToCoorX(pixelX), Renderer.pxToCoorY(pixelY), precision, 0);
                    case 2 ->
                            burningShipFunction(z1x, z1y, Renderer.pxToCoorX(pixelX), Renderer.pxToCoorY(pixelY), precision, 0);
                    case 3 ->
                            celticFunction(z1x, z1y, Renderer.pxToCoorX(pixelX), Renderer.pxToCoorY(pixelY), precision, 0);
                    case 4 ->
                            qMandelFunction(z1x, z1y, Renderer.pxToCoorX(pixelX), Renderer.pxToCoorY(pixelY), precision, 0);
                    default -> false;
                }
                :
                switch (Renderer.setOfInterest) {
                    case 1 ->
                            mandelbrotFunction(Renderer.pxToCoorX(pixelX), Renderer.pxToCoorY(pixelY), z1x, z1y, precision, 0);
                    case 2 ->
                            burningShipFunction(Renderer.pxToCoorX(pixelX), Renderer.pxToCoorY(pixelY), z1x, z1y, precision, 0);
                    case 3 ->
                            celticFunction(Renderer.pxToCoorX(pixelX), Renderer.pxToCoorY(pixelY), z1x, z1y, precision, 0);
                    case 4 ->
                            qMandelFunction(Renderer.pxToCoorX(pixelX), Renderer.pxToCoorY(pixelY), z1x, z1y, precision, 0);
                    default -> false;
                };
    }
    static double plot_fibonacci_abs(double x, double y){
        double[] c = ComplexMath.fibonacci_C(x, y);
        return ComplexMath.abs(c);
    }
    static double plot_zz(double x, double y){
        return ComplexMath.absComplexPow(x, y, x, y);
    }
    private static boolean mandelbrotFunction(double z1, double z2, double c1, double c2, int precision, int ite){
        double t;
        for(;;) {
            t = z1;
            z1 = z1*z1-(z2*z2)+c1;
            z2 = (2*t*z2)+c2;
            if(z1*z1 + z2*z2 > 4) return true;
            if(ite >= precision) return false;
            ite++;
        }
    }
    private static boolean qMandelFunction(double z1, double z2, double c1, double c2, int precision, int ite) {
        double abs2 = c1*c1+c2*c2;
        c1 = c1/abs2;
        c2 = -c2/abs2;
        double t;
        for(;;) {
            t = z1;
            z1 = z1*z1-(z2*z2)+c1;
            z2 = (2*t*z2)+c2;
            if(z1*z1 + z2*z2 > 4) return true;
            if(ite >= precision) return false;
            ite++;
        }
    }
//    private static boolean qMandelFunction(double z1, double z2, double c1, double c2, int precision, int ite){
//        double arg;
//        double abs;
//        double p1 = z1x;
//        double t;
//        for(;;) {
//            arg = p1 * ComplexMath.arg1(z1, z2);
//            abs = ComplexMath.abs(z1, z2);
//            t = z2/abs;
//
//            abs = Math.pow(abs, p1);
//
//            z2 = t*abs+c2;
//            z1 = signC(arg)*Math.sqrt(1-t*t)*abs+c1;
//
//            I saw this... B?
//            if(z1*z1 + z2*z2 > 4) return true;
//            if(ite >= precision) return false;
//            ite++;
//        }
//    }
    private static int signC(double v){
        double n = (int)(2*v/Math.PI);
        if(n==1||n==-1||n==2||n==-2) return -1;
        return 1;
    }
    private static boolean burningShipFunction(double z1, double z2, double c1, double c2, int precision, int ite){
        double k;
        for(;;) {
            k = z1 = Math.abs(z1);
            z2 = Math.abs(z2);
            z1 = (z1*z1-z2*z2)+c1;
            z2 = (2*k*z2)+c2;
            if(z1*z1 + z2*z2 > 4) return true;
            if(ite >= precision) return false;
            ite++;
        }
    }
    private static boolean celticFunction(double z1, double z2, double c1, double c2, int precision, int ite){
        double k;
        for(;;) {
            k = z1;
            z1 = z1 * z2 * 2 + c2;
            z2 = Math.abs((k*k) - (z2*z2)) + c1;
            if(z1*z1 + z2*z2 > 4) return true;
            if(ite >= precision) return false;
            ite++;
        }
    }
}


