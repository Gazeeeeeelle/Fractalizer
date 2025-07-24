package main;

import java.util.ArrayList;
import java.util.Scanner;

class InputReceiver extends Thread{
    private final Scanner SCANNER = new Scanner(System.in);
    private ArrayList<Command> commands = new ArrayList<>();
    private final String HELP_TEXT = buildHelpText();
    private final String GEN_ERROR = "Invalid input.";
    private final String BINDINGS_TEXT =
            """
            g          - Reset initial Z;
            f          - Set initial Z;
            o          - Reset range;
            j          - Switch julia set;
            up arrow   - Next color palette;
            down arrow - Previous color palette;
            F2         - Screenshot;
            F5         - Redraw;
            c          - Set center;
            n          - Switch pinpoint precision rendering;
            e          - Flip vertically;
            i          - Get info;
            p          - Enable pre calculation of precision;
            1 -> 9     - Switch between available sets.
            """;
    @Override
    public void run(){

        loadCommands();

        System.out.println("Send \"help\" to receive the command list.");
        String s;
        while(true){
            for(;;) {
                s = SCANNER.nextLine();
                if(!s.isEmpty()) break;
            }
            try {
                if (commands.get(getCommandIndex(s)).cf.function() == 1) {
                    System.out.println(GEN_ERROR);
                }
            }catch (IndexOutOfBoundsException e){
                //pass
            }
        }
    }
    String ask(String question){
        System.out.print(question);
        return SCANNER.nextLine();
    }
    int getCommandIndex(String command){
        for (int i = 0; i < commands.size(); i++) {
            if(command.matches(commands.get(i).name)){
                return i;
            }
        }
        return -1;
    }

    String buildHelpText(){
        String ht = "";
        for (int i = 0; i < commands.size(); i++) {
            ht = ht.concat(buildHelpTextForCommand(i)+"\n");
        }
        String sep = createSeparator(findLongestLineSize(ht));
        ht = sep + ht + sep;
        return ht;
    }
    String createSeparator(int l){
        return "-".repeat(l)+"\n";
    }
    int findLongestLineSize(String text){
        int l = 0;
        String[] lines = text.split("\n");
        for(String s : lines){
            if(s.length() > l) l = s.length();
        }
        return l;
    }
    String buildHelpTextForCommand(int index){
        String r1 = "  ->  ";
        return (index >= 0) ?
                commands.get(index).name +
                        (" ".repeat(getLargestCommandRegexSize()- commands.get(index).name.length())) +
                        r1 +
                        commands.get(index).name.split("\\|")[0]+" " +
                        commands.get(index).instruction
        :
                GEN_ERROR;
    }
    int getLargestCommandRegexSize(){
        int l = 0;
        for(Command c : commands){
            if(c.name.length() > l) l = c.name.length();
        }
        return l;
    }
    void loadCommands(){
        //coordinates
        {
            commands.add(new Command(
                            "coor|coordinates|goto|go to",
                            "<from x1> <divisor> <to x2> <divisor> <from y1><Optional \"i\"> <divisor> <to y2><Optional \"i\">\n" +
                                    "                                     <divisors : \" -> \", \", \">",
                            () -> {
                                String s = ask("Enter coordinates: ");
                                try {
                                    String[] arr = s.split(", | -> |i -> |i");
                                    double v1 = Double.parseDouble(arr[0]);
                                    double v2 = Double.parseDouble(arr[1]);
                                    double v3 = Double.parseDouble(arr[2]);
                                    double v4 = Double.parseDouble(arr[3]);
                                    Renderer.fromX = v1;
                                    Renderer.toX = v2;
                                    Renderer.fromY = v3;
                                    Renderer.toY = v4;
                                    Renderer.clearImage();
                                    System.out.println("There you go!");
                                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                    return 1;
                                }
                                return 0;

                            }
                    )
            );
        }

        //night
        {
            commands.add(new Command(
                            "n|night|setPinpointPrecision|pinpoint",
                            "<pinpoint precision value(Range: 0 -> +"+Integer.MAX_VALUE+")>",
                            () -> {
                                String s = ask("Pinpoint precision: ");
                                try {
                                    Sets.solN = Integer.parseInt(s);
                                    Renderer.clearImage();
                                } catch (NumberFormatException e) {
                                    return 1;
                                }
                                return 0;
                            }
                    )
            );
        }

        //exit
        {
            commands.add(
                    new Command(
                            "exit|quit",
                            "",
                            () -> {
                                System.out.println("Goodbye.");
                                System.exit(0);
                                return 0;
                            }

                    )
            );
        }

        //help
        {
            commands.add(
                    new Command(
                            "help|h",
                            "__OR__ help <command name>",
                            () -> {
                                String s = ask("Specific command(leave blank for full help): ");
                                if (s.isEmpty()) {
                                    System.out.println(HELP_TEXT);
                                } else {
                                    System.out.println(buildHelpTextForCommand(getCommandIndex(s)));
                                }
                                return 0;
                            }
                    )
            );
        }

        //threads
        {
            commands.add(
                    new Command(
                            "threads|th|calcs|calculators",
                            "<amount of calculators(threads)> <priority of calculators(threads)>",
                            () -> {
                                String s;
                                try {
                                    s = ask("Amount of calculators(threads): ");
                                    int n = Integer.parseInt(s);
                                    s = ask("Priority of calculators(threads): ");
                                    int p = Integer.parseInt(s);
                                    Renderer.destroyCalculatorSet();
                                    Renderer.buildCalculatorSet(n, p);
                                } catch (NumberFormatException e) {
                                    return 1;
                                }
                                return 0;
                            }
                    )
            );
        }

        //set z
        {
            commands.add(new Command(
                            "setz|setZ|sz|z",
                            "<zx> <divisor> <zy> <divisor> <from y1><Optional \"i\">",
                            () -> {
                                String s = ask("Enter Z: ");
                                try {
                                    s = s.replaceAll(" ", "");
                                    s = s.replaceAll("i", "");
                                    s = s.replaceAll("\\+", ",");
                                    String[] arr = s.split(",");
                                    double v1 = Double.parseDouble(arr[0]);
                                    double v2 = Double.parseDouble(arr[1]);
                                    Sets.z1x = v1;
                                    Sets.z1y = v2;
                                    Renderer.clearImage();
                                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                    return 1;
                                }
                                return 0;

                            }
                    )
            );
        }
        {
            {
                commands.add(new Command(
                                "zr",
                                "<Value for Zr>",
                                () -> {
                                    String s = ask("Enter Zr: ");
                                    try {
                                        double v1;
                                        v1 = Double.parseDouble(s);
                                        Sets.z1x = v1;
                                        Renderer.clearImage();
                                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                        return 1;
                                    }
                                    return 0;
                                }
                        )
                );
            }
            {
                commands.add(new Command(
                                "zi",
                                "<Value for Zi>",
                                () -> {
                                    String s = ask("Enter Zi: ");
                                    try {
                                        double v1;
                                        v1 = Double.parseDouble(s);
                                        Sets.z1y = v1;
                                        Renderer.clearImage();
                                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                                        return 1;
                                    }
                                    return 0;
                                }
                        )
                );
            }
        }

        //bindings
        {
            commands.add(
                    new Command(
                            "bindings|bind|keys",
                            "",
                            () -> {
                                System.out.println(BINDINGS_TEXT);
                                return 0;
                            }
                    )
            );
        }
    }
    static class Command{
        String name;
        String instruction;
        CommandFunction cf;
        Command(String name, String instruction, CommandFunction cf){
            this.name = name;
            this.instruction = instruction;
            this.cf = cf;
        }
    }
    interface CommandFunction{
        int function();
    }
}
