/**
 * GameController Class      Author Dominique M.
 *
 * PURPOSE:
 * Serves as the middle layer between player input, game logic, and output.
 * This class receives raw commands from the user, interprets them, routes
 * them to the GameModel, and then passes the results to the GameView.
 *
 * DESIGN:
 * - Keeps command parsing separate from core game logic
 * - Keeps UI display separate from decision-making
 * - Helps maintain a clean MVC-style structure
 *
 * NOTE:
 * The controller should not contain major game rules. Its job is mainly to
 * translate user input into actions the model understands.
 */
public class GameController     {

    // Reference to the game logic layer
    private GameModel model;

    // Reference to the output/display layer
    private GameView view;

    /**
     * Constructor
     *
     * Connects the controller to the model and the view so it can route
     * player commands and display the results.
     */
    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Starts the game by showing the welcome message and the first room.
     * Also checks if the starting room contains a puzzle that should be shown.
     */
    public void startGame() {
        GameResult result = model.getStartingRoomResult();

        // Initial welcome text
        view.showMessage("Welcome to Escape the Mansion!");

        // Show the opening room description
        view.showMessage(result.getMessage());

        // If the starting room has an active puzzle, show it
        if (result.shouldShowPuzzle()) {
            view.showPuzzle(model.getCurrentPuzzle());
        }
    }

    /**
     * Processes a single line of player input.
     *
     * Returns:
     * - true if the game should continue running
     * - false if the player chose to quit
     */
    public boolean processInput(String input) {

        // Prevent null or empty input from being treated like a command
        if (input == null || input.trim().isEmpty()) {
            view.showMessage("Please enter a command.");
            return true;
        }

        // Split into command and optional argument
        // Example: "pickup sword" -> command = PICKUP, argument = sword
        String[] parts = input.trim().split(" ", 2);
        String command = parts[0].toUpperCase();
        String argument = parts.length > 1 ? parts[1].trim() : "";

        GameResult result;

        // Route the command to the correct model method
        switch (command) {

            // Movement commands (short and full versions supported)
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

            // Room exploration
            case "EXPLORE":
                result = model.exploreRoom();
                break;

            // Show available exits from current room
            case "EXITS":
                result = model.getExits();
                break;

            // Pick up / take item from room
            case "PICKUP":
            case "TAKE":
                result = model.pickupItem(argument);
                break;

            // Drop item from inventory into room
            case "DROP":
                result = model.dropItem(argument);
                break;

            // Use item from inventory
            case "USE":
                result = model.useItem(argument);
                break;

            // Equip item if it is wearable/usable as gear
            case "EQUIP":
                result = model.equipItem(argument);
                break;

            // Show player inventory
            case "INVENTORY":
                result = model.getInventory();
                break;

            // Inspect either a monster or an item
            case "INSPECT":
                if (argument.equalsIgnoreCase("monster")) {
                    result = model.inspectMonster();
                } else {
                    result = model.inspectItem(argument);
                }
                break;

            // Interact with puzzle if present
            case "INTERACT":
                if (argument.equalsIgnoreCase("puzzle")) {
                    result = model.interactPuzzle();
                } else {
                    result = new GameResult("Not a valid interact command.");
                }
                break;

            // Submit a puzzle answer
            case "SOLVE":
                result = model.solvePuzzle(argument);
                break;

            // Attack the current monster
            case "ATTACK":
                result = model.attackMonster();
                break;

            // Show current player status
            case "STATUS":
                result = model.getStatus();
                break;

            // Show help text
            case "HELP":
                result = new GameResult(buildHelpText());
                break;

            // Quit ends the loop immediately
            case "QUIT":
            case "EXITGAME":
                view.showMessage("Thanks for playing Escape the Mansion.");
                return false;

            // Catch-all for invalid commands
            default:
                result = new GameResult("Not a valid command.");
                break;
        }

        // Display the message returned by the model
        view.showMessage(result.getMessage());

        // If the result says a puzzle should be shown, display the puzzle
        if (result.shouldShowPuzzle()) {
            view.showPuzzle(model.getCurrentPuzzle());
        }

        // Keep the game loop running
        return true;
    }

    /**
     * Builds the help text displayed to the player.
     * Keeping this in its own method makes it easier to update later.
     */
    private String buildHelpText() {
        return "Commands:\n" +
                "N, E, S, W\n" +
                "EXPLORE\n" +
                "EXITS\n" +
                "PICKUP <item>\n" +
                "DROP <item>\n" +
                "USE <item>\n" +
                "EQUIP <item>\n" +
                "INVENTORY\n" +
                "INSPECT <item/monster>\n" +
                "INTERACT PUZZLE\n" +
                "SOLVE <answer>\n" +
                "ATTACK\n" +
                "STATUS\n" +
                "HELP\n" +
                "QUIT";
    }
}
