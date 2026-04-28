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

        player.move(direction, currentRoom);

        Room newRoom = getCurrentRoom();

        if (newRoom == null) {
            return new GameResult("Room could not be found.", false, false);
        }

        boolean visitedBefore = newRoom.isVisited();
        newRoom.setVisited(true);

        if (visitedBefore) {
            return new GameResult("You've been here before.", true, false);
        }

        return new GameResult("", true, false);
    }

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
        GameResult result = player.defend(getCurrentRoom());

        if (!player.isAlive()) {
            return new GameResult("Your health reached 0. Game over.", false, false, true);
        }

        return result;
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
                        "You can attack, defend, or flee.",
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
                        "\nAttack Bonus: +" + player.getAttackBonus() +
                        "\nDefense Bonus: +" + player.getDefenseBonus() +
                        "\nCurrent Room: " + player.getCurrentRoomId(),
                false,
                false
        );
    }

    private void grantPuzzleReward(Puzzle puzzle) {
        Room room = getCurrentRoom();

        if (room == null || puzzle == null) {
            return;
        }

        Item reward = null;

        if (puzzle.getPuzzleID().equalsIgnoreCase("PZ02")) {
            reward = createItemByName("Spirit Potion");
        } else if (puzzle.getPuzzleID().equalsIgnoreCase("PZ03")) {
            reward = createItemByName("Attack Potion");
        } else if (puzzle.getPuzzleID().equalsIgnoreCase("PZ04")) {
            reward = createItemByName("Health Stone");
        } else if (puzzle.getPuzzleID().equalsIgnoreCase("PZ05")) {
            reward = createItemByName("Rusted Axe");
        } else if (puzzle.getPuzzleID().equalsIgnoreCase("PZ06")) {
            reward = createItemByName("Golf Club");
        } else if (puzzle.getPuzzleID().equalsIgnoreCase("PZ07")) {
            reward = createItemByName("Master Room Key");
        }

        if (reward != null) {
            room.addItem(reward);
        }
    }

    private void dropMonsterLoot(Monster monster, Room room) {
        if (monster == null || room == null) {
            return;
        }

        Item drop = null;

        if (monster.getName().equalsIgnoreCase("Mice")) {
            drop = createItemByName("Apple");
        } else if (monster.getName().equalsIgnoreCase("Bat")) {
            drop = createItemByName("Spirit Potion");
        } else if (monster.getName().equalsIgnoreCase("Ghost")) {
            drop = createItemByName("Ghost Key");
        } else if (monster.getName().equalsIgnoreCase("Ghoul")) {
            drop = createItemByName("Closet Key");
        } else if (monster.getName().equalsIgnoreCase("Butler")) {
            drop = createItemByName("Rusted Axe");
        } else if (monster.getName().equalsIgnoreCase("Specter")) {
            drop = createItemByName("Final Key");
        }

        if (drop != null) {
            room.addItem(drop);
        }
    }

    private Item createItemByName(String itemName) {
        if (itemName.equalsIgnoreCase("Apple")) {
            return new Item("A_1", "Apple", "consumable", "A fresh red apple. Health +10.", 10);
        }

        if (itemName.equalsIgnoreCase("Spirit Potion")) {
            return new Item("A_3", "Spirit Potion", "consumable", "A bright blue potion. Health +25.", 25);
        }

        if (itemName.equalsIgnoreCase("Attack Potion")) {
            return new Item("A_5", "Attack Potion", "consumable", "A yellow potion. Attack bonus +15.", 15);
        }

        if (itemName.equalsIgnoreCase("Curse Potion")) {
            return new Item("A_4", "Curse Potion", "consumable", "A smoky potion. Defense bonus +1.", 1);
        }

        if (itemName.equalsIgnoreCase("Rusted Axe")) {
            return new Item("A_8", "Rusted Axe", "weapon", "A rusty axe with a sharp blade. Attack +12.", 12);
        }

        if (itemName.equalsIgnoreCase("Golf Club")) {
            return new Item("A_7", "Golf Club", "weapon", "A heavy iron golf club. Attack +8.", 8);
        }

        if (itemName.equalsIgnoreCase("Master Room Key")) {
            return new Item("A_17", "Master Room Key", "utility", "A large brass key. Unlocks the Master Bedroom.", 0);
        }

        if (itemName.equalsIgnoreCase("Ghost Key")) {
            return new Item("A_18", "Ghost Key", "utility", "A dark key covered in webs. Opens the Attic.", 0);
        }

        if (itemName.equalsIgnoreCase("Closet Key")) {
            return new Item("A_19", "Closet Key", "utility", "A small key that opens the Closet.", 0);
        }

        if (itemName.equalsIgnoreCase("Health Stone")) {
            return new Item("A_20", "Health Stone", "utility", "A smooth ancient stone used to escape the mansion.", 0);
        }

        if (itemName.equalsIgnoreCase("Final Key")) {
            return new Item("A_21", "Final Key", "utility", "A majestic key. Unlocks the Master Safe Room.", 0);
        }

        return null;
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