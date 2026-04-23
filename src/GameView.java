/**
 * GameView Class      Author Dominique M.
 *
 * PURPOSE:
 * Handles all console output for the game. This keeps display logic separate
 * from the controller and model so the rest of the system can focus only on
 * processing commands and managing game state.
 *
 * DESIGN:
 * - Responsible only for showing text to the player
 * - Does not make gameplay decisions
 * - Helps keep the project aligned with a simple MVC-style structure
 *
 * NOTE:
 * If the game were later converted to a GUI or web interface, this class
 * would be one of the main places to update without changing the game logic.
 */
public class GameView {

    /**
     * Displays a general message to the player.
     * Used for room descriptions, status updates, combat results, and errors.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays the main command prompt.
     * This gives the player a quick reminder of the available commands.
     */
    public void showPrompt() {
        System.out.println("----------------------------------");
        System.out.println("Commands: N E S W | EXPLORE | EXITS | INVENTORY | PICKUP <item> | DROP <item> | USE <item> | EQUIP <item> | INSPECT <item/monster> | INTERACT PUZZLE | SOLVE <answer> | ATTACK | STATUS | HELP | QUIT");
        System.out.print("> ");
    }

    /**
     * Displays puzzle information when a room contains an active unsolved puzzle.
     * Right now this only shows the puzzle name, but it can be expanded later
     * to display full puzzle instructions or attempt info if needed.
     */
    public void showPuzzle(Puzzle puzzle) {
        if (puzzle != null && !puzzle.isSolved()) {
            System.out.println("Puzzle Available: " + puzzle.getPuzzleName());
        }
    }
}