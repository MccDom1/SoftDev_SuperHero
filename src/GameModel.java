import java.util.HashMap;

public class GameModel {

    private Player player;
    private GameWorld gameWorld;

    public GameModel(GameWorld gameWorld) {
        this.gameWorld = gameWorld;

        HashMap<String, Room> rooms = gameWorld.getRoomMap();
        String startingRoomId = "R_1";

        if (!rooms.containsKey(startingRoomId) && !rooms.isEmpty()) {
            startingRoomId = rooms.keySet().iterator().next();
        }

        this.player = new Player(startingRoomId);
    }

    public Room getCurrentRoom() {
        return gameWorld.getRoomMap().get(player.getCurrentRoomId());
    }

    public GameResult movePlayer(String direction) {
        Room currentRoom = getCurrentRoom();

        GameResult moveResult = player.move(direction, currentRoom);

        if (!moveResult.shouldShowRoom()) {
            return moveResult;
        }

        Room newRoom = getCurrentRoom();

        if (newRoom == null) {
            return new GameResult("Room could not be found.", false, false);
        }

        if (newRoom.hasMonster()) {
            return new GameResult(
                    "You enter " + newRoom.getRoomName() + ". A " + newRoom.getMonster().getName() + " is here.\n" +
                            "Available monster commands: inspect monster, attack, defend, flee",
                    false,
                    false
            );
        }

        if (newRoom.hasPuzzle()) {
            return new GameResult(
                    "You enter " + newRoom.getRoomName() + ". There is a puzzle here.\n" +
                            "Use 'interact puzzle' to inspect it.",
                    false,
                    false
            );
        }

        return new GameResult("You enter " + newRoom.getRoomName() + ".", false, false);
    }

    public GameResult exploreRoom() {
        Room room = getCurrentRoom();

        if (room == null) {
            return new GameResult("Current room is invalid.", false, false);
        }

        return new GameResult("You look around carefully.", true, false);
    }

    public GameResult viewExits() {
        Room room = getCurrentRoom();

        if (room == null) {
            return new GameResult("Current room is invalid.", false, false);
        }

        return new GameResult("Available exits: " + room.getExitList(), false, false);
    }

    public GameResult pickupItem(String itemName) {
        return player.pickupItem(itemName, getCurrentRoom());
    }

    public GameResult dropItem(String itemName) {
        return player.dropItem(itemName, getCurrentRoom());
    }

    public GameResult inspectItem(String itemName) {
        return player.inspectItem(itemName, getCurrentRoom());
    }

    public GameResult useItem(String itemName) {
        GameResult result = player.useItem(itemName);

        if (result.isGameOver()) {
            return result;
        }

        return checkWinCondition(result);
    }

    public GameResult equipItem(String itemName) {
        return player.equipItem(itemName);
    }

    public GameResult interactPuzzle() {
        return player.interactPuzzle(getCurrentRoom());
    }

    public GameResult attemptPuzzle(String answer) {
        GameResult result = player.solvePuzzle(answer, getCurrentRoom());

        if (!player.isAlive()) {
            return new GameResult("Your health reached 0. Game over.", false, false, true);
        }

        return result;
    }

    public GameResult attackMonster() {
        GameResult result = player.attackMonster(getCurrentRoom());

        if (!player.isAlive()) {
            return new GameResult("Your health reached 0. Game over.", false, false, true);
        }

        return result;
    }

    public GameResult defend() {
        return player.defend(getCurrentRoom());
    }

    public GameResult flee() {
        return player.flee(getCurrentRoom());
    }

    public GameResult inspectMonster() {
        Room room = getCurrentRoom();

        if (room == null || !room.hasMonster()) {
            return new GameResult("There is no monster here.", false, false);
        }

        Monster monster = room.getMonster();

        return new GameResult(
                monster.getName() + "\n" +
                        monster.getDescription() + "\n" +
                        "HP: " + monster.getHealth() + "\n" +
                        "Damage: " + monster.getDamage() + "\n" +
                        "Available commands: attack, defend, flee",
                false,
                false
        );
    }

    public GameResult getInventory() {
        return player.getInventoryResult();
    }

    public GameResult getStatus() {
        return new GameResult(
                "Health: " + player.getHealth() + "/" + player.getMaxHealth() +
                        "\nScore: " + player.getPlayerScore() +
                        "\nCurrent Room: " + player.getCurrentRoomId(),
                false,
                false
        );
    }

    private GameResult checkWinCondition(GameResult previousResult) {
        Room room = getCurrentRoom();

        if (room == null) {
            return previousResult;
        }

        boolean inMasterBedroom = room.getRoomID().equalsIgnoreCase("R_22");
        boolean hasStone = player.hasItem("Health Stone");
        boolean hasFinalKey = player.hasItem("Final Key");
        boolean specterDefeated = !room.hasMonster();

        if (inMasterBedroom && hasStone && hasFinalKey && specterDefeated) {
            return new GameResult(
                    previousResult.getMessage() +
                            "\nYou use the Health Stone and Final Key. The Master Safe Room opens.\n" +
                            "You escaped the mansion!",
                    false,
                    false,
                    true
            );
        }

        return previousResult;
    }
}