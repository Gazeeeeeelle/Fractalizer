package main;

import java.awt.event.KeyEvent;
import java.util.Hashtable;

abstract class Controller {
    static Map map;
    static {
        map = new Map()
                .map("G", () -> {
                    if (isCool(200)) {
                        Sets.setZ(0, 0);
                        Renderer.clearImage();
                    }
                })
                .map("F", () -> Sets.setZ(Window.getMousePos()))
                .map("E", () -> {
                    Renderer.special ^= true;
                    Renderer.clearImage();
                })
                .map("O", () -> {
                    Renderer.resetRange();
                    Renderer.clearImage();
                })
                .map("J", () -> {
                    Sets.julia ^= true;
                    Renderer.clearImage();
                })
                .map("L", () -> {
                    Sets.connectLines ^= true;
                    Renderer.clearImage();
                })
                .map("C", () -> {
                    Renderer.centerAtPixel(Window.getMousePos());
                })
                .map("N", () -> {
                    Renderer.night ^= true;
                    Renderer.clearImage();
                })
                .map("I", () -> System.out.println(Sets.getInfo()))
                .map("P", () -> {
                    Renderer.preCalcRange ^= true;
                    Renderer.clearImage();
                })
                .map("S", () -> Renderer.showPosition ^= true)
                .map("V", () -> {
                    if (VideoRecorder.isRec) {
                        VideoRecorder.isRec = false;
                    }else {
                        VideoRecorder vr = new VideoRecorder();
                        vr.start();
                    }
                })
                .map("A", () -> Renderer.axis ^= true)
                .map("Right",  () -> {
                    Sets.mode++;
                    Renderer.clearImage();
                })
                .map("Left", () -> {
                    Sets.mode--;
                    Renderer.clearImage();
                })
                .map("Up", () -> {
                    Colors.shiftColorPalette(1);
                    Renderer.clearImage();
                })
                .map("Down", () -> {
                    Colors.shiftColorPalette(-1);
                    Renderer.clearImage();
                })
                .map("F5", Renderer::clearImage)
                .map("F2", Renderer::screenshot)
        ;
    }
    static void operate(KeyEvent e){
        String code = getCode(e);
        if (map.mappings.get(code) != null) {
            map.mappings.get(code).run();
        }
    }
    static String getCode(KeyEvent e){
        return KeyEvent.getKeyText(e.getKeyCode());
    }

    static class Map {
        Hashtable<String, Operation> mappings = new Hashtable<>();
        Map map(String regex, Operation operation){
            String[] matches = regex.split("\\|");
            for(String match : matches) {
                mappings.put(match, operation);
            }
            return this;
        }
    }
    interface Operation {
        void run();
    }
    static long time = System.currentTimeMillis();
    public static boolean isCool(long c){
        if(System.currentTimeMillis()-time >= c){
            time = System.currentTimeMillis();
            return true;
        }
        return false;
    }
}

