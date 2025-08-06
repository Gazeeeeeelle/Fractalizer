import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FrameRecorder;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Scanner;

class Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s;
                for(;;) {
                    s = scanner.nextLine();
                    int i = Integer.parseInt(s);
                    System.out.println((i-360) % 360);
                }
    }
}
class Test2{
    public static void main(String[] args) {
        System.out.println(Math.hypot(3, 4));
    }
}