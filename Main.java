/**
 * Main Class      Author Dominique M.
 *
 * PURPOSE:
 * Entry point of the game. Responsible for initializing core components
 * and running the main game loop.
 *
 * DESIGN:
 * - Creates and connects Model, View, and Controller
 * - Keeps the loop running until the player chooses to quit
 *
 * NOTE:
 * This class is intentionally simple. It does not contain game logic,
 * only setup and control flow for execution.
 */

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // Scanner used to read player input from the console
        Scanner scanner = new Scanner(System.in);

        // Initialize core components of the game
        GameModel model = new GameModel();      // handles game logic
        GameView view = new GameView();         // handles output
        GameController controller = new GameController(model, view); // handles input routing

        // Start the game (welcome message + initial room display)
        controller.startGame();

        // Main game loop
        boolean running = true;
        while (running) {

            // Show available commands prompt
            view.showPrompt();

            // Read input from the player
            String input = scanner.nextLine();

            // Process input and determine if the game should continue
            running = controller.processInput(input);
        }

        // Clean up scanner resource when game ends
        scanner.close();
    }
}