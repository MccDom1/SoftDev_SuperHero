import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {

    private String roomID;
    private String roomName;
    private String roomDesc;
    private boolean visited;
    private Map<String, String> exits;
    private ArrayList<Item> roomInventory;
    private Puzzle puzzle;
    private Monster monster;

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

    public String getRoomID() {
        return roomID;
    }

    public String getRoomName() {
        return roomName;
    }

    public String getRoomDesc() {
        return roomDesc;
    }

    public String getExit(String direction) {
        if (direction == null) return null;
        return exits.get(direction.toLowerCase());
    }

    public void addExit(String direction, String roomID) {
        if (direction != null && roomID != null && !roomID.equals("0")) {
            exits.put(direction.toLowerCase(), roomID);
        }
    }

    public String getExitList() {
        if (exits.isEmpty()) {
            return "none";
        }

        StringBuilder sb = new StringBuilder();

        for (String direction : exits.keySet()) {
            sb.append(direction).append(" ");
        }

        return sb.toString().trim();
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isVisited() {
        return visited;
    }

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

    public Item findItemByName(String itemName) {
        if (itemName == null) return null;

        for (Item item : roomInventory) {
            if (item.getName().equalsIgnoreCase(itemName.trim())) {
                return item;
            }
        }

        return null;
    }

    public void addPuzzle(Puzzle puzzle) {
        this.puzzle = puzzle;
    }

    public boolean hasPuzzle() {
        return puzzle != null && !puzzle.isSolved();
    }

    public Puzzle getPuzzle() {
        return puzzle;
    }

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