package main;

class ComplexMath {
    static double[] zetaFunction(double a, double b, int depth){
        double[] ret = new double[]{0,0};
        //This gets the region that diverges to infinity off
        if(a<1) return ret;
        double ka;
        double blnk;
        double s;
        for (int k = 1; k < depth+1; k++) {
            ka = Math.pow(k, -a);
            blnk = b*Math.log(k);
            s = Math.sin(blnk);
            ret[0] += ka*Math.sqrt(1-s*s)*signC(blnk);
            ret[1] -= ka*s;
        }
        return ret;
    }
    static double[] zetaFunctionQ(double a, double b, double c, double d, int depth){
        double[] ret = new double[]{0,0};
        double ka;
        double blnk;
        double clnk;
        double dlnk;

        a = -a;
        b = -b;
        c = -c;
        d = -d;
        for (int k = 1; k < depth+1; k++) {
            ka = Math.pow(k, a);
            blnk = b*Math.log(k);
            clnk = c*Math.log(k);
            dlnk = d*Math.log(k);
            ret[0] += ka*
                    (
                            Math.cos(blnk)*Math.cos(clnk)*Math.cos(dlnk)
                            -Math.sin(blnk)*Math.sin(clnk)*Math.sin(dlnk)
                    );
            ret[1] -= ka*
                    (
                            Math.sin(blnk)*Math.cos(clnk)*Math.cos(dlnk)
                                    -Math.cos(blnk)*Math.sin(clnk)*Math.sin(dlnk)
                    );;
        }
        return ret;
    }
    private static int signC(double v){
        v %= (2*Math.PI);
        int n = (int)(2*v/Math.PI);
        if(n==1||n==2||n==-1||n==-2) return -1;
        return 1;
    }
    static double[] fibonacci_C(double a, double b){
        double sqrt5 = Math.sqrt(5);
        double inv_sqrt5 = 1/sqrt5;
        double gamma = 1/2d*(1+sqrt5);
        double absDelta = gamma-1;
        double gammaA = Math.pow(gamma, a);
        double deltaA = Math.pow(absDelta, a);
        double lnGamma = Math.log(gamma);
        double lnDelta = Math.log(absDelta);
        double ebPi = Math.exp(-Math.PI*b);
        double phi1 = b*lnGamma;
        double phi2 = b*lnDelta+Math.PI*a;
        return new double[]{
                inv_sqrt5*(gammaA*Math.cos(phi1)-deltaA*ebPi*Math.cos(phi2)),
                inv_sqrt5*(gammaA*Math.sin(phi1)-deltaA*ebPi*Math.sin(phi2))
        };
    }
    static double[] complexLog1(double a, double b){
        double lnAbs = Math.log(abs(a, b));
        double arg = arg1(a, b);
        return new double[]{
                lnAbs,
                arg
        };
    }
    static double[] complexLog2(double a, double b){
        double abs = abs(a, b);
        double lnAbs = Math.log(abs);
        double cs = a/abs;
        double sn = b/abs;
        return new double[]{
                cs*lnAbs,
                sn*lnAbs
        };
    }
    static double[] sin(double a, double b){
        return new double[]{
                Math.cosh(b)*Math.sin(a),
                Math.cos(a)*Math.sinh(b)
        };
    }
    static double[] cos(double a, double b){
        return new double[]{
                Math.cosh(b)*Math.cos(a),
                -Math.sin(a)*Math.sinh(b)
        };
    }
    static double arg1(double a, double b){
        return Math.atan2(b, a);
    }
    static double arg2(double a, double b){
        return Math.atan(b/a);
    }
    static double abs(double a, double b){
        return Math.sqrt(a*a+b*b);
    }
    static double abs(double[] c){
        return Math.sqrt(c[0]*c[0]+c[1]*c[1]);
    }
    static double absComplexPow(double rho, double ina, double R, double I){
        double absDelta = abs(rho, ina);
        double argDelta = arg1(rho, ina);
        return Math.pow(absDelta, R)*Math.exp(-I*argDelta);
    }
    static double psi_delta_R(double rho, double ina, double R, double I){
        double absDelta = abs(rho, ina);
        double argDelta = arg1(rho, ina);
        return Math.pow(absDelta, R)*Math.exp(-I*argDelta);
    }
    static double[] psi(double rho, double ina, double R, double I){
        double pref1 = Math.pow(abs(rho, ina), arg1(R, I));
        double args = arg1(R, I)*arg1(rho, ina);
        return new double[]{
                pref1*Math.cos(args),
                pref1*Math.sin(args),
        };
    }
    static double[] complexPow(double rho, double ina, double R, double I){
        double absDelta = abs(rho, ina);
        double argDelta = arg1(rho, ina);
        double pref1 = Math.pow(absDelta, R)*Math.exp(-I*argDelta);
        double phi1 = I*Math.log(absDelta);
        double phi2 = R*argDelta;
        double s = Math.sin(phi1 + phi2);
        return new double[]{
                pref1*Math.sqrt(1-s*s)*signC(phi1 + phi2),
                pref1*s
        };
    }
    //Computation of GammaFunction;
}
