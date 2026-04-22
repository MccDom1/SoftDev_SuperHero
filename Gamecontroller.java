public class GameController {
    private GameModel model;
    private GameView view;

    public GameController(GameModel model, GameView view) {
        this.model = model;
        this.view = view;
    }

    public void startGame() {
        GameResult result = model.getStartingRoomResult();
        view.showMessage("Welcome to Escape the Mansion!");
        view.showMessage(result.getMessage());
        if (result.shouldShowPuzzle()) {
            view.showPuzzle(model.getCurrentPuzzle());
        }
    }

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
            case "EXITS":
                result = model.getExits();
                break;
            case "PICKUP":
            case "TAKE":
                result = model.pickupItem(argument);
                break;
            case "DROP":
                result = model.dropItem(argument);
                break;
            case "USE":
                result = model.useItem(argument);
                break;
            case "EQUIP":
                result = model.equipItem(argument);
                break;
            case "INVENTORY":
                result = model.getInventory();
                break;
            case "INSPECT":
                if (argument.equalsIgnoreCase("monster")) {
                    result = model.inspectMonster();
                } else {
                    result = model.inspectItem(argument);
                }
                break;
            case "INTERACT":
                if (argument.equalsIgnoreCase("puzzle")) {
                    result = model.interactPuzzle();
                } else {
                    result = new GameResult("Not a valid interact command.");
                }
                break;
            case "SOLVE":
                result = model.solvePuzzle(argument);
                break;
            case "ATTACK":
                result = model.attackMonster();
                break;
            case "STATUS":
                result = model.getStatus();
                break;
            case "HELP":
                result = new GameResult(buildHelpText());
                break;
            case "QUIT":
            case "EXITGAME":
                view.showMessage("Thanks for playing Escape the Mansion.");
                return false;
            default:
                result = new GameResult("Not a valid command.");
                break;
        }

        view.showMessage(result.getMessage());
        if (result.shouldShowPuzzle()) {
            view.showPuzzle(model.getCurrentPuzzle());
        }
        return true;
    }

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
