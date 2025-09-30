package main;

abstract class Colors {
    static int whichColorPalette = 0;
    static int[] //Color palettes:
            colors1 = {
            -12444145, -15136998, -16187089, -16513975,
            -16775324, -15979382, -15183183, -13009455,
            -7948827 , -2888456 , -923201  , -472737  ,
            -22016   , -3375104 , -6727936 , -9817085
    },
            greenTone = makeTone(0, 1, 0, 50, 100, 0, 100, true, false),
            iceCoaledTone = makeTone(0, 1, 1, 0, 50, 255, 100, true, false),
            redToYellowTone = makeTone(1, 1, 0, 255, 50, 0, 100, true, false),
            blueToPurpleTone = makeTone(1, 0, 1, 50, 0, 255, 100, true, false),
            greenToYellowTone = makeTone(1, 1, 0, 50, 255, 0, 100, true, false),
            grayTone = makeTone(1, 1, 1, 60, 60, 60, 70, true, false),
            rainbowTone = rainbowMaker(250);
    static int[][] colorPalettes = new int[][]{
            colors1,
            greenTone,
            redToYellowTone,
            blueToPurpleTone,
            iceCoaledTone,
            greenToYellowTone,
            rainbowTone,
            grayTone
    };
    static int getColor(int n){
        return colorPalettes[whichColorPalette][n % colorPalettes[whichColorPalette].length];
    }
    private static int[] makeTone(double r, double g, double b, int minimumR, int minimumG, int minimumB, int size, boolean back, boolean invert){
        minimumR = (int) ((double) minimumR * r);
        minimumG = (int) ((double) minimumG * g);
        minimumB = (int) ((double) minimumB * b);
        int[] colors;
        if(back) {
            colors = new int[(size * 2) - 1];
        }else{
            colors = new int[size];
        }
        final double rk = (r * 255 - minimumR) / size,
                gk = (g * 255 - minimumG) / size,
                bk = (b * 255 - minimumB) / size;
        int rv, gv, bv;
        for (int i = 0; i < size; i++) {
            rv = (int) ((i+1) * rk) + minimumR;
            gv = (int) ((i+1) * gk) + minimumG;
            bv = (int) ((i+1) * bk) + minimumB;
            colors[i] = (rv<<16)+(gv<<8)+(bv);
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
                int t = colors[i];
                colors[i] = colors[colors.length-1-i];
                colors[colors.length-1-i] = t;
            }
        }
        return colors;
    }
    private static int[] rainbowMaker(int size){
        size /= 6;
        final int[][] colors = new int[][]{
                makeTone(1, 1, 0, 255, 0, 0, size, false, false),
                makeTone(1, 1, 0, 0, 255, 0, size, false, true),
                makeTone(0, 1, 1, 0, 255, 0, size, false, false),
                makeTone(0, 1, 1, 0, 0, 255, size, false, true),
                makeTone(1, 0, 1, 0, 0, 255, size, false, false),
                makeTone(1, 0, 1, 255, 0, 0, size, false, true)
        };
        int[] color = new int[size * 6];
        int i = 0;
        for(int[] bc : colors){
            for(int c : bc){
                color[i] = c;
                i++;
            }
        }
        return color;
    }
    static void shiftColorPalette(int d){
        int localPointer = whichColorPalette + d;
        if (localPointer < 0) {
            whichColorPalette = colorPalettes.length - 1;
            return;
        }else if(localPointer > colorPalettes.length - 1){
            whichColorPalette = 0;
            return;
        }
        whichColorPalette = localPointer;
    }
    static int getColorDir (double[] z){
        double arg = Math.toDegrees(ComplexMath.arg1(z[0], z[1]));
        if(arg < 0) arg += 360;
        int r=0,
                g=0,
                b=0;
        if(arg >= 300 || arg <= 60){
            r = 255;
            if(arg >= 300){
                g = (int)(4.25d*(360-arg));
            } else {
                b = (int)(4.25d*(arg));
            }
        } else if(arg >= 60 && arg <= 180){
            b = 255;
            if(arg <= 120){
                r = (int)(4.25d*(120-arg));
            } else {
                g = (int)(4.25d*(arg-120));
            }
        } else {
            g = 255;
            if(arg <= 240){
                b = (int)(4.25d*(240-arg));
            } else {
                r = (int)(4.25d*(arg-240));
            }
        }
        double abs = Math.hypot(z[0], z[1]);
        double i = Sets.topographicStep*100;
        if(abs < i) {
            double k = abs / i;
            r = (int) (r*k);
            g = (int) (g*k);
            b = (int) (b*k);
        }else{
            i = (abs/(Sets.topographicStep*10));
            r += (int)i;
            g += (int)i;
            b += (int)i;
        }
        if(r > 255) r = 255;
        if(g > 255) g = 255;
        if(b > 255) b = 255;
        if(r == 0 && r == g && r == b){
            r = 1;
        }
        return (r<<16)+(g<<8)+(b);
    }
}
