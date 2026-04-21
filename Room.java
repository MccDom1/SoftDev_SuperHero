import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {
    // Marquella's room idea, adapted to fit the rest of the system.
    private int roomId;
    private String roomCode;
    private String roomName;
    private String roomDesc;
    private boolean visited;
    private Map<String, Integer> exits;
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

    public int getRoomId() { return roomId; }
    public String getRoomCode() { return roomCode; }
    public String getName() { return roomName; }
    public String getDescription() { return roomDesc; }

    public void addExit(String direction, int targetRoomId) {
        if (targetRoomId > 0) {
            exits.put(direction.toUpperCase(), targetRoomId);
        }
    }

    public int getExit(String direction) {
        return exits.getOrDefault(direction.toUpperCase(), -1);
    }

    public Map<String, Integer> getExits() {
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

    public Monster getMonster() { return monster; }
    public void setMonster(Monster monster) { this.monster = monster; }
}