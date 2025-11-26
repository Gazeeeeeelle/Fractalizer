package main;

import complexMath.Sets;

import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.regex.Pattern;
final class Controller {
    Controller mapKey(String keyRegex, Operation e){
        keyMap.map(keyRegex, e);
        return this;
    }
    Controller useMap(Map map){
        keyMap = keyMap1;
        return this;
    }
    void operate(KeyEvent e){
        String code = getCode(e);
        getMatchAndRun(code);
    }
    private Map keyMap = new Map();
    private boolean getMatchAndRun(String code){
        for(Pattern regex : keyMap.mappings.keySet()){
            if(regex.matcher(code).matches()){
                keyMap.mappings.get(regex).run(code);
                return true;
            }
        }
        return false;
    }
    private String getCode(KeyEvent e){
        return KeyEvent.getKeyText(e.getKeyCode());
    }

    private static class Map {
        private final Hashtable<Pattern, Operation> mappings = new Hashtable<>();
        private Map map(String regex, Operation operation){
            mappings.put(Pattern.compile(regex), operation);
            return this;
        }
    }
    interface Operation {
        void run(String input);
    }
    static long time = System.currentTimeMillis();
    private boolean isCool(long c){
        if(System.currentTimeMillis()-time >= c){
            time = System.currentTimeMillis();
            return true;
        }
        return false;
    }
    Map keyMap1 = new Map()
            .map("G", e -> {
                if (isCool(200)) {
                    Sets.setZ1(0d, 0d);
                    Renderer.clearImage();
                }
            })
            .map("F", e -> {
                if(Window.getMousePos() != null && isCool(200)) {
                    Sets.setZPixel(Window.getMousePos());
                }
            })
            .map("E", e -> {
                Renderer.special ^= true;
                Renderer.clearImage();
            })
            .map("O", e -> {
                Renderer.resetRange();
                Renderer.clearImage();
            })
            .map("H", e -> {
                Sets.toggleInverse();
                Renderer.clearImage();
            })
            .map("J", e -> {
                Sets.toggleJulia();
                Renderer.clearImage();
            })
            .map("L", e -> {
                Sets.connectLines ^= true;
                Renderer.clearImage();
            })
            .map("C", e -> Renderer.centerAtPixel(Window.getMousePos()))
            .map("N", e -> {
                Renderer.togglePinpointPrecision();
                Renderer.clearImage();
            })
            .map("I", e -> System.out.println(Sets.getInfo()))
            .map("P", e -> {
                Renderer.togglePreCalculation();
                Renderer.clearImage();
            })
            .map("S", e -> Renderer.togglePosition())
            .map("A", e -> Renderer.toggleAxis())
            .map("Right", e -> {
                Sets.mode++;
                Renderer.clearImage();
            })
            .map("Left", e -> {
                Sets.mode--;
                Renderer.clearImage();
            })
            .map("Up", e -> {
                Colors.shiftColorPalette(1);
                Renderer.clearImage();
            })
            .map("Down", e -> {
                Colors.shiftColorPalette(-1);
                Renderer.clearImage();
            })
            .map("F5", e -> Renderer.clearImage())
            .map("F2", e -> Renderer.screenshot())
            .map("[1-9]", e -> {
                int set = Integer.parseInt(e);
                if (set < 6/*FIXME*/) Renderer.chooseSet(set); //FIXME accessing amount of ComplexFractals
                else return;
                Renderer.clearImage();
            });
}

