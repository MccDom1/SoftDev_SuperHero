import java.util.ArrayList;
import java.util.List;

public class Player {

    private String currentRoomId;
    private String previousRoomId;

    private int health;
    private int maxHealth;
    private int playerScore;

    private int attackBonus;
    private int defenseBonus;

    private List<Item> inventory;

    private Item equippedWeapon;
    private Item equippedArmor;

    public Player(String startingRoomId) {
        this.currentRoomId = startingRoomId;
        this.previousRoomId = startingRoomId;
        this.maxHealth = 100;
        this.health = 100;
        this.playerScore = 100;
        this.attackBonus = 0;
        this.defenseBonus = 0;
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

    public int getAttackBonus() {
        return attackBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public boolean isAlive() {
        return health > 0;
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

    public List<Item> getInventory() {
        return inventory;
    }

    public boolean hasItem(String itemName) {
        return findItemInInventory(itemName) != null;
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
        if (itemName == null) return null;

        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName.trim())) {
                return item;
            }
        }

        return null;
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
        return new GameResult("Moved.", true, false);
    }

    public GameResult pickupItem(String itemName, Room room) {
        if (room == null) {
            return new GameResult("Current room is invalid.", false, false);
        }

        if (itemName == null || itemName.trim().isEmpty()) {
            return new GameResult("Specify an item. Example: pickup Silver Cog", false, false);
        }

        Item item = room.removeItemByName(itemName);

        if (item == null) {
            return new GameResult("Item not found. Try 'explore' to see items in the room.", false, false);
        }

        addItem(item);
        return new GameResult(item.getName() + " was added to your inventory.", false, false);
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

        if (equippedWeapon == item) equippedWeapon = null;
        if (equippedArmor == item) equippedArmor = null;

        return new GameResult("You dropped " + item.getName() + ".", false, false);
    }

    public GameResult inspectItem(String itemName, Room room) {
        if (itemName == null || itemName.trim().isEmpty()) {
            return new GameResult("Specify what to inspect. Example: inspect Silver Cog", false, false);
        }

        Item item = findItemInInventory(itemName);

        if (item == null && room != null) {
            item = room.findItemByName(itemName);
        }

        if (item == null) {
            return new GameResult("That item could not be found.", false, false);
        }

        return new GameResult(item.getDescription(), false, false);
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

    public GameResult useItem(String itemName, Room room) {
        Item item = findItemInInventory(itemName);

        if (item == null) {
            return new GameResult("That item is not in your inventory.", false, false);
        }

        if (room != null && room.hasPuzzle()) {
            Puzzle puzzle = room.getPuzzle();

            if (puzzle.getSolution().equalsIgnoreCase(item.getName())) {
                boolean solved = puzzle.attemptAnswer(item.getName());

                if (solved) {
                    removeItem(item);
                    return new GameResult("You used " + item.getName() + ". Puzzle solved! " + puzzle.getOutcome(), false, false);
                }
            }
        }

        if (item.isConsumable()) {
            if (item.getName().equalsIgnoreCase("Attack Potion")) {
                attackBonus += item.getStatValue();
                removeItem(item);
                return new GameResult("Attack Potion used. Attack bonus increased by " + item.getStatValue() + ".", false, false);
            }

            if (item.getName().equalsIgnoreCase("Curse Potion")) {
                defenseBonus += item.getStatValue();
                removeItem(item);
                return new GameResult("Curse Potion used. Defense bonus increased by " + item.getStatValue() + ".", false, false);
            }

            int effect = item.getHealthEffect();

            if (effect >= 0) {
                health += effect;
            } else {
                health -= Math.abs(effect);
            }

            if (health > maxHealth) health = maxHealth;
            if (health < 0) health = 0;

            removeItem(item);

            if (health == 0) {
                return new GameResult(item.getName() + " used. Your health reached 0. Game over.", false, false, true);
            }

            return new GameResult(item.getName() + " used. Health: " + health + "/" + maxHealth, false, false);
        }

        if (item.isUtility()) {
            return new GameResult("You used " + item.getName() + ".", false, false);
        }

        return new GameResult("That item cannot be used right now.", false, false);
    }

    public GameResult interactPuzzle(Room room) {
        if (room == null || !room.hasPuzzle()) {
            return new GameResult("There is no puzzle here.", false, false);
        }

        return new GameResult("You inspect the puzzle.", false, true);
    }

    public GameResult solvePuzzle(String answer, Room room) {
        if (room == null || !room.hasPuzzle()) {
            return new GameResult("There is no puzzle here.", false, false);
        }

        Puzzle puzzle = room.getPuzzle();

        if (answer == null || answer.trim().isEmpty()) {
            return new GameResult("Submit an answer with: solve <answer>", false, true);
        }

        boolean correct = puzzle.attemptAnswer(answer.trim());

        if (correct) {
            return new GameResult("Correct! " + puzzle.getOutcome(), false, false);
        }

        takeDamage(5);
        playerScore -= puzzle.getWrongAttempt();

        if (playerScore < 0) playerScore = 0;

        if (!isAlive()) {
            return new GameResult("Your health reached 0. Game over.", false, false, true);
        }

        if (puzzle.isFailed()) {
            puzzle.resetPuzzle();
            returnToPreviousRoom();
            return new GameResult("Puzzle failed. You lost 5 HP and were sent back.", false, false);
        }

        return new GameResult("Incorrect. You lost 5 HP. Attempts left: " + puzzle.getRemainingAttempts(), false, true);
    }

    public GameResult attackMonster(Room room) {
        if (room == null || !room.hasMonster()) {
            return new GameResult("There is no monster here.", false, false);
        }

        Monster monster = room.getMonster();

        int damage = 5 + attackBonus;

        if (equippedWeapon != null) {
            damage += equippedWeapon.getStatValue();
        }

        monster.takeDamage(damage);

        if (monster.isDefeated()) {
            return new GameResult("You defeated the " + monster.getName() + ".", false, false);
        }

        int incomingDamage = monster.getDamage() - defenseBonus;

        if (equippedArmor != null) {
            incomingDamage -= equippedArmor.getStatValue();
        }

        if (incomingDamage < 0) {
            incomingDamage = 0;
        }

        takeDamage(incomingDamage);

        if (!isAlive()) {
            return new GameResult("The " + monster.getName() + " defeated you. Game over.", false, false, true);
        }

        return new GameResult(
                "You hit the " + monster.getName() + " for " + damage + " damage.\n" +
                        "The " + monster.getName() + " hit you for " + incomingDamage + " damage.\n" +
                        "You can attack, defend, or flee.",
                false,
                false
        );
    }

    public GameResult defend(Room room) {
        if (room == null || !room.hasMonster()) {
            return new GameResult("There is no monster to defend against.", false, false);
        }

        Monster monster = room.getMonster();

        int incomingDamage = monster.getDamage() - defenseBonus;

        if (equippedArmor != null) {
            incomingDamage -= equippedArmor.getStatValue();
        }

        incomingDamage = Math.max(0, incomingDamage / 2);

        takeDamage(incomingDamage);

        if (!isAlive()) {
            return new GameResult("You defended, but your health reached 0. Game over.", false, false, true);
        }

        return new GameResult("You defend against the " + monster.getName() + ". Damage reduced to " + incomingDamage + ".", false, false);
    }

    public GameResult flee(Room room) {
        if (room == null || !room.hasMonster()) {
            return new GameResult("There is nothing to flee from.", false, false);
        }

        returnToPreviousRoom();
        return new GameResult("You fled back to the previous room.", true, false);
    }
}