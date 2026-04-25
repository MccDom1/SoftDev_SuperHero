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

        Room startRoom = getCurrentRoom();
        if (startRoom != null) {
            startRoom.setVisited(true);
        }
    }

    public Room getCurrentRoom() {
        return gameWorld.getRoomMap().get(player.getCurrentRoomId());
    }

    public GameResult movePlayer(String direction) {
        GameResult result = player.move(direction, getCurrentRoom());

        Room newRoom = getCurrentRoom();

        if (newRoom == null) {
            return new GameResult("Room could not be found.", false, false);
        }

        boolean visitedBefore = newRoom.isVisited();
        newRoom.setVisited(true);

        String message;

        if (visitedBefore) {
            message = "You return to " + newRoom.getRoomName() + ".";
        } else {
            message = newRoom.getRoomName() + "\n" + newRoom.getRoomDesc();
        }

        return new GameResult(message, true, newRoom.hasPuzzle());
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
        return player.useItem(itemName);
    }

    public GameResult equipItem(String itemName) {
        return player.equipItem(itemName);
    }

    public GameResult interactPuzzle() {
        return player.interactPuzzle(getCurrentRoom());
    }

    public GameResult attemptPuzzle(String answer) {
        return player.solvePuzzle(answer, getCurrentRoom());
    }

    public GameResult attackMonster() {
        return player.attackMonster(getCurrentRoom());
    }

    public GameResult getInventory() {
        return player.getInventoryResult();
    }

    public GameResult getStatus() {
        return new GameResult(
                "Health: " + player.getHealth() + "/" + player.getMaxHealth() +
                        "\nCurrent Room: " + player.getCurrentRoomId(),
                false,
                false
        );
    }

    public Player getPlayer() {
        return player;
    }
}