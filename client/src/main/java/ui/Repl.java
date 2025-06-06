package ui;



import ui.websocket.GameHandler;

import ui.websocket.GameUI;



import java.util.Scanner;





public class Repl {

    private final PreLoginClient preLoginClient;

    private final PostLoginClient postLoginClient;

    private GamePlay gamePlay;

    private static State state = State.SIGNEDOUT;
    String serverUrl;
    PromoteClient promoteClient = new PromoteClient();




    public Repl(String serverUrl) {
        preLoginClient = new PreLoginClient(serverUrl);
        this.serverUrl = serverUrl;
        GameHandler gameHandler = new GameUI(serverUrl);
        postLoginClient = new PostLoginClient(serverUrl, gameHandler);
        this.gamePlay = new GamePlay(serverUrl,gameHandler, postLoginClient );
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
                gamePlay.updateGamePlay(postLoginClient, promoteClient);

                switch(state){
                    case SIGNEDOUT -> result = preLoginClient.eval(line);

                    case SIGNEDIN -> result = postLoginClient.eval(line);

                    case GAMEPLAY -> result = gamePlay.eval(line);
                    case PROMOTE -> result = promoteClient.eval(line);
                }

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



    public static State getState(){

        return state;

    }



    public static void updateState(State newState){

        state = newState;

    }



}