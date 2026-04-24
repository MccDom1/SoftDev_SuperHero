import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Room {
    // Marquella's room idea, adapted to fit the rest of the system.
    private int roomId;
    private String roomCode;
    private String roomName;
    private String roomDesc;
    private boolean visited;
    private java.util.Map<String, Integer> exits;
    private List<Item> roomInventory;
    private Puzzle puzzle;
    private Monster monster;

    public Room(int roomId, String roomCode, String roomName, String roomDesc) {
        this.roomId = roomId;
        this.roomCode = roomCode;
        this.roomName = roomName;
        this.roomDesc = roomDesc;
        this.visited = false;
        this.exits = new HashMap<>();
        this.roomInventory = new ArrayList<>();
        this.puzzle = null;
        this.monster = null;
    }

    // ======================== // CONSTRUCTOR OVERLOAD FOR MAP DATA // ========================
    public Room(String roomCode, String roomName, String roomDesc) {
        this(parseRoomNumber(roomCode), roomCode, roomName, roomDesc);
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

    public int getRoomId() { return roomId; }
    public String getRoomCode() { return roomCode; }
    public String getName() { return roomName; }
    public String getDescription() { return roomDesc; }

    public void addExit(String direction, int targetRoomId) {
        if (targetRoomId > 0) {
            exits.put(normalizeDirection(direction), targetRoomId);
        }
    }

    // ======================== // EXIT OVERLOAD FOR MAP DATA // ========================
    public void addExit(String direction, String targetRoomCode) {
        int targetRoomId = parseRoomNumber(targetRoomCode);
        addExit(direction, targetRoomId);
    }

    public int getExit(String direction) {
        return exits.getOrDefault(normalizeDirection(direction), -1);
    }

    private String normalizeDirection(String direction) {
        if (direction == null || direction.trim().isEmpty()) {
            return "";
        }

        String value = direction.trim().toUpperCase();
        if (value.startsWith("N")) return "N";
        if (value.startsWith("E")) return "E";
        if (value.startsWith("S")) return "S";
        if (value.startsWith("W")) return "W";
        return value;
    }

    public java.util.Map<String, Integer> getExits() {
        return exits;
    }

    public boolean isVisited() { return visited; }
    public void markVisited() { visited = true; }

    public List<Item> getItems() { return roomInventory; }

    public void addItem(Item item) {
        if (item != null) {
            roomInventory.add(item);
            item.setRoomId(roomId);
        }
    }

    public void removeItem(Item item) {
        roomInventory.remove(item);
    }

    public Item findItem(String itemName) {
        if (itemName == null) return null;
        for (Item item : roomInventory) {
            if (item.getName().equalsIgnoreCase(itemName.trim())) {
                return item;
            }
        }
        return null;
    }

    public Puzzle getPuzzle() { return puzzle; }
    public void setPuzzle(Puzzle puzzle) { this.puzzle = puzzle; }
    public void addPuzzle(Puzzle puzzle) { this.puzzle = puzzle; }

    public Monster getMonster() { return monster; }
    public void setMonster(Monster monster) { this.monster = monster; }
}
