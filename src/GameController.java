public class GameController {

    private GameModel model;

    public GameController(GameModel model) {
        this.model = model;
    }

    public GameResult processCommand(String input) {

        if (input == null || input.trim().equals("")) {
            return new GameResult("Invalid command. Type 'help' to see available commands.", false, false);
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

            case "exits":
                return model.viewExits();

            case "pickup":
            case "take":
            case "loot":
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
                return new GameResult("Invalid interact command. Try: interact puzzle", false, false);

            case "solve":
            case "answer":
                return model.attemptPuzzle(argument);

            case "ignore":
                if (argument.equalsIgnoreCase("puzzle")) {
                    return new GameResult("You step away from the puzzle for now.", false, false);
                }
                return new GameResult("Invalid ignore command. Try: ignore puzzle", false, false);

            case "attack":
                return model.attackMonster();

            case "defend":
                return model.defend();

            case "flee":
            case "escape":
                return model.flee();

            case "inventory":
                return model.getInventory();

            case "status":
                return model.getStatus();

            case "help":
                return new GameResult(
                        "Commands:\n" +
                                "- Movement: n, s, e, w\n" +
                                "- Room: explore, exits\n" +
                                "- Items: pickup <item>, loot <item>, inspect <item>, use <item>, equip <item>, drop <item>, inventory\n" +
                                "- Puzzle: interact puzzle, solve <answer>, ignore puzzle\n" +
                                "- Monster: inspect monster, attack, defend, flee\n" +
                                "- Player: status, quit",
                        false,
                        false
                );

            default:
                return new GameResult("Unknown command. Type 'help' to see available commands.", false, false);
        }
    }
}