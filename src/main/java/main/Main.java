package main;

class Main {
    public static void main(String[] args) {
        Window window = new Window();

        Renderer r = new Renderer();
        r.start();

        InputReceiver inputReceiver = new InputReceiver();
        inputReceiver.start();
    }
}
