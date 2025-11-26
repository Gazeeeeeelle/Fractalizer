package complexMath;

import main.Renderer;

public final class Sets {
    private Sets(){}
    public static int setOfInterest = 1;
    public static Mode mode = Mode.FRACTAL;
    private static boolean
                julia = false,
                inverse = false;
    public static boolean connectLines = false;
    public static double topographicStep = .1;
    static double z1x = 0.0, z1y = 0.0;
    public static int solN = 100;
    private static final double[][][] reference = new double[Renderer.w][Renderer.h][2];
    static double[][][][] cache =
            new double[2][Renderer.w][Renderer.h][2]; //FIXME cache should be at the calculator
    static {
        populateReverence(z1x, z1y); //reference
        clearCache(0, 1);
    }
    //Complex functions for rendering fractals
    private static final IComplexFunction //Treating as a "Fractal" class is too slow.
    mandelbrot = (x, y, c1, c2, i) -> {
        double z1 = cache[i][x][y][0];
        cache[i][x][y][0] = z1 * z1 - (cache[i][x][y][1] * cache[i][x][y][1]) + c1;
        cache[i][x][y][1] = (2 * z1 * cache[i][x][y][1]) + c2;
        return (cache[i][x][y][0] * cache[i][x][y][0]
                + cache[i][x][y][1] * cache[i][x][y][1] > 4);
    },
    burningShip = (x, y, c1, c2, i) -> {
        c2 = -c2;
        double z1 = Math.abs(cache[i][x][y][0]);
        double z2 = Math.abs(cache[i][x][y][1]);
        z1 = (z1 * z1 - z2 * z2) + c1;
        z2 = (2 * Math.abs(cache[i][x][y][0] * z2)) + c2;
        cache[i][x][y][0] = z1;
        cache[i][x][y][1] = z2;
        return (z1 * z1 + z2 * z2 > 4);
    },
    celtic = (x, y, c1, c2, i) -> {
        double z1 = cache[i][x][y][0] * cache[i][x][y][1] * 2 + c2;
        double z2 = Math.abs(cache[i][x][y][0]*cache[i][x][y][0] - cache[i][x][y][1]*cache[i][x][y][1]) + c1;
        cache[i][x][y][0] = z1;
        cache[i][x][y][1] = z2;
        return (z1 * z1 + z2 * z2 > 4);
    },
    powerBrot = (x, y, c1, c2, i) -> {
        double z1 = cache[i][x][y][0];
        double z2 = cache[i][x][y][1];
        cache[i][x][y] = ComplexMath.complexPow(z1, z2, 4, 0);
        cache[i][x][y][0] += c1;
        cache[i][x][y][1] += c2;
        return (cache[i][x][y][0] * cache[i][x][y][0]
                + cache[i][x][y][1] * cache[i][x][y][1] > 4);
    };
    private static final IComplexFunction[] sets = new IComplexFunction[]{null, mandelbrot, burningShip, celtic, powerBrot};

    public static boolean calc_frac(int x, int y, int index, int n){
        return cached_generalized(sets[setOfInterest], x, y, index, n);
    }
    //TODO evaluate: Should there be a calculate function that receives Sets.mode to then decide which to call?
    //I don't think that would impact performance at all if done. That is because the process of "selecting" which mode
    //it is going to operate would just be transferred from Calculator.java to Sets.java
    public static double[] calc_dir(double x, double y, int set){
        return switch (set) {
            case 1 -> ComplexMath.complexPow(x, y, z1x, z1y);
            case 2 -> ComplexMath.complexPow(x, y, x, y);
            case 3 -> ComplexMath.cos(x, y);
            case 4 -> ComplexMath.sin(x, y);
            default -> new double[]{0, 0};
        };
    }
    public static boolean calc_abs(int x, int y, int index, int nth){
        switch (setOfInterest) {
            case 1 -> ComplexMath.fibonacci_C(x, y, index);
            case 2 -> ComplexMath.zetaFunction(x, y, index, nth);
            case 3 -> ComplexMath.invZetaFunction(x, y, index, nth);
        }
        return true;
    }
    private static boolean cached_generalized(IComplexFunction fr, int x, int y, int index, int times){
        double c1 = z1x;
        double c2 = z1y;
        if (!julia) {
            c1 = Renderer.p2cx(x);
            c2 = Renderer.p2cy(y);
        }
        for (int i = 0; i < times; i++) {
            if(fr.cf(x, y, c1, c2, index)) return true;
            if(inverse) ComplexMath.s_inverse(x, y, index);
        }
        return false;
    }
    public static void clearCache(int... indexes){
        for(int index : indexes) {
            int width = cache[index].length;
            int height = cache[index][0].length;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    clearCache(x, y, index);
                }
            }
        }
    }
    public static void clearCache(int x, int y, int index){
        if(julia) populateReverenceJulia(x, y);
        cache[index][x][y][0] = reference[x][y][0];
        cache[index][x][y][1] = reference[x][y][1];
    }
    private static void populateReverence(double... z){
        int width = reference.length;
        int height = reference[0].length;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                reference[x][y][0] = z[0];
                reference[x][y][1] = z[1];
            }
        }
    }
    private static void populateReverenceJulia(){
        int width = reference.length;
        int height = reference[0].length;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                populateReverenceJulia(x, y);
            }
        }
    }
    private static void populateReverenceJulia(int x, int y){
        reference[x][y][0] = Renderer.p2cx(x);
        reference[x][y][1] = Renderer.p2cy(y);
    }
    public static void setZ1(Double z1, Double z2){
        if(z1!=null) Sets.z1x = z1;
        if(z2!=null) Sets.z1y = z2;
        populate(z1x, z1y);
        Renderer.clearImage();
    }
    public static void populate(double z1, double z2){
        if(isJulia()){
            populateReverenceJulia();
        }else{
            populateReverence(z1, z2);
        }
    }
    public static void setZPixel(int[] pixelPosition){
        if(pixelPosition == null) return;
        Sets.setZ1(
                Renderer.p2cx(pixelPosition[0]),
                Renderer.p2cy(pixelPosition[1])
        );
        Renderer.clearImage();
    }
    public static String getInfo(){
        String name = "";
        if(Sets.mode == Mode.FRACTAL) {
            name = switch (Sets.setOfInterest) {
                        case 1 -> "Mandelbrot Set";
                        case 2 -> "Burning Ship Set";
                        case 3 -> "Celtic Set";
                        case 4 -> "Inverse Mandelbrot Set";
                        case 5 -> "Power Set";
                        default -> "Set not found.";
            };
        }
        String line = "-".repeat(63);
        return """
                %s
                Set: %s
                Coor: %f -> %f, %fi -> %fi
                Zoom: %f
                Z0: %f + %fi
                %s
                """.formatted(
                line,
                name,
                Renderer.getFromX(), Renderer.getToX(), Renderer.getFromY(), Renderer.getToY(),
                Renderer.getZoom(),
                Sets.z1x, Sets.z1y,
                line
        );
    }
    public static boolean isJulia() {
        return julia;
    }
    static boolean isInverse() {
        return inverse;
    }
    public static void toggleInverse(){
        inverse^=true;
        Renderer.clearImage();
    }
    public static void toggleJulia(){
        Renderer.turnOff();
        julia^=true;
        if(julia){
            populateReverenceJulia();
        }else{
            populateReverence(z1x, z1y);
        }
        Renderer.clearImage();

    }
    public static void chooseSet(int set){
        if(set > 0 && set <= sets.length){
            setOfInterest = set;
        }
    }
    public static double[] getFromCache(int x, int y, int index){
        return cache[index][x][y];
    }
    public static void nextMode(){
        Mode[] modes = Mode.values();
        int i = Sets.mode.ordinal();
        if(i < modes.length - 1){
            Sets.mode = modes[i + 1];
            Renderer.clearImage();
        }
    }
    public static void previousMode(){
        Mode[] modes = Mode.values();
        int i = Sets.mode.ordinal();
        if(i > 0){
            Sets.mode = modes[i - 1];
            Renderer.clearImage();
        }
    }
    public static void resetTopographicStep(){
        topographicStep = .1;
    }
}