import java.util.ArrayList;
import java.util.List;

public class Player {

    private String currentRoomId;
    private String previousRoomId;

    private int health;
    private int maxHealth;
    private int playerScore;

    private List<Item> inventory;

    private Item equippedWeapon;
    private Item equippedArmor;

    public Player(String startingRoomId) {
        this.currentRoomId = startingRoomId;
        this.previousRoomId = startingRoomId;

        this.maxHealth = 100;
        this.health = 100;
        this.playerScore = 100;

        this.inventory = new ArrayList<>();

        this.equippedWeapon = null;
        this.equippedArmor = null;
    }

    public String getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(String currentRoomId) {
        this.previousRoomId = this.currentRoomId;
        this.currentRoomId = currentRoomId;
    }

    public String getPreviousRoomId() {
        return previousRoomId;
    }

    public void returnToPreviousRoom() {
        this.currentRoomId = previousRoomId;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getPlayerScore() {
        return playerScore;
    }

    public void setHealth(int health) {
        if (health < 0) {
            this.health = 0;
        } else if (health > maxHealth) {
            this.health = maxHealth;
        } else {
            this.health = health;
        }
    }

    public void takeDamage(int amount) {
        setHealth(health - amount);
    }

    public void heal(int amount) {
        setHealth(health + amount);
    }

    public void decreaseScore(int amount) {
        playerScore -= amount;

        if (playerScore < 0) {
            playerScore = 0;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void addItem(Item item) {
        if (item != null) {
            inventory.add(item);
        }
    }

    public boolean removeItem(Item item) {
        return item != null && inventory.remove(item);
    }

    public Item findItemInInventory(String itemName) {
        if (itemName == null) {
            return null;
        }

        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName.trim())) {
                return item;
            }
        }

        return null;
    }

    public boolean hasItem(String itemName) {
        return findItemInInventory(itemName) != null;
    }

    public Item getEquippedWeapon() {
        return equippedWeapon;
    }

    public Item getEquippedArmor() {
        return equippedArmor;
    }

    public GameResult move(String direction, Room currentRoom) {
        if (currentRoom == null) {
            return new GameResult("Current room is invalid.", false, false);
        }

        String nextRoomId = currentRoom.getExit(direction);

        if (nextRoomId == null) {
            return new GameResult("You can't go that way.", false, false);
        }

        setCurrentRoomId(nextRoomId);

        return new GameResult("You moved " + direction + ".", true, false);
    }

    public GameResult pickupItem(String itemName, Room room) {
        if (room == null) {
            return new GameResult("Current room is invalid.", false, false);
        }

        if (itemName == null || itemName.trim().isEmpty()) {
            return new GameResult("You must specify an item to pick up.", false, false);
        }

        Item item = room.removeItemByName(itemName);

        if (item == null) {
            return new GameResult("Item not found.", false, false);
        }

        addItem(item);

        return new GameResult("You picked up " + item.getName() + ".", false, false);
    }

    public GameResult dropItem(String itemName, Room room) {
        if (room == null) {
            return new GameResult("Current room is invalid.", false, false);
        }

        Item item = findItemInInventory(itemName);

        if (item == null) {
            return new GameResult("That item is not in your inventory.", false, false);
        }

        removeItem(item);
        room.addItem(item);

        if (equippedWeapon == item) {
            equippedWeapon = null;
        }

        if (equippedArmor == item) {
            equippedArmor = null;
        }

        return new GameResult("You dropped " + item.getName() + ".", false, false);
    }

    public GameResult inspectItem(String itemName, Room room) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return new GameResult("You must specify what to inspect.", false, false);
        }

        Item inventoryItem = findItemInInventory(itemName);

        if (inventoryItem != null) {
            return new GameResult(inventoryItem.getDescription(), false, false);
        }

        if (room != null) {
            for (Item item : room.getItems()) {
                if (item.getName().equalsIgnoreCase(itemName.trim())) {
                    return new GameResult(item.getDescription(), false, false);
                }
            }
        }

        return new GameResult("That item could not be found.", false, false);
    }

    public GameResult getInventoryResult() {
        if (inventory.isEmpty()) {
            return new GameResult("Your inventory is empty.", false, false);
        }

        StringBuilder sb = new StringBuilder("Inventory:\n");

        for (Item item : inventory) {
            sb.append("- ").append(item.getName());

            if (item == equippedWeapon || item == equippedArmor) {
                sb.append(" (equipped)");
            }

            sb.append("\n");
        }

        return new GameResult(sb.toString(), false, false);
    }

    public GameResult equipItem(String itemName) {
        Item item = findItemInInventory(itemName);

        if (item == null) {
            return new GameResult("That item is not in your inventory.", false, false);
        }

        if (!item.isEquippable()) {
            return new GameResult("That item cannot be equipped.", false, false);
        }

        if (item.isWeapon()) {
            equippedWeapon = item;
        } else if (item.isArmor()) {
            equippedArmor = item;
        }

        return new GameResult(item.getName() + " equipped.", false, false);
    }

    public GameResult useItem(String itemName) {
        Item item = findItemInInventory(itemName);

        if (item == null) {
            return new GameResult("That item is not in your inventory.", false, false);
        }

        if (item.isConsumable()) {
            health += item.getHealthEffect();

            if (health > maxHealth) {
                health = maxHealth;
            }

            if (item.getHealthEffect() < 0) {
                health -= item.getHealthEffect();
            }

            removeItem(item);

            return new GameResult(item.getName() + " used. Health: " + health + "/" + maxHealth, false, false);
        }

        return new GameResult("That item cannot be used right now.", false, false);
    }

    public GameResult interactPuzzle(Room room) {
        if (room == null || !room.hasPuzzle()) {
            return new GameResult("There is no puzzle here.", false, false);
        }

        return new GameResult("Puzzle available.", false, true);
    }

    public GameResult solvePuzzle(String answer, Room room) {
        if (room == null || !room.hasPuzzle()) {
            return new GameResult("There is no puzzle here.", false, false);
        }

        Puzzle puzzle = room.getPuzzle();

        if (answer == null || answer.trim().isEmpty()) {
            return new GameResult("You must enter an answer.", false, true);
        }

        boolean correct = puzzle.attemptAnswer(answer.trim());

        if (correct) {
            return new GameResult("Correct! " + puzzle.getOutcome(), true, false);
        }

        takeDamage(5);

        playerScore -= puzzle.getWrongAttempt();

        if (playerScore < 0) {
            playerScore = 0;
        }

        if (puzzle.isFailed()) {
            puzzle.resetPuzzle();
            returnToPreviousRoom();
            return new GameResult("Wrong! You failed the puzzle, lost 5 HP, lost score, and were sent back.", true, false);
        }

        return new GameResult("Incorrect. You lost 5 HP. Attempts left: " + puzzle.getRemainingAttempts(), false, true);
    }

    public GameResult attackMonster(Room room) {
        if (room == null || !room.hasMonster()) {
            return new GameResult("There is no monster here.", false, false);
        }

        Monster monster = room.getMonster();

        int damage = 5;

        if (equippedWeapon != null) {
            damage += equippedWeapon.getStatValue();
        }

        monster.takeDamage(damage);

        if (monster.isDefeated()) {
            return new GameResult("You defeated the " + monster.getName() + ".", true, false);
        }

        int incomingDamage = monster.getDamage();

        if (equippedArmor != null) {
            incomingDamage = Math.max(0, incomingDamage - equippedArmor.getStatValue());
        }

        takeDamage(incomingDamage);

        return new GameResult(
                "You hit the " + monster.getName() + " for " + damage + " damage.\n" +
                        "The " + monster.getName() + " hit you for " + incomingDamage + " damage.\n" +
                        "HP: " + health + "/" + maxHealth,
                false,
                false
        );
    }
}