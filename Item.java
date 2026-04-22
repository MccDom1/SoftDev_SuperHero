public class Item {
    // Create variables to represent properties and characteristics
	
    private final String id;
    private final String name;
    private final String type;
    private final String description;
    private final int healthEffect;
    private final int damageEffect;
    private int roomId;
    private boolean consumable;
    private boolean equippable;
    
    //Setter for all properties

    public Item(String id, String name, String type, String description,
                int healthEffect, int damageEffect,
                int roomId, boolean consumable, boolean equippable) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.description = description;
        this.healthEffect = healthEffect;
        this.damageEffect = damageEffect;
        this.roomId = roomId;
        this.consumable = consumable;
        this.equippable = equippable;
    }
    
    //Getters for all properties

    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public int getHealthEffect() { return healthEffect; }
    public int getDamageEffect() { return damageEffect; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public boolean isConsumable() { return consumable; }
    public boolean isEquippable() { return equippable; }

    public boolean isWeapon() {
        return type.equalsIgnoreCase("Weapon");
    }

    public boolean isArmor() {
        return type.equalsIgnoreCase("Equipment");
    }

    @Override
    public String toString() {
        return name;
    }
}

