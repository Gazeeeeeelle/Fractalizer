package main;

import complexMath.Sets;

import java.util.Scanner;

public class ZTests extends Thread{
    private static final Scanner scanner = new Scanner(System.in);
    public static void main(String[] args) throws InterruptedException {
        System.out.println("""
                1 - thread testing
                2 - set testing
                """);
        String in = scanner.nextLine();
        switch (in){
            case "1" -> test1(args);
            case "2" -> test2(args);
        }
    }
    private static void test1(String[] args) throws InterruptedException {
        System.out.println("Thread testing");
        Main.main(args);
        int w = 1000;
        int h = 1000;
        int limit = 1000;
        int nt = 11;
        double[] averages = new double[nt-1];
        int nPerTN = 10;
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
    private static void test2(String[] args) throws InterruptedException {
        Main.main(args);
        int limit = 300;
        int nt = 3;
        double[] averages = new double[nt-1];
        int nPerTN = 10;
        long sum = 0;
        Calculator.destroyCalculatorSet();
        Calculator.buildCalculatorSet(1000, 1000, 1, 1);
        for(int t = 1; t < nt; t++){
            for (int i = 0; i < nPerTN; i++) {
                long time = System.currentTimeMillis();
                while(Calculator.calc[0].getPrecision() < limit){Thread.sleep(1);}
                sum += System.currentTimeMillis() - time;
                System.out.println((System.currentTimeMillis() - time)+"ms");
                Renderer.clearImage();
            }
            Sets.setOfInterest++;
            averages[t-1] = (double)sum/nPerTN;
            sum = 0L;
        }
        System.out.println("AVERAGES: ");
        for (int i = 0; i < averages.length; i++) {
            System.out.println("Set " + (i+1) + ": " + averages[i] + "ms");
        }
    }

}
