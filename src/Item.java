public class Item {

    // ======================== // VARIABLES // ========================
    private final String id;
    private final String name;
    private final String type;
    private final String description;
    private final int statValue;
    private int roomId;

    private boolean consumable;
    private boolean equippable;

    // ======================== // CONSTRUCTOR // ========================
    public Item(String id, String name, String type, String description, int statValue) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.statValue = statValue;
        this.roomId = -1;

        // ======================== // TYPE LOGIC // ========================
        if (type.equalsIgnoreCase("consumable")) {
            this.consumable = true;
            this.equippable = false;
        }
        else if (type.equalsIgnoreCase("weapon") || type.equalsIgnoreCase("equipment")) {
            this.consumable = false;
            this.equippable = true;
        }
        else {
            // utility items (keys, puzzle rewards, etc.)
            this.consumable = false;
            this.equippable = false;
        }
    }

    // ======================== // CONSTRUCTOR OVERLOAD FOR GAMEWORLD DATA // ========================
    public Item(String id, String name, String type, String description,
                int healthEffect, int damageEffect, int roomId,
                boolean consumable, boolean equippable) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;

        // Stat value is kept as the single shared item value.
        // Consumables use it as health. Weapons/equipment use it as damage/defense.
        this.statValue = type.equalsIgnoreCase("consumable") ? healthEffect : damageEffect;
        this.roomId = roomId;
        this.consumable = consumable;
        this.equippable = equippable;
    }

    // ======================== // GETTERS // ========================
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getDescription() { return description; }

    public int getStatValue() {
        return statValue;
    }

    public int getHealthEffect() {
        return isConsumable() ? statValue : 0;
    }

    public int getDamageEffect() {
        return isWeapon() || isArmor() ? statValue : 0;
    }

    public int getRoomId() { return roomId; }

    public void setRoomId(int roomId) { this.roomId = roomId; }

    public boolean isConsumable() { return consumable; }
    public boolean isEquippable() { return equippable; }

    // ======================== // TYPE HELPERS // ========================
    public boolean isWeapon() {
        return type.equalsIgnoreCase("weapon");
    }

    public boolean isArmor() {
        return type.equalsIgnoreCase("equipment");
    }

    public boolean isUtility() {
        return type.equalsIgnoreCase("utility");
    }

    // ======================== // DISPLAY // ========================
    @Override
    public String toString() {
        return name;
    }
}
