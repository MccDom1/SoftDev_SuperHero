/**
 * GameModel Class     Author Dominique M.
 *
 * PURPOSE:
 * Core logic engine of the game. Handles player actions, world interaction,
 * and overall game state progression.
 *
 * DESIGN:
 * - Acts as the main Model in MVC architecture
 * - Coordinates Player, GameWorld, Room, Item, Puzzle, and Monster systems
 * - Returns structured results using GameResult instead of direct output
 *
 * NOTE:
 * Designed for scalability and modular expansion (combat, puzzles, inventory, etc.)
 * while maintaining clean separation from UI logic.
 */

public class GameModel {
    private GameWorld world;
    private Player player;

    public GameModel() {
        this.world = new GameWorld();
        this.player = new Player(1);
        Room startRoom = getCurrentRoom();
        if (startRoom != null) {
            startRoom.markVisited();
            player.setCurrentPuzzle(startRoom.getPuzzle());
        }
    }

    public Player getPlayer() { return player; }
    public GameWorld getWorld() { return world; }

    public Room getCurrentRoom() {
        return world.getRoom(player.getCurrentRoomId());
    }

    public Puzzle getCurrentPuzzle() {
        return player.getCurrentPuzzle();
    }

    public boolean hasUnsolvedPuzzle() {
        Puzzle puzzle = getCurrentPuzzle();
        return puzzle != null && !puzzle.isSolved();
    }

    public GameResult getStartingRoomResult() {
        Room room = getCurrentRoom();
        if (room == null) {
            return new GameResult("Starting room could not be found.");
        }

        GameResult result = new GameResult(room.getName() + "\n" + room.getDescription());
        if (room.getPuzzle() != null && !room.getPuzzle().isSolved()) {
            result.setShowPuzzle(true);
        }
        return result;
    }

    public GameResult movePlayer(String direction) {
        Room currentRoom = getCurrentRoom();
        if (currentRoom == null) {
            return new GameResult("Current room is invalid.");
        }

        int nextRoomId = currentRoom.getExit(direction);
        if (nextRoomId == -1) {
            return new GameResult("You cannot travel this way!");
        }

        Room nextRoom = world.getRoom(nextRoomId);
        if (nextRoom == null) {
            return new GameResult("That room does not exist.");
        }

        player.setCurrentRoomId(nextRoomId);
        player.setCurrentPuzzle(nextRoom.getPuzzle());

        StringBuilder message = new StringBuilder();
        message.append(nextRoom.getName()).append("\n");
        message.append(nextRoom.getDescription());

        if (nextRoom.isVisited()) {
            message.append("\nYou have visited this room before.");
        } else {
            nextRoom.markVisited();
        }

        if (nextRoom.getMonster() != null && !nextRoom.getMonster().isDefeated()) {
            message.append("\nA ").append(nextRoom.getMonster().getName()).append(" is here.");
        }

        GameResult result = new GameResult(message.toString());
        if (nextRoom.getPuzzle() != null && !nextRoom.getPuzzle().isSolved()) {
            result.setShowPuzzle(true);
        }
        if (nextRoom.getMonster() != null && !nextRoom.getMonster().isDefeated()) {
            result.setInCombat(true);
        }
        return result;
    }

    public GameResult exploreRoom() {
        Room room = getCurrentRoom();
        if (room == null) {
            return new GameResult("Current room is invalid.");
        }

        StringBuilder message = new StringBuilder();
        message.append(room.getName()).append("\n");
        message.append(room.getDescription());

        if (!room.getItems().isEmpty()) {
            message.append("\nItems in this room:");
            for (Item item : room.getItems()) {
                message.append("\n- ").append(item.getName());
            }
        } else {
            message.append("\nThere are no visible items here.");
        }

        if (room.getPuzzle() != null && !room.getPuzzle().isSolved()) {
            message.append("\nThere is an unsolved puzzle here.");
        }

        if (room.getMonster() != null && !room.getMonster().isDefeated()) {
            message.append("\nMonster Present: ").append(room.getMonster().getName());
        }

        GameResult result = new GameResult(message.toString());
        if (room.getPuzzle() != null && !room.getPuzzle().isSolved()) {
            result.setShowPuzzle(true);
        }
        return result;
    }

    public GameResult getExits() {
        Room room = getCurrentRoom();
        if (room == null) return new GameResult("Current room is invalid.");

        StringBuilder sb = new StringBuilder("Exits:");
        boolean found = false;
        for (String dir : new String[]{"N", "E", "S", "W"}) {
            int next = room.getExit(dir);
            if (next != -1) {
                sb.append(" ").append(dir);
                found = true;
            }
        }
        if (!found) {
            sb.append(" none");
        }
        return new GameResult(sb.toString());
    }

    public GameResult pickupItem(String itemName) {
        Room room = getCurrentRoom();
        if (room == null) return new GameResult("Current room is invalid.");
        if (itemName == null || itemName.trim().isEmpty()) return new GameResult("You must specify an item to pick up.");

        Item item = room.findItem(itemName);
        if (item == null) return new GameResult("That item is not in this room.");

        room.removeItem(item);
        player.addItem(item);
        return new GameResult(item.getName() + " was added to your inventory.");
    }

    public GameResult dropItem(String itemName) {
        Room room = getCurrentRoom();
        if (room == null) return new GameResult("Current room is invalid.");
        if (itemName == null || itemName.trim().isEmpty()) return new GameResult("You must specify an item to drop.");

        Item item = player.findItemInInventory(itemName);
        if (item == null) return new GameResult("That item is not in your inventory.");

        player.removeItem(item);
        room.addItem(item);

        if (player.getEquippedWeapon() == item) {
            player.setEquippedWeapon(null);
        }
        if (player.getEquippedArmor() == item) {
            player.setEquippedArmor(null);
        }

        return new GameResult(item.getName() + " was dropped into the room.");
    }

    public GameResult inspectItem(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return new GameResult("You must specify an item to inspect.");
        }

        Item inventoryItem = player.findItemInInventory(itemName);
        if (inventoryItem != null) {
            return new GameResult(inventoryItem.getDescription());
        }

        Room room = getCurrentRoom();
        if (room != null) {
            Item roomItem = room.findItem(itemName);
            if (roomItem != null) {
                return new GameResult(roomItem.getDescription());
            }
        }
        return new GameResult("That item could not be found.");
    }

    public GameResult getInventory() {
        if (player.getInventory().isEmpty()) {
            return new GameResult("Your inventory is empty.");
        }

        StringBuilder message = new StringBuilder("Inventory:");
        for (Item item : player.getInventory()) {
            message.append("\n- ").append(item.getName());
            if (player.getEquippedWeapon() == item || player.getEquippedArmor() == item) {
                message.append(" (equipped)");
            }
        }
        return new GameResult(message.toString());
    }

    public GameResult equipItem(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return new GameResult("You must specify an item to equip.");
        }

        Item item = player.findItemInInventory(itemName);
        if (item == null) {
            return new GameResult("That item is not in your inventory.");
        }
        if (!item.isEquippable()) {
            return new GameResult("That item cannot be equipped.");
        }

        if (item.isWeapon()) {
            player.setEquippedWeapon(item);
        } else if (item.isArmor()) {
            player.setEquippedArmor(item);
        } else {
            return new GameResult("That item cannot be equipped.");
        }

        return new GameResult(item.getName() + " is now equipped.");
    }

    public GameResult useItem(String itemName) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return new GameResult("You must specify an item to use.");
        }

        Item item = player.findItemInInventory(itemName);
        if (item == null) {
            return new GameResult("That item is not in your inventory.");
        }

        if (item.getHealthEffect() != 0) {
            player.setHealth(player.getHealth() + item.getHealthEffect());
        }

        StringBuilder sb = new StringBuilder(item.getName() + " was used.");
        if (item.getHealthEffect() != 0) {
            sb.append(" Health is now ").append(player.getHealth()).append("/").append(player.getMaxHealth()).append(".");
        }

        if (item.isConsumable()) {
            player.removeItem(item);
            sb.append(" It was consumed.");
        }

        return new GameResult(sb.toString());
    }

    public GameResult interactPuzzle() {
        Puzzle puzzle = getCurrentPuzzle();
        if (puzzle == null) {
            return new GameResult("There is no puzzle in this room.");
        }

        GameResult result = new GameResult(
                "Puzzle: " + puzzle.getPuzzleName() + "\n" +
                puzzle.getDescription() + "\n" +
                "Attempts Remaining: " + puzzle.getRemainingAttempts());
        result.setShowPuzzle(true);
        return result;
    }

    public GameResult solvePuzzle(String answer) {
        Puzzle puzzle = getCurrentPuzzle();
        if (puzzle == null) return new GameResult("There is no puzzle in this room.");
        if (puzzle.isSolved()) return new GameResult("This puzzle has already been solved.");
        if (answer == null || answer.trim().isEmpty()) return new GameResult("You must enter an answer.");

        boolean correct = puzzle.checkAnswer(answer);
        if (correct) {
            StringBuilder message = new StringBuilder();
            message.append(puzzle.getSuccessMessage());
            if (puzzle.getReward() != null && !puzzle.getReward().isEmpty()) {
                message.append("\nReward: ").append(puzzle.getReward());
                grantPuzzleReward(puzzle.getReward());
            }
            return new GameResult(message.toString());
        }

        player.takeDamage(5);

        if (puzzle.isFailed()) {
            String fail = puzzle.getFailMessage();
            puzzle.resetPuzzle();
            player.returnToPreviousRoom();
            player.setCurrentPuzzle(getCurrentRoom().getPuzzle());
            return new GameResult(fail + "\nYou took 5 damage and were returned to the previous room.");
        }

        GameResult result = new GameResult("Incorrect answer. You took 5 damage.\nAttempts Remaining: " + puzzle.getRemainingAttempts());
        result.setShowPuzzle(true);
        return result;
    }

    private void grantPuzzleReward(String rewardName) {
        Room room = getCurrentRoom();
        if (room == null || rewardName == null) return;
        Item reward = room.findItem(rewardName);
        if (reward != null) {
            room.removeItem(reward);
            player.addItem(reward);
            return;
        }

        // fallback: if the reward exists in world but has not already been placed in inventory
        for (Room r : world.getAllRooms()) {
            Item item = r.findItem(rewardName);
            if (item != null) {
                r.removeItem(item);
                player.addItem(item);
                return;
            }
        }
    }

    public GameResult attackMonster() {
        Room room = getCurrentRoom();
        if (room == null) return new GameResult("Current room is invalid.");

        Monster monster = room.getMonster();
        if (monster == null || monster.isDefeated()) {
            return new GameResult("There is no monster to attack.");
        }

        int damage = 5;
        if (player.getEquippedWeapon() != null) {
            damage += player.getEquippedWeapon().getDamageEffect();
        }

        monster.takeDamage(damage);

        if (monster.isDefeated()) {
            StringBuilder sb = new StringBuilder("You defeated the ");
            sb.append(monster.getName()).append(".");
            for (String lootId : monster.getLootIds()) {
                Item loot = world.getItemById(lootId);
                if (loot != null) {
                    room.addItem(loot);
                    sb.append("\nDropped: ").append(loot.getName());
                }
            }
            return new GameResult(sb.toString());
        }

        int incoming = monster.getDamage();
        if (player.getEquippedArmor() != null) {
            incoming = Math.max(0, incoming - Math.max(1, player.getEquippedArmor().getDamageEffect()));
        }
        player.takeDamage(incoming);

        StringBuilder sb = new StringBuilder();
        sb.append("You hit the ").append(monster.getName()).append(" for ").append(damage).append(" damage.");
        sb.append("\nThe ").append(monster.getName()).append(" hit you for ").append(incoming).append(" damage.");
        sb.append("\nYour HP: ").append(player.getHealth()).append("/").append(player.getMaxHealth());
        if (!player.isAlive()) {
            sb.append("\nYou have been defeated.");
        }
        GameResult result = new GameResult(sb.toString());
        result.setInCombat(true);
        return result;
    }

    public GameResult inspectMonster() {
        Room room = getCurrentRoom();
        if (room == null) return new GameResult("Current room is invalid.");
        Monster monster = room.getMonster();
        if (monster == null || monster.isDefeated()) {
            return new GameResult("There is no monster here.");
        }
        return new GameResult(monster.getName() + "\n" + monster.getDescription() + "\nHP: " + monster.getHealth() + "\nDamage: " + monster.getDamage());
    }

    public GameResult getStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("HP: ").append(player.getHealth()).append("/").append(player.getMaxHealth());
        sb.append("\nCurrent Room: ").append(getCurrentRoom() != null ? getCurrentRoom().getName() : "Unknown");
        sb.append("\nEquipped Weapon: ").append(player.getEquippedWeapon() != null ? player.getEquippedWeapon().getName() : "None");
        sb.append("\nEquipped Armor: ").append(player.getEquippedArmor() != null ? player.getEquippedArmor().getName() : "None");
        return new GameResult(sb.toString());
    }
}
