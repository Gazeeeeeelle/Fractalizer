package main;

abstract class Sets {
    static int setOfInterest = 1;
    static final int FRACTAL = 1,
                     ABS = 2,
                     DIR = 3;
    static int mode = FRACTAL;
    static boolean
                julia = false,
                inverse = false,
                connectLines = false;
    static double topographicStep = .1;
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
            case 5 -> cached_power_brot(x, y, index, n);
            case 6 -> cached_gahbrot(x, y, index, n);
            default -> false;
        };
    }
    static double[] calc_dir(double x, double y, int set){
        return switch (set) {
            case 1 -> ComplexMath.complexPow(x, y, z1x, z1y);
            case 2 -> ComplexMath.complexPow(x, y, x, y);
            case 3 -> ComplexMath.cos(x, y);
            case 4 -> ComplexMath.sin(x, y);
            default -> new double[]{0, 0};
        };
    }
    static boolean calc_abs(int x, int y, int index, int nth){
        switch (setOfInterest) {
            case 1 -> ComplexMath.fibonacci_C(x, y, index);
            case 2 -> ComplexMath.zetaFunction(x, y, index, nth);
            case 3 -> ComplexMath.invZetaFunction(x, y, index, nth);
        }
        return true;
    }
    public static void cleanCache(int index){
        for (int x = 0; x < Window.jpWidth; x++) {
            for (int y = 0; y < Window.jpWidth; y++) {
                if(julia && (Sets.mode == Sets.FRACTAL)) {
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
        if(julia && (Sets.mode == Sets.FRACTAL)) {
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
        for (int i = 0; i < times; i++) {
            if(mandelbrot(x, y, c1, c2, index)) return true;
            if(inverse) ComplexMath.s_inverse(x, y, index);
        }
        return false;
    }
    private static boolean mandelbrot(int x, int y, double c1, double c2, int index){
        double z1 = cache[index][x][y][0];
        double z2 = cache[index][x][y][1];
        cache[index][x][y][0] = z1 * z1 - (z2 * z2) + c1;
        cache[index][x][y][1] = (2 * z1 * z2) + c2;
        return (cache[index][x][y][0] * cache[index][x][y][0]
                + cache[index][x][y][1] * cache[index][x][y][1] > 4);
    }
    private static boolean cached_inverse(int x, int y, int index) {
        double c1 = z1x;
        double c2 = z1y;
        if (!julia) {
            c1 = Renderer.p2cx(x);
            c2 = Renderer.p2cy(y);
        }
        mandelbrot(x,y, c1, c2, index);
        ComplexMath.s_inverse(x, y, index);
        return false;
    }
    private static boolean cached_power_brot(int x, int y, int index, int times) {
        double c1 = z1x;
        double c2 = z1y;
        if (!julia) {
            c1 = Renderer.p2cx(x);
            c2 = Renderer.p2cy(y);
        }
        double z1, z2;
        for (int i = 0; i < times; i++) {
            z1 = cache[index][x][y][0];
            z2 = cache[index][x][y][1];
            cache[index][x][y] = ComplexMath.complexPow(z1, z2, 4, 0);
            cache[index][x][y][0] += c1;
            cache[index][x][y][1] += c2;
            if (cache[index][x][y][0] * cache[index][x][y][0]
                    + cache[index][x][y][1] * cache[index][x][y][1] > 4) {
                return true;
            }
        }
        return false;
    }
    private static boolean cached_gahbrot(int x, int y, int index, int times) {
        //re(sqrt(z^a))+i*im(sqrt(z^b))+c
        double c1 = Renderer.p2cx(x);
        double c2 = Renderer.p2cy(y);
        double z1, z2;
        double a = z1x;
        double b = z1y;
        for (int i = 0; i < times; i++) {
            z1 = cache[index][x][y][0];
            z2 = cache[index][x][y][1];
            cache[index][x][y][0] = ComplexMath.complexPow(z1, z2, a, 0)[0] + c1;
            cache[index][x][y][1] = ComplexMath.complexPow(z1, z2, b, 0)[1] + c2;
            if (cache[index][x][y][0] * cache[index][x][y][0]
                    + cache[index][x][y][1] * cache[index][x][y][1] > 4) {
                return true;
            }
        }
        return false;
    }
    private static boolean cached_burningShip(int x, int y, int index, int times){
        double c1 = z1x;
        double c2 = z1y * -1;// "*-1" for flipping vertically
        if(!julia){
            c1 = Renderer.p2cx(x);
            c2 = Renderer.p2cy(y) * -1;// "*-1" for flipping vertically
        }
        for (int i = 0; i < times; i++) {
            if(burningShip(x, y, c1, c2, index)) return true;
            if(inverse) ComplexMath.s_inverse(x, y, index);
        }
        return false;
    }
    private static boolean burningShip(int x, int y, double c1, double c2, int index){
        double z1 = Math.abs(cache[index][x][y][0]);
        double z2 = Math.abs(cache[index][x][y][1]);
        z1 = (z1 * z1 - z2 * z2) + c1;
        z2 = (2 * Math.abs(cache[index][x][y][0] * z2)) + c2;
        cache[index][x][y][0] = z1;
        cache[index][x][y][1] = z2;
        return (z1 * z1 + z2 * z2 > 4);
    }
    private static boolean cached_celtic(int x, int y, int index, int times){
        double c1 = z1x;
        double c2 = z1y;
        if(!julia){
            c1 = Renderer.p2cx(x);
            c2 = Renderer.p2cy(y);
        }
        for (int i = 0; i < times; i++) {
            if(celtic(x, y, c1, c2, index)) return true;
            if(inverse) ComplexMath.s_inverse(x, y, index);
        }
        return false;
    }
    private static boolean celtic(int x, int y, double c1, double c2, int index){
        double z2 = Math.abs((cache[index][x][y][0]*cache[index][x][y][0]) - (cache[index][x][y][1]*cache[index][x][y][1])) + c1;
        double z1 = cache[index][x][y][0] * cache[index][x][y][1] * 2 + c2;
        cache[index][x][y][0] = z1;
        cache[index][x][y][1] = z2;
        return (z1 * z1 + z2 * z2 > 4);
    }
    private static boolean cached_invMandelbrot(int x, int y, int index, int times){
        double c1 = z1x;
        double c2 = z1y;
        if(!julia){
            c1 = Renderer.p2cx(x);
            c2 = Renderer.p2cy(y);
        }
        double abs2 = c1 * c1 + c2 * c2;
        c1 = c1 / abs2;
        c2 = -c2 / abs2;
        for (int i = 0; i < times; i++) {
            if(mandelbrot(x, y, c1, c2, index)) return true;
            if(inverse) ComplexMath.s_inverse(x, y, index);
        }
        return false;
    }
    public static void setZ1(double... z){
        assert z.length == 2;
        Sets.z1x = z[0];
        Sets.z1y = z[1];
    }
    public static void setZPixel(int[] pixelPosition){
        if (Controller.isCool(200)) {
            try {
                Sets.setZ1(
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
                        case 5 -> "Power Set";
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
    //-> cached_mandelbrot(x, y, index, n);
    //            case 2 -> cached_burningShip(x, y, index, n);
    //            case 3 -> cached_celtic(x, y, index, n);
    //            case 4 -> cached_invMandelbrot(x, y, index, n);
    //            case 5 -> cached_gahbrot(x, y, index, n);
}



