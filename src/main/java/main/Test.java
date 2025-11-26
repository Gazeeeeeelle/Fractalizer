package main;

public class Test extends Thread{
    public static void main(String[] args) throws InterruptedException {
        Main.main(args);
        int w = 1000;
        int h = 1000;
        int limit = 300;
        int nt = 11;
        double[] averages = new double[nt-1];
        int nPerTN = 30;
        long sum = 0;
        for(int t = 1; t < nt; t++){
            Calculator.destroyCalculatorSet();
            Calculator.buildCalculatorSet(w, h, t, 10);
            for (int i = 0; i < nPerTN; i++) {
                long time = System.currentTimeMillis();
                while(Calculator.calc[0].getPrecision() < limit){Thread.sleep(1);}
                sum += System.currentTimeMillis() - time;
                System.out.println((System.currentTimeMillis() - time)+"ms");
                Renderer.clearImage();
            }
            averages[t-1] = (double)sum/nPerTN;
            sum = 0L;
        }
        System.out.println("AVERAGES: ");
        for (int i = 0; i < averages.length; i++) {
            System.out.println((i+1)+" threads: " + averages[i] + "ms");
        }
    }
}
