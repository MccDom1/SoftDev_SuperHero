public class GameController {

    private GameModel model;

    public GameController(GameModel model) {
        this.model = model;
    }

    public GameResult processCommand(String input) {

        if (input == null || input.trim().equals("")) {
            return new GameResult("Invalid command.", false, false);
        }

        input = input.trim();

        String[] parts = input.split(" ", 2);

        String command = parts[0].toLowerCase();
        String argument = (parts.length > 1) ? parts[1].trim() : "";

        switch (command) {

            case "n":
            case "north":
                return model.movePlayer("north");

            case "s":
            case "south":
                return model.movePlayer("south");

            case "e":
            case "east":
                return model.movePlayer("east");

            case "w":
            case "west":
                return model.movePlayer("west");

            case "explore":
                return model.exploreRoom();

            case "pickup":
            case "take":
                return model.pickupItem(argument);

            case "drop":
                return model.dropItem(argument);

            case "inspect":
                if (argument.equalsIgnoreCase("monster")) {
                    return model.inspectMonster();
                }
                return model.inspectItem(argument);

            case "use":
                return model.useItem(argument);

            case "equip":
                return model.equipItem(argument);

            case "interact":
                if (argument.equalsIgnoreCase("puzzle")) {
                    return model.interactPuzzle();
                }
                return new GameResult("Invalid interact command.", false, false);

            case "solve":
            case "answer":
                return model.attemptPuzzle(argument);

            case "attack":
                return model.attackMonster();

            case "inventory":
                return model.getInventory();

            case "status":
                return model.getStatus();

            case "help":
                return new GameResult(
                        "Commands: n s e w, explore, pickup <item>, drop <item>, inspect <item>, inspect monster, use <item>, equip <item>, interact puzzle, solve <answer>, attack, inventory, status, quit",
                        false,
                        false
                );

            default:
                return new GameResult("Unknown command.", false, false);
        }
    }
}