/**
 * Player Class
 *
 * PURPOSE:
 * Represents the player's state throughout the game. This includes location,
 * health, inventory, equipped items, and active puzzle interaction.
 *
 * DESIGN:
 * - Stores only player-related data (no game logic decisions)
 * - Keeps track of movement, combat readiness, and inventory management
 * - Designed to be updated by GameModel rather than controlling the game itself
 *
 * NOTE:
 * This class focuses on state management, allowing other parts of the system
 * (like GameModel) to handle gameplay rules and interactions.
 */

import java.util.ArrayList;
import java.util.List;

public class Player {

    // Tracks the current room the player is in
    private int currentRoomId;

    // Stores the previous room (used for backtracking or penalties)
    private int previousRoomId;

    // Current health of the player
    private int health;

    // Maximum health cap
    private int maxHealth;

    // Stores all items collected by the player
    private List<Item> inventory;

    // Currently equipped weapon (affects damage output)
    private Item equippedWeapon;

    // Currently equipped armor (affects incoming damage)
    private Item equippedArmor;

    // Tracks the current puzzle the player is interacting with
    private Puzzle currentPuzzle;

    /**
     * Constructor
     *
     * Initializes the player with a starting room and default stats.
     */
    public Player(int startingRoomId) {
        this.currentRoomId = startingRoomId;
        this.previousRoomId = startingRoomId;
        this.maxHealth = 100;
        this.health = 100;
        this.inventory = new ArrayList<>();
        this.equippedWeapon = null;
        this.equippedArmor = null;
        this.currentPuzzle = null;
    }

    // Returns current player location
    public int getCurrentRoomId() { return currentRoomId; }

    /**
     * Updates player location.
     * Also stores the previous room before moving.
     */
    public void setCurrentRoomId(int currentRoomId) {
        this.previousRoomId = this.currentRoomId;
        this.currentRoomId = currentRoomId;
    }

    public int getPreviousRoomId() { return previousRoomId; }

    // Allows manual override if needed (rare use case)
    public void setPreviousRoomId(int previousRoomId) { this.previousRoomId = previousRoomId; }

    public int getHealth() { return health; }

    /**
     * Sets player health with bounds checking.
     * Prevents health from going below 0 or above maxHealth.
     */
    public void setHealth(int health) {
        if (health < 0) this.health = 0;
        else if (health > maxHealth) this.health = maxHealth;
        else this.health = health;
    }

    public int getMaxHealth() { return maxHealth; }

    /**
     * Updates max health and ensures current health stays valid.
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = Math.max(1, maxHealth);
        if (health > this.maxHealth) health = this.maxHealth;
    }

    /**
     * Applies damage to the player.
     */
    public void takeDamage(int amount) {
        setHealth(health - amount);
    }

    // Returns true if player is still alive
    public boolean isAlive() { return health > 0; }

    public List<Item> getInventory() { return inventory; }

    /**
     * Adds an item to the player's inventory.
     * Also removes it from the room by setting its roomId to -1.
     */
    public void addItem(Item item) {
        if (item != null) {
            inventory.add(item);
            item.setRoomId(-1);
        }
    }

    /**
     * Removes an item from inventory.
     */
    public boolean removeItem(Item item) {
        return item != null && inventory.remove(item);
    }

    /**
     * Searches for an item in inventory by name (case-insensitive).
     */
    public Item findItemInInventory(String itemName) {
        if (itemName == null) return null;

        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName.trim())) {
                return item;
            }
        }
        return null;
    }

    // Convenience method to check if player has an item
    public boolean hasItem(String itemName) {
        return findItemInInventory(itemName) != null;
    }

    public Item getEquippedWeapon() { return equippedWeapon; }

    // Sets equipped weapon (used for combat damage)
    public void setEquippedWeapon(Item equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    public Item getEquippedArmor() { return equippedArmor; }

    // Sets equipped armor (used for damage reduction)
    public void setEquippedArmor(Item equippedArmor) {
        this.equippedArmor = equippedArmor;
    }

    public Puzzle getCurrentPuzzle() { return currentPuzzle; }

    // Updates the current puzzle based on room interaction
    public void setCurrentPuzzle(Puzzle currentPuzzle) {
        this.currentPuzzle = currentPuzzle;
    }

    /**
     * Returns player to their previous room.
     * Used for penalties like failed puzzles.
     */
    public void returnToPreviousRoom() {
        currentRoomId = previousRoomId;
    }
}