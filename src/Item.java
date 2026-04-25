public class Item {

    private final String id;
    private final String name;
    private final String type;
    private final String description;
    private final int statValue;

    private boolean consumable;
    private boolean equippable;

    public Item(String id, String name, String type, String description, int statValue) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.statValue = statValue;

        if (type.equalsIgnoreCase("consumable")) {
            this.consumable = true;
            this.equippable = false;
        } else if (type.equalsIgnoreCase("weapon") || type.equalsIgnoreCase("equipment")) {
            this.consumable = false;
            this.equippable = true;
        } else {
            this.consumable = false;
            this.equippable = false;
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public int getStatValue() { return statValue; }

    public boolean isConsumable() { return consumable; }
    public boolean isEquippable() { return equippable; }

    public boolean isWeapon() {
        return type.equalsIgnoreCase("weapon");
    }

    public boolean isArmor() {
        return type.equalsIgnoreCase("equipment");
    }

    public boolean isUtility() {
        return type.equalsIgnoreCase("utility");
    }

    public int getHealthEffect() {
        if (isConsumable()) {
            return statValue;
        }
        return 0;
    }

    @Override
    public String toString() {
        return name;
    }
}