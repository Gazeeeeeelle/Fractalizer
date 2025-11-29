package main;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.regex.Pattern;

final class Controller {
    Controller mapKey(String keyRegex, Operation e){
        keyMap.map(keyRegex, e);
        return this;
    }
    void operate(KeyEvent e){
        String code = getCode(e);
        getMatchAndRun(code);
    }
    private final Map keyMap = new Map();
    private void getMatchAndRun(String code){
        for(Pattern regex : keyMap.mappings.keySet()){
            if(regex.matcher(code).matches()){
                keyMap.mappings.get(regex).run(code);
                return;
            }
        }
    }
    private String getCode(KeyEvent e){
        return KeyEvent.getKeyText(e.getKeyCode());
    }
    private static class Map {
        private final HashMap<Pattern, Operation> mappings = new HashMap<>();
        private void map(String regex, Operation operation){
            mappings.put(Pattern.compile(regex), operation);
        }
    }
    interface Operation {
        void run(String input);
    }
    private long time = System.currentTimeMillis();
    boolean isCool(long c){
        if(System.currentTimeMillis()-time >= c){
            time = System.currentTimeMillis();
            return true;
        }
        return false;
    }
}

