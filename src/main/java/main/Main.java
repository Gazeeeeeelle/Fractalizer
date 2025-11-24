package main;

class Main {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");

        Renderer r = new Renderer();
        r.start();

        InputReceiver inputReceiver = new InputReceiver();
        inputReceiver.start();

        Test test = new Test();
        test.start();
    }
}
