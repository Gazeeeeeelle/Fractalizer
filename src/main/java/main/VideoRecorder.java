package main;

import com.sun.glass.ui.Pixels;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;

import java.awt.*;

class VideoRecorder extends Thread{
    static boolean isRec = false;

    @Override
    public void run(){
        try {
            VideoRecorder.record();
        } catch (FrameRecorder.Exception e) {
            throw new RuntimeException(e);
        }
    }
    static void record() throws FrameRecorder.Exception {
        startRecording();
        FrameRecorder recorder = new FFmpegFrameRecorder("output.mp4", Renderer.img.getWidth(), Renderer.img.getHeight());
        recorder.setVideoCodec(avcodec.IDFT_R2C);
        recorder.setFormat("mp4");
        recorder.start();
        Frame f = new Frame(Renderer.img.getWidth(), Renderer.img.getHeight(), 8, 4);

        recorder.setPixelFormat(Pixels.Format.BYTE_ARGB);

        double fr = recorder.getFrameRate();

        for(;;) {
            if(!isRec) break;
            Java2DFrameConverter.copy(Renderer.img, f, 1, true, new Rectangle(new Dimension(Renderer.img.getWidth(), Renderer.img.getHeight())));
            recorder.record(f);
            try {
                Thread.sleep((long) (1000/fr));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        recorder.stop();
        recorder.release();
    }

    static private boolean startRecording(){
        if(!isRec) {
            isRec = true;
            return true;
        }
        return false;
    }
    static boolean stopRecording(){
        if(isRec) {
            isRec = false;
            return true;
        }
        return false;
    }
}
