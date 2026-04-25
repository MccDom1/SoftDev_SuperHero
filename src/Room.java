import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {

    // ======================== // VARIABLES // ========================
    private String roomID;
    private String roomName;
    private String roomDesc;

    private boolean visited;

    private Map<String, String> exits;

    private ArrayList<Item> roomInventory;

    private Puzzle puzzle;

    // ✅ ADDED: Monster support
    private Monster monster;

    // ======================== // CONSTRUCTOR // ========================
    public Room(String roomID, String roomName, String roomDesc) {
        this.roomID = roomID;
        this.roomName = roomName;
        this.roomDesc = roomDesc;

        this.visited = false;

        this.exits = new HashMap<>();
        this.roomInventory = new ArrayList<>();

        this.puzzle = null;
        this.monster = null;
    }

    // ======================== // GETTERS // ========================
    public String getRoomID() {
        return roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomDesc() {
        return roomDesc;
    }

    // ======================== // EXIT METHODS // ========================
    public String getExit(String direction) {
        if (direction == null) return null;
        return exits.get(direction.toLowerCase());
    }

    public void addExit(String direction, String roomID) {
        if (direction != null && roomID != null && !roomID.equals("0")) {
            exits.put(direction.toLowerCase(), roomID);
        }
    }

    public Map<String, String> getExits() {
        return exits;
    }

    // ======================== // VISITED TRACKING // ========================
    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isVisited() {
        return visited;
    }

    public boolean trackVisit() {
        if (visited) {
            return true;
        }
        visited = true;
        return false;
    }

    // ======================== // ROOM ITEMS // ========================
    public void addItem(Item item) {
        if (item != null) {
            roomInventory.add(item);
        }
    }

    public boolean removeItem(Item item) {
        return item != null && roomInventory.remove(item);
    }

    public ArrayList<Item> getItems() {
        return roomInventory;
    }

    public Item removeItemByName(String itemName) {
        if (itemName == null) return null;

        for (int i = 0; i < roomInventory.size(); i++) {
            if (roomInventory.get(i).getName().equalsIgnoreCase(itemName.trim())) {
                return roomInventory.remove(i);
            }
        }
        return null;
    }

    // ======================== // PUZZLE METHODS // ========================
    public void addPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    public boolean hasPuzzle() {
        return puzzle != null && !puzzle.isSolved();
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

    public void resetPuzzle() {
        if (puzzle != null) {
            puzzle.resetPuzzle();
        }
    }

    // ======================== // MONSTER METHODS (NEW) ========================
    public void setMonster(Monster monster) {
        this.monster = monster;
    }

    public Monster getMonster() {
        return monster;
    }

    public boolean hasMonster() {
        return monster != null && !monster.isDefeated();
    }
}