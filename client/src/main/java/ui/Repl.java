package ui;

import java.util.Scanner;


public class Repl {
    private final PreLoginClient preLoginClient;

    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl);
    }

    public void run() {
        System.out.println("\uD83D\uDC51 Welcome to 240 chess. Type help to get started. \uD83D\uDC51");
        System.out.print(preLoginClient.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = preLoginClient.eval(line);
                System.out.print(EscapeSequences.SET_BG_COLOR_WHITE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + EscapeSequences.RESET_BG_COLOR + ">>> " + EscapeSequences.SET_BG_COLOR_BLUE);
    }

}