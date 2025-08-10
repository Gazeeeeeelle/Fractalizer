package main;

import java.awt.event.KeyEvent;
import java.util.Hashtable;

abstract class Control {
    static Mapping mapping;
    static {
        mapping = new Mapping()
                .map("G", () -> {
                    if (isCool(200)) {
                        try {
                            Sets.z1x = 0;
                            Sets.z1y = 0;
                            Renderer.clearImage();
                        } catch (NullPointerException exception) {
                            //pass
                        }
                    }
                })
                .map("F", () -> {
                    if (isCool(200)) {
                        try {
                            double x = Renderer.p2cx((int) Window.Fractalizer.getMousePosition().getX());
                            double y = Renderer.p2cy((int) Window.Fractalizer.getMousePosition().getY());
                            Sets.z1x = x;
                            Sets.z1y = y;
                            Renderer.clearImage();
                        } catch (NullPointerException exception) {
                            //pass
                        }
                    }
                })
                .map("O", () -> {
                    Renderer.resetRange();
                    Renderer.clearImage();
                })

        ;
    }
    static void doAction(KeyEvent e){
        String code = getCode(e);
        mapping.mapping.get(code).run();
    }
    private static String getCode(KeyEvent e){
        return KeyEvent.getKeyText(e.getKeyCode());
    }

    static class Mapping{
        Hashtable<String, KeyMap> mapping = new Hashtable<>();
        Mapping map(String regex, KeyMap keyMap){
            String[] matches = regex.split("\\|");
            for(String match : matches) {
                mapping.put(match, keyMap);
            }
            return this;
        }
    }
    interface KeyMap {
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

