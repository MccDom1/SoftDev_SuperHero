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

    // ======================== // MOVEMENT // ========================
    public GameResult movePlayer(String direction) {

        Room currentRoom = getCurrentRoom();

        if (currentRoom == null) {
            return new GameResult("Current room is invalid.", false, false);
        }

        String nextRoomId = currentRoom.getExit(direction);

        if (nextRoomId == null) {
            return new GameResult("You can't go that way.", false, false);
        }

        GameResult lockCheck = checkLockedRoom(nextRoomId);
        if (lockCheck != null) {
            return lockCheck;
        }

        // Move player
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
                    "You enter " + newRoom.getRoomName() + ". A " + newRoom.getMonster().getName() + " is here.",
                    true,
                    false
            );
        }

        if (newRoom.hasPuzzle()) {
            return new GameResult(
                    "You enter " + newRoom.getRoomName() + ". There is a puzzle here. Use 'interact puzzle' to inspect it.",
                    true,
                    false
            );
        }

        return new GameResult("You enter " + newRoom.getRoomName() + ".", true, false);
    }

    // ======================== // LOCKED ROOMS // ========================
    private GameResult checkLockedRoom(String nextRoomId) {

        if (nextRoomId.equalsIgnoreCase("R_22") && !player.hasItem("Master Room Key")) {
            return new GameResult("The Master Bedroom is locked. You need the Master Room Key.", false, false);
        }

        if (nextRoomId.equalsIgnoreCase("R_20") && !player.hasItem("Ghost Key")) {
            return new GameResult("The Attic is locked. You need the Ghost Key.", false, false);
        }

        if (nextRoomId.equalsIgnoreCase("R_23") && !player.hasItem("Closet Key")) {
            return new GameResult("The Closet is locked. You need the Closet Key.", false, false);
        }

        if (nextRoomId.equalsIgnoreCase("R_5")) {
            Room currentRoom = getCurrentRoom();
            if (currentRoom != null && currentRoom.hasPuzzle()) {
                return new GameResult("A hidden mechanism blocks the Secret Passage. Solve the Library puzzle first.", false, false);
            }
        }

        return null;
    }

    // ======================== // ROOM ACTIONS // ========================
    public GameResult exploreRoom() {
        return new GameResult("You look around carefully.", true, false);
    }

    public GameResult viewExits() {
        Room room = getCurrentRoom();

        if (room == null) {
            return new GameResult("Current room is invalid.", false, false);
        }

        return new GameResult("Available exits: " + room.getExitList(), false, false);
    }

    // ======================== // ITEM ACTIONS // ========================
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

        Room room = getCurrentRoom();
        Puzzle puzzle = null;
        boolean wasSolved = false;

        if (room != null && room.hasPuzzle()) {
            puzzle = room.getPuzzle();
            wasSolved = puzzle.isSolved();
        }

        GameResult result = player.useItem(itemName, room);

        if (result.isGameOver()) {
            return result;
        }

        if (puzzle != null && !wasSolved && puzzle.isSolved()) {
            grantPuzzleReward(puzzle);
        }

        return checkWinCondition(result);
    }

    public GameResult equipItem(String itemName) {
        return player.equipItem(itemName);
    }

    // ======================== // PUZZLE ACTIONS // ========================
    public GameResult interactPuzzle() {
        return player.interactPuzzle(getCurrentRoom());
    }

    public GameResult attemptPuzzle(String answer) {

        Room room = getCurrentRoom();
        Puzzle puzzle = null;
        boolean wasSolved = false;

        if (room != null && room.hasPuzzle()) {
            puzzle = room.getPuzzle();
            wasSolved = puzzle.isSolved();
        }

        GameResult result = player.solvePuzzle(answer, room);

        if (!player.isAlive()) {
            return new GameResult("Your health reached 0. Game over.", false, false, true);
        }

        if (puzzle != null && !wasSolved && puzzle.isSolved()) {
            grantPuzzleReward(puzzle);
        }

        return result;
    }

    public GameResult resetPuzzle() {
        Room room = getCurrentRoom();

        if (room == null || !room.hasPuzzle()) {
            return new GameResult("There is no active puzzle to reset.", false, false);
        }

        room.getPuzzle().resetPuzzle();

        return new GameResult("Puzzle reset.", false, true);
    }

    // ======================== // MONSTER ACTIONS // ========================
    public GameResult attackMonster() {

        Room room = getCurrentRoom();

        if (room == null) {
            return new GameResult("Current room is invalid.", false, false);
        }

        Monster monster = room.getMonster();
        boolean wasDefeated = monster != null && monster.isDefeated();

        GameResult result = player.attackMonster(room);

        if (!player.isAlive()) {
            return new GameResult("Your health reached 0. Game over.", false, false, true);
        }

        if (monster != null && !wasDefeated && monster.isDefeated()) {
            dropMonsterLoot(monster, room);
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
                        "HP: " + monster.getHealth(),
                false,
                false
        );
    }

    // ======================== // PLAYER INFO // ========================
    public GameResult getInventory() {
        return player.getInventoryResult();
    }

    public GameResult getStatus() {
        return new GameResult(
                "Health: " + player.getHealth() + "/" + player.getMaxHealth() +
                        "\nScore: " + player.getPlayerScore(),
                false,
                false
        );
    }

    // ======================== // REWARDS + LOOT // ========================
    private void grantPuzzleReward(Puzzle puzzle) {

        Room room = getCurrentRoom();
        if (room == null) return;

        Item reward = null;

        switch (puzzle.getPuzzleID()) {
            case "PZ02": reward = createItemByName("Spirit Potion"); break;
            case "PZ03": reward = createItemByName("Attack Potion"); break;
            case "PZ04": reward = createItemByName("Health Stone"); break;
            case "PZ05": reward = createItemByName("Rusted Axe"); break;
            case "PZ06": reward = createItemByName("Golf Club"); break;
            case "PZ07": reward = createItemByName("Master Room Key"); break;
        }

        if (reward != null) {
            room.addItem(reward);
        }
    }

    private void dropMonsterLoot(Monster monster, Room room) {

        Item drop = null;

        if (monster.getName().equalsIgnoreCase("Specter")) {
            drop = createItemByName("Final Key");
        }

        if (drop != null) {
            room.addItem(drop);
        }
    }

    private Item createItemByName(String name) {
        if (name.equalsIgnoreCase("Final Key")) {
            return new Item("A_21", "Final Key", "utility", "Unlocks the final escape.", 0);
        }
        return null;
    }

    // ======================== // WIN CONDITION // ========================
    private GameResult checkWinCondition(GameResult previousResult) {

        Room room = getCurrentRoom();

        if (room == null) return previousResult;

        boolean win =
                room.getRoomID().equalsIgnoreCase("R_22") &&
                        player.hasItem("Health Stone") &&
                        player.hasItem("Final Key") &&
                        !room.hasMonster();

        if (win) {
            return new GameResult(
                    previousResult.getMessage() +
                            "\nYou escaped the mansion!",
                    false,
                    false,
                    true
            );
        }

        return previousResult;
    }
}