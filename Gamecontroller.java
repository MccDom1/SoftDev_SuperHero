/*
 * GameController
 *
 * Notes to self:
 * - This is the bridge between user input and model logic.
 * - Its job is NOT to contain all game rules.
 * - Its real job is:
 *      1. read the input string
 *      2. split command from argument
 *      3. call the correct model method
 *      4. send the returned result to the view
 *
 * Example:
 * - "N"                 -> move player north
 * - "PICKUP lamp"       -> call model.pickupItem("lamp")
 * - "DROP key"          -> call model.dropItem("key")
 * - "SOLVE answer"      -> call model.solvePuzzle("answer")
 */
public class GameController {

    private GameModel model;
    private GameView view;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
    }

    /*
     * Start game
     *
     * Notes to self:
     * - When the game begins, just show the starting room result.
     * - If a puzzle is sitting in the opening room, display it too.
     */
    public void startGame() {
        GameResult result = model.getStartingRoomResult();
        view.showMessage(result.getMessage());

        if (result.shouldShowPuzzle()) {
            view.showPuzzle(model.getCurrentPuzzle());
        }
    }

    /*
     * Process input
     *
     * Notes to self:
     * - Split once only so commands like "PICKUP old key" still work.
     * - parts[0] = command
     * - parts[1] = argument (if one exists)
     */
    public boolean processInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            view.showMessage("Please enter a command.");
            return true;
        }

        String[] parts = input.trim().split(" ", 2);
        String command = parts[0].toUpperCase();
        String argument = parts.length > 1 ? parts[1].trim() : "";

        GameResult result;

        switch (command) {

            case "N":
            case "NORTH":
                result = model.movePlayer("N");
                break;

            case "E":
            case "EAST":
                result = model.movePlayer("E");
                break;

            case "S":
            case "SOUTH":
                result = model.movePlayer("S");
                break;

            case "W":
            case "WEST":
                result = model.movePlayer("W");
                break;

            case "EXPLORE":
                result = model.exploreRoom();
                break;

            case "PICKUP":
            case "TAKE":
                result = model.pickupItem(argument);
                break;

            case "DROP":
                result = model.dropItem(argument);
                break;

            case "INSPECT":
            case "LOOK":
                result = model.inspectItem(argument);
                break;

            case "INVENTORY":
                result = model.getInventory();
                break;

            case "EQUIP":
                result = model.equipItem(argument);
                break;

            case "SOLVE":
                result = model.solvePuzzle(argument);
                break;

            case "HELP":
                result = new GameResult(buildHelpText());
                break;

            case "QUIT":
            case "EXITGAME":
                view.showMessage("Thanks for playing Escape the Mansion.");
                return false;

            default:
                /*
                 * Notes to self:
                 * - If there is an unsolved puzzle, treat unknown input as a possible puzzle answer.
                 * - Otherwise just reject it as an invalid command.
                 */
                if (model.hasUnsolvedPuzzle()) {
                    result = model.solvePuzzle(input.trim());
                } else {
                    result = new GameResult("Not a valid command.");
                }
                break;
        }

        view.showMessage(result.getMessage());

        if (result.shouldShowPuzzle()) {
            view.showPuzzle(model.getCurrentPuzzle());
        }

        return true;
    }

    /*
     * Help text
     *
     * Notes to self:
     * - Keep command list clear and readable.
     * - This is one of the easiest things to grade / demo, so clarity matters.
     */
    private String buildHelpText() {
        StringBuilder help = new StringBuilder();

        help.append("Valid Commands:\n");
        help.append("- N, E, S, W\n");
        help.append("- NORTH, EAST, SOUTH, WEST\n");
        help.append("- EXPLORE\n");
        help.append("- PICKUP <item>\n");
        help.append("- TAKE <item>\n");
        help.append("- DROP <item>\n");
        help.append("- INSPECT <item>\n");
        help.append("- LOOK <item>\n");
        help.append("- INVENTORY\n");
        help.append("- EQUIP <item>\n");
        help.append("- SOLVE <answer>\n");
        help.append("- HELP\n");
        help.append("- QUIT");

        return help.toString();
    }
}