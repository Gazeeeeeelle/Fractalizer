package main;

import java.util.ArrayList;
import java.util.Scanner;

class InputReceiver extends Thread{
    private final Scanner SCANNER = new Scanner(System.in);
    private final ArrayList<Command> commands = new ArrayList<>();
    private final String HELP_TEXT = buildHelpText();
    private final String GENERIC_ERROR = "Invalid input.";
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
        String scanned;
        while(true){
            for(;;) {
                scanned = SCANNER.nextLine();
                if(!scanned.isEmpty()) break;
            }
            try {
                if (commands.get(getCommandIndex(scanned)).operation.run() == 1) {
                    System.out.println(GENERIC_ERROR);
                }
            }catch (IndexOutOfBoundsException e){
                //pass
            }
        }
    }
    private String ask(String question){
        System.out.print(question);
        return SCANNER.nextLine();
    }
    private int getCommandIndex(String command){
        for (int i = 0; i < commands.size(); i++) {
            if(command.matches(commands.get(i).name)){
                return i;
            }
        }
        return -1;
    }

    private String buildHelpText(){
        String helpText = "";
        for (int i = 0; i < commands.size(); i++) {
            helpText = helpText.concat(buildHelpTextForCommand(i)+"\n");
        }
        String separator = createSeparator(findLongestLineSize(helpText));
        helpText = separator + helpText + separator;
        return helpText;
    }
    private String createSeparator(int length){
        return "-".repeat(length)+"\n";
    }
    private int findLongestLineSize(String text){
        int l = 0;
        String[] lines = text.split("\n");
        for(String string : lines){
            if(string.length() > l) l = string.length();
        }
        return l;
    }
    private String buildHelpTextForCommand(int index){
        String r1 = "  ->  ";
        return (index >= 0) ?
                commands.get(index).name +
                        (" ".repeat(getLargestCommandRegexSize()- commands.get(index).name.length())) +
                        r1 +
                        commands.get(index).name.split("\\|")[0]+" " +
                        commands.get(index).instruction
        :
                GENERIC_ERROR;
    }
    private int getLargestCommandRegexSize(){
        int l = 0;
        for(Command command : commands){
            if(command.name.length() > l) l = command.name.length();
        }
        return l;
    }
    private void loadCommands(){
        commands.add(new Command(
                "coor|coordinates|goto|go to",
                "<from x1> <divisor> <to x2> <divisor> <from y1><Optional \"i\"> <divisor> <to y2><Optional \"i\">\n" +
                        "                                     <divisors : \" -> \", \", \">",
                () -> {
                    String s = ask("Enter coordinates: ");
                    try {
                        String[] arr = s.split(", | -> |i -> |i");
                        Renderer.setBounds(
                                Double.parseDouble(arr[0]),
                                Double.parseDouble(arr[1]),
                                Double.parseDouble(arr[2]),
                                Double.parseDouble(arr[3])
                        );
                        Renderer.clearImage();
                        System.out.println("There you go!");
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        return 1;
                    }
                    return 0;
                }
        ));
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
        ));
        commands.add(new Command(
                "exit|quit",
                "",
                () -> {
                    System.out.println("Goodbye.");
                    System.exit(0);
                    return 0;
                }
        ));
        commands.add(new Command(
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
        ));
        commands.add(new Command(
                "threads|th|calcs|calculators",
                "<amount of calculators(threads)> <priority of calculators(threads)>",
                () -> {
                    String answer;
                    try {
                        answer = ask("Amount of calculators(threads): ");
                        int n = Integer.parseInt(answer);
                        answer = ask("Priority of calculators(threads): ");
                        int p = Integer.parseInt(answer);
                        Renderer.destroyCalculatorSet();
                        Renderer.buildCalculatorSet(n, p);
                    } catch (NumberFormatException e) {
                        return 1;
                    }
                    return 0;
                }
        ));
        commands.add(new Command(
                "setz|setZ|sz|z",
                "<zx> <divisor> <zy> <divisor> <from y1><Optional \"i\">",
                () -> {
                    String answer = ask("Enter Z: ");
                    try {
                        answer = answer.replaceAll(" ", "")
                                .replaceAll("i", "")
                                .replaceAll("\\+", ",");

                        String[] split = answer.split(",");
                        Sets.setZ(
                                Double.parseDouble(split[0]),
                                Double.parseDouble(split[1])
                        );
                        Renderer.clearImage();
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        return 1;
                    }
                    return 0;

                }
        ));
        commands.add(new Command(
                "zr",
                "<Value for Zr>",
                () -> {
                    String answer = ask("Enter Zr: ");
                    try {
                        double v1;
                        v1 = Double.parseDouble(answer);
                        Sets.setZR(v1);
                        Renderer.clearImage();
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        return 1;
                    }
                    return 0;
                }
        ));
        commands.add(new Command(
                "zi",
                "<Value for Zi>",
                () -> {
                    String answer = ask("Enter Zi: ");
                    try {
                        Sets.setZI(Double.parseDouble(answer));
                        Renderer.clearImage();
                    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                        return 1;
                    }
                    return 0;
                }
        ));
        commands.add(new Command(
                "bindings|bind|keys",
                "",
                () -> {
                    System.out.println(BINDINGS_TEXT);
                    return 0;
                }
        ));
    }
    private static class Command{
        String name;
        String instruction;
        Operation operation;
        Command(String name, String instruction, Operation operation){
            this.name = name;
            this.instruction = instruction;
            this.operation = operation;
        }
    }
    private interface Operation{
        int run();
    }
}
