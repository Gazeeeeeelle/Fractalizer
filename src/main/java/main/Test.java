package main;

public class Test extends Thread{
    @Override
    public void run(){
        int limit = 300;
        long time = System.currentTimeMillis();
        for(;;){
            if(Calculator.calc[0].precision > limit){
                System.out.println(System.currentTimeMillis()-time);
                Renderer.clearImage();
                time = System.currentTimeMillis();
            }
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
