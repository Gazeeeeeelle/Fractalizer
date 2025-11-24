package main;

class Main {
    public static void main(String[] args) {
        System.setProperty("sun.java2d.uiScale", "1.0");
        Window window = new Window();

        Renderer r = new Renderer();
        r.start();

        InputReceiver inputReceiver = new InputReceiver();
        inputReceiver.start();
    }
}
