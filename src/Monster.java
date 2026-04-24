import java.util.ArrayList;
import java.util.List;

public class Monster {
    private final String id;
    private final String name;
    private final String description;
    private final int roomId;
    private int health;
    private final int damage;
    private final boolean hostile;
    private boolean defeated;
    private final List<String> lootIds;

    public Monster(String id, String name, String description, int roomId, int health, int damage, boolean hostile) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.roomId = roomId;
        this.health = health;
        this.damage = damage;
        this.hostile = hostile;
        this.defeated = false;
        this.lootIds = new ArrayList<>();
    }

    // ======================== // CONSTRUCTOR OVERLOAD FOR MAP DATA // ========================
    public Monster(String id, String name, String description, boolean defeated,
                   int health, int damage, String spawnType, String roomCode) {
        this(id, name, description, parseRoomNumber(roomCode), health, damage,
                !spawnType.equalsIgnoreCase("friendly"));
        this.defeated = defeated;
    }

    private static int parseRoomNumber(String roomCode) {
        if (roomCode == null) {
            return -1;
        }

        try {
            return Integer.parseInt(roomCode.replace("R_", "").trim());
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getRoomId() { return roomId; }
    public int getHealth() { return health; }
    public int getDamage() { return damage; }
    public boolean isHostile() { return hostile; }
    public boolean isDefeated() { return defeated; }
    public List<String> getLootIds() { return lootIds; }

    public void addLootId(String itemId) {
        lootIds.add(itemId);
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health <= 0) {
            health = 0;
            defeated = true;
        }
    }
}
