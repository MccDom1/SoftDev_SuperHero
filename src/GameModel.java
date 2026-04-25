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

        if (!result.shouldShowRoom()) {
            return result;
        }

        Room newRoom = getCurrentRoom();

        if (newRoom == null) {
            return new GameResult("Room not found.", false, false);
        }

        newRoom.setVisited(true);

        return new GameResult(
                "You moved " + direction + ".",
                true,
                false
        );
    }

    public GameResult exploreRoom() {
        return new GameResult("You look around the room.", true, false);
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

    public GameResult inspectMonster() {
        Room room = getCurrentRoom();

        if (room == null || !room.hasMonster()) {
            return new GameResult("There is no monster here.", false, false);
        }

        Monster monster = room.getMonster();

        return new GameResult(
                monster.getName() + "\n" +
                        monster.getDescription() + "\n" +
                        "HP: " + monster.getHealth(),
                false,
                false
        );
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
                "Health: " + player.getHealth() +
                        "\nScore: " + player.getPlayerScore(),
                false,
                false
        );
    }
}