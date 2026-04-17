import java.util.Scanner;

/*
 * Main
 *
 * Notes to self:
 * - This is the entry point.
 * - Keep Main simple.
 * - Main should create MVC objects and start the loop.
 * - Avoid stuffing game logic here.
 * - Main should not decide room movement / puzzle solving / inventory rules.
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        GameModel model = new GameModel();
        GameView view = new GameView();
        GameController controller = new GameController(model, view);

        controller.startGame();

        boolean running = true;

        while (running) {
            view.showPrompt();

            String input = scanner.nextLine();
            running = controller.processInput(input);
        }

        scanner.close();
    }
}