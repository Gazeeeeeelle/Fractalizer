import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameRecorder;
import org.opencv.core.Mat;

import java.awt.image.BufferedImage;

class Test {
    public static void main(String[] args) {
        double v = .6;

        int n = 10000000;

        double s;
        double c;
        long time = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            s = Math.sin(v);
            c = Math.cos(v);
        }
        System.out.println((System.currentTimeMillis()-time)+"ms");

        System.out.println();
        System.out.println("s, sqrt");

        time = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            s = Math.sin(v);
            c = Math.sqrt(1-(s*s))*signC(v);
        }
        System.out.println((System.currentTimeMillis()-time)+"ms");

    }
    static int signC(double v){
        double n = (int)(2*v/Math.PI);
        if(n==1||n==2) return -1;
        return 1;
    }
}
class Test2{
    public static void main(String[] args) {
        System.out.println(Math.hypot(3, 4));
    }
}