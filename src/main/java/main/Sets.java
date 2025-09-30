package main;

abstract class Sets {
    static int setOfInterest = 1;
    static double topographicStep = .1;
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
        return switch (setOfInterest) {
            case 1 -> cached_mandelbrot(x, y, index, n);
            case 2 -> cached_burningShip(x, y, index, n);
            case 3 -> cached_celtic(x, y, index, n);
            case 4 -> cached_invMandelbrot(x, y, index, n);
            case 5 -> cached_mandelbrot_short(x, y, index, n);
            default -> false;
        };
    }
    static double[] calc_dist(double x, double y, int set){
        return switch (set) {
            case 1 -> ComplexMath.complexPow(x, y, z1x, z1y);
            case 2 -> ComplexMath.complexPow(x, y, x, y);
            case 3 -> ComplexMath.cos(x, y);
            case 4 -> ComplexMath.sin(x, y);
            case 5 -> ComplexMath.zetaFunction(x, y, 3000);
            case 6 -> new double[]{y, ComplexMath.zetaFunction(x, y, (int)z1x)[0]};
            case 7 -> new double[]{y, ComplexMath.zetaFunction(x, y, (int)z1x)[1]};
            default -> new double[]{0, 0};
        };
    }
    static double[] calc_dir(int pixelX, int pixelY){
        double x = Renderer.p2cx(pixelX);
        double y = Renderer.p2cy(pixelY);
        return switch (setOfInterest) {
            case 1 -> ComplexMath.fibonacci_C(x, y);
            case 2 -> ComplexMath.complexPow(x, y, x, y);
            case 3 -> ComplexMath.cos(x, y);
            case 4 -> ComplexMath.sin(x, y);
            case 5 -> ComplexMath.zetaFunction(x, y, (int)(100*z1x));
            case 6 -> ComplexMath.invZetaFunction(x, y, (int)(100*z1x));
//            case 6 -> ComplexMath.complexPow(x, y, z1x, z1y);
            default -> new double[]{0, 0};
        };
    }
    public static void cleanCache(int index){
        for (int x = 0; x < Window.jpWidth; x++) {
            for (int y = 0; y < Window.jpWidth; y++) {
                if(julia) {
                    cache[index][x][y][0] = Renderer.p2cx(x);
                    cache[index][x][y][1] = Renderer.p2cy(y);
                }else{
                    cache[index][x][y][0] = z1x;
                    cache[index][x][y][1] = z1y;
                }
            }
        }
    }
    public static void cleanCache(int x, int y, int index){
        if(julia) {
            cache[index][x][y][0] = Renderer.p2cx(x);
            cache[index][x][y][1] = Renderer.p2cy(y);
        }else{
            cache[index][x][y][0] = z1x;
            cache[index][x][y][1] = z1y;
        }
    }
    private static boolean cached_mandelbrot(int x, int y, int index, int times) {
        double c1 = z1x;
        double c2 = z1y;
        if (!julia) {
            c1 = Renderer.p2cx(x);
            c2 = Renderer.p2cy(y);
        }
        double z1 = cache[index][x][y][0];
        double z2 = cache[index][x][y][1];
        for (int i = 0; i < times; i++) {
            cache[index][x][y][0] = z1 * z1 - (z2 * z2) + c1;
            cache[index][x][y][1] = (2 * z1 * z2) + c2;
            if (cache[index][x][y][0] * cache[index][x][y][0]
                    + cache[index][x][y][1] * cache[index][x][y][1] > 4) {
                return true;
            }
            z1 = cache[index][x][y][0];
            z2 = cache[index][x][y][1];
        }
        return false;
    }
    public static void main(String[] args) {
        int n = Integer.MAX_VALUE;
        cleanCache(1);
        long time;
        time = System.currentTimeMillis();
        Sets.cached_mandelbrot(400, 440, 1, n);
//        System.out.println(System.currentTimeMillis()-time);
        cleanCache(1);
        time = System.currentTimeMillis();
        Sets.cached_mandelbrot_short(400, 440, 1, n);
        System.out.println(System.currentTimeMillis()-time);
        cleanCache(1);
        time = System.currentTimeMillis();
        Sets.cached_mandelbrot(400, 440, 1, n);
        System.out.println(System.currentTimeMillis()-time);

    }
    private static boolean cached_mandelbrot_short(int x, int y, int index, int times) {
        double c1 = z1x;
        double c2 = z1y;
        if (!julia) {
            c1 = Renderer.p2cx(x);
            c2 = Renderer.p2cy(y);
        }
        double z1 = cache[index][x][y][0];
        for (int i = 0; i < times; i++) {
            cache[index][x][y][0] = z1 * z1 - (cache[index][x][y][1] * cache[index][x][y][1]) + c1;
            cache[index][x][y][1] = (2 * z1 * cache[index][x][y][1]) + c2;
            if (cache[index][x][y][0] * cache[index][x][y][0]
                    + cache[index][x][y][1] * cache[index][x][y][1] > 4) {
                return true;
            }
            z1 = cache[index][x][y][0];
        }
        return false;
    }
    private static boolean test(int x, int y, int index, int times){
        for (int i = 0; i < times; i++) {
            if(q_cached_mandelbrot(x, y, index)) return true;
        }
        return false;
    }
    private static boolean q_cached_mandelbrot(int x, int y, int index){
        double t = cache[index][x][y][0];
        cache[index][x][y][0] = cache[index][x][y][0]*cache[index][x][y][0]
                -(cache[index][x][y][1]*cache[index][x][y][1])+Renderer.p2cx(x);
        cache[index][x][y][1] = (2*t*cache[index][x][y][1])+Renderer.p2cy(y);
        return cache[index][x][y][0]*cache[index][x][y][0]
                + cache[index][x][y][1]*cache[index][x][y][1] > 4;
    }

    private static boolean cached_burningShip(int x, int y, int index, int times){
        double c1 = z1x;
        double c2 = z1y * -1;// "*-1" for flipping vertically
        if(!julia){
            c1 = Renderer.p2cx(x);
            c2 = Renderer.p2cy(y) * -1;// "*-1" for flipping vertically
        }
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
    private static boolean cached_celtic(int x, int y, int index, int times){
        double c1 = z1x;
        double c2 = z1y;
        if(!julia){
            c1 = Renderer.p2cx(x);
            c2 = Renderer.p2cy(y);
        }
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
    private static boolean cached_invMandelbrot(int x, int y, int index, int times){
        double c1 = z1x;
        double c2 = z1y;
        if(!julia){
            c1 = Renderer.p2cx(x);
            c2 = Renderer.p2cy(y);
        }
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
    static String getInfo(){
        String ret = "";
        ret += "----------------------------------------------------------------"+"\n";
        ret += (Renderer.fromX+" -> "+Renderer.toX+", "+Renderer.fromY+"i -> "+Renderer.toY+"i"+"\n");
        ret += ("zoom: "+ Renderer.getZoom()+"\n");
        if(Sets.mode == Sets.FRACTAL) {
            ret += ("Set: " +
                    switch (Sets.setOfInterest) {
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


