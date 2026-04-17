import java.util.ArrayList;
import java.util.List;

/*
 * Player
 *
 * Notes to self:
 * - This class should focus on PLAYER STATE, not full game logic.
 * - Keep this class responsible for what the player "has" and "is",
 *   not for controlling the whole world.
 * - The model can ask the player questions like:
 *      "What room are you in?"
 *      "What items are you carrying?"
 *      "What item is equipped?"
 *      "How much health do you have?"
 */
public class Player {

    // Current room the player is standing in right now.
    private int currentRoomId;

    // Useful for things like flee / backtracking / returning after an event.
    private int previousRoomId;

    // Basic player health setup.
    private int health;
    private int maxHealth;

    // Player inventory should live here because it belongs to the player.
    private List<Item> inventory;

    // Keeping one equipped item for now keeps the design simple.
    // If the project expands later, this can become equippedWeapon / equippedArmor / etc.
    private Item equippedItem;

    /*
     * Constructor
     *
     * Notes to self:
     * - Game starts with a room id.
     * - Start health at 100 because that matches the game rules.
     * - Inventory starts empty.
     */
    public Player(int startingRoomId) {
        this.currentRoomId = startingRoomId;
        this.previousRoomId = startingRoomId;

        this.maxHealth = 100;
        this.health = 100;

        this.inventory = new ArrayList<>();
        this.equippedItem = null;
    }

    // -----------------------------
    // Room tracking
    // -----------------------------

    public int getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(int currentRoomId) {
        // Before changing current room, store the old one.
        // This gives us a clean "where was the player just before this?" value.
        this.previousRoomId = this.currentRoomId;
        this.currentRoomId = currentRoomId;
    }

    public int getPreviousRoomId() {
        return previousRoomId;
    }

    public void setPreviousRoomId(int previousRoomId) {
        this.previousRoomId = previousRoomId;
    }

    // -----------------------------
    // Health
    // -----------------------------

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        /*
         * Notes to self:
         * - Never let health drop below 0.
         * - Never let health go above maxHealth.
         * - That keeps player state clean without needing extra checks everywhere else.
         */
        if (health < 0) {
            this.health = 0;
        } else if (health > maxHealth) {
            this.health = maxHealth;
        } else {
            this.health = health;
        }
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        /*
         * Notes to self:
         * - Max health should never be less than 1.
         * - If max health shrinks, current health should not stay above it.
         */
        if (maxHealth < 1) {
            this.maxHealth = 1;
        } else {
            this.maxHealth = maxHealth;
        }

        if (health > this.maxHealth) {
            health = this.maxHealth;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    // -----------------------------
    // Inventory
    // -----------------------------

    public List<Item> getInventory() {
        return inventory;
    }

    public void addItem(Item item) {
        /*
         * Notes to self:
         * - Only add if item actually exists.
         * - Convention used here:
         *      roomId = -1 means the item is in the player's inventory.
         * - That makes file-loaded objects easier to reuse.
         */
        if (item != null) {
            inventory.add(item);
            item.setRoomId(-1);
        }
    }

    public boolean removeItem(Item item) {
        if (item == null) {
            return false;
        }
        return inventory.remove(item);
    }

    public Item findItemInInventory(String itemName) {
        /*
         * Notes to self:
         * - Search by name, case-insensitive.
         * - This is used a lot by the model, so it belongs here.
         */
        if (itemName == null || itemName.trim().isEmpty()) {
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

    // -----------------------------
    // Equipped item
    // -----------------------------

    public Item getEquippedItem() {
        return equippedItem;
    }

    public void setEquippedItem(Item equippedItem) {
        this.equippedItem = equippedItem;
    }

    public void clearEquippedItem() {
        this.equippedItem = null;
    }
}