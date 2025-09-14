package main;

abstract class Sets {
    static final int FRACTAL = 1,
                     ABS = 2,
                     DIST = 3;
    static int mode = FRACTAL;
    static boolean
                julia = false,
                connectLines = false;
    static double z1x = 0.0, z1y = 0.0;
    static int solN = 100;
    static double[][][][] cache = new double[2][Window.jpWidth][Window.jpHeight][2];
    static {
        cleanCache(0);
        cleanCache(1);
    }
    static boolean calc_frac(int x, int y, int index, int n){
        return switch (Renderer.setOfInterest) {
            case 1 ->
                    cached_mandelbrotFunction(x, y, index, n);
            case 2 ->
                    cached_burningShipFunction(x, y, index, n);
            case 3 ->
                    cached_celticFunction(x, y, index, n);
            case 4 ->
                    cached_qMandelbrotFunction(x, y, index, n);
            default -> false;
        };
    }
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
                    ComplexMath.zetaFunction(x, y, 3000);
            case 6 ->
                    new double[]{y, ComplexMath.zetaFunction(x, y, (int)z1x)[0]};
            case 7 ->
                    new double[]{y, ComplexMath.zetaFunction(x, y, (int)z1x)[1]};
            default -> new double[]{0, 0};
        };
    }
    static double[] calc_dir(int pixelX, int pixelY){
        double x = Renderer.p2cx(pixelX);
        double y = Renderer.p2cy(pixelY);
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
    public static void cleanCache(int index){
        for (int x = 0; x < Window.jpWidth; x++) {
            for (int y = 0; y < Window.jpWidth; y++) {
                cache[index][x][y][0] = z1x;
                cache[index][x][y][1] = z1y;
            }
        }
    }
    private static boolean cached_mandelbrotFunction(int x, int y, int index, int times){
        double c1 = Renderer.p2cx(x);
        double c2 = Renderer.p2cy(y);
        double z1 = cache[index][x][y][0];
        double z2 = cache[index][x][y][1];
        for (int i = 0; i < times; i++) {
            z1 = z1*z1-(z2*z2)+c1;
            z2 = (2*cache[index][x][y][0]*z2)+c2;
            if(z1*z1 + z2*z2 > 4) {
                return true;
            }
            cache[index][x][y][0] = z1;
            cache[index][x][y][1] = z2;
        }
        return false;
    }
    private static boolean cached_burningShipFunction(int x, int y, int index, int times){
        double c1 = Renderer.p2cx(x);
        double c2 = Renderer.p2cy(y) * -1;// "*-1" for flipping vertically
        double z1 = cache[index][x][y][0];
        double z2 = cache[index][x][y][1];
        for (int i = 0; i < times; i++) {
            z1 = Math.abs(z1);
            z2 = Math.abs(z2);
            z1 = (z1 * z1 - z2 * z2) + c1;
            z2 = (2 * Math.abs(cache[index][x][y][0] * z2)) + c2;
            if (z1 * z1 + z2 * z2 > 4) return true;
            cache[index][x][y][0] = z1;
            cache[index][x][y][1] = z2;
        }
        return false;
    }
    //double k;
    //        for(;;) {
    //            k = z2;
    //            z2 = Math.abs((z1*z1) - (z2*z2)) + c1;
    //            z1 = z1 * k * 2 + c2;
    //            if(z1*z1 + z2*z2 > 4) return true;
    //            if(ite >= precision) return false;
    //            ite++;
    //        }
    private static boolean cached_celticFunction(int x, int y, int index, int times){
        double c1 = Renderer.p2cx(x);
        double c2 = Renderer.p2cy(y);
        double z1 = cache[index][x][y][0];
        double z2 = cache[index][x][y][1];
        for (int i = 0; i < times; i++) {
            z2 = Math.abs((z1*z1) - (z2*z2)) + c1;
            z1 = z1 * cache[index][x][y][1] * 2 + c2;
            if (z1 * z1 + z2 * z2 > 4) return true;
            cache[index][x][y][0] = z1;
            cache[index][x][y][1] = z2;
        }
        return false;
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
    private static boolean cached_qMandelbrotFunction(int x, int y, int index, int times){
        double c1 = Renderer.p2cx(x);
        double c2 = Renderer.p2cy(y);
        double z1 = cache[index][x][y][0];
        double z2 = cache[index][x][y][1];
        double abs2 = c1 * c1 + c2 * c2;
        c1 = c1 / abs2;
        c2 = -c2 / abs2;
        for (int i = 0; i < times; i++) {
            z1 = z1 * z1 - (z2 * z2) + c1;
            z2 = (2 * cache[index][x][y][0] * z2) + c2;
            if (z1 * z1 + z2 * z2 > 4) return true;
            cache[index][x][y][0] = z1;
            cache[index][x][y][1] = z2;
        }
        return false;
    }
    private static boolean qMandelFunction(double z1, double z2, double c1, double c2, int precision, int ite) {
        double abs2 = c1 * c1 + c2 * c2;
        c1 = c1 / abs2;
        c2 = -c2 / abs2;
        double t;
        for (; ; ) {
            t = z1;
            z1 = z1 * z1 - (z2 * z2) + c1;
            z2 = (2 * t * z2) + c2;
            if (z1 * z1 + z2 * z2 > 4) return true;
            if (ite >= precision) return false;
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
    public static void setZ(double a, double b){
        Sets.z1x = a;
        Sets.z1y = b;
    }
    public static void setZR(double a){
        Sets.z1x = a;
    }
    public static void setZI(double b){
        Sets.z1y = b;
    }
    public static void setZ(int[] pixelPosition){
        if (Controller.isCool(200)) {
            try {
                Sets.setZ(
                        Renderer.p2cx(pixelPosition[0]),
                        Renderer.p2cy(pixelPosition[1])
                );
                Renderer.clearImage();
            } catch (NullPointerException exception){
                //pass
            }
        }
    }
}


