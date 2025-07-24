package main;

class Main {
    public static void main(String[] args) {
        Window window = new Window();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Renderer r = new Renderer();
        r.start();

        InputReceiver inputReceiver = new InputReceiver();
        inputReceiver.setDaemon(true);
        inputReceiver.start();
    }
}
