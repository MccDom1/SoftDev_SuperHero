import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room 
{
    private String roomID;
    private String roomName;
    private String roomDesc;

    private boolean visited;

    // exits: north, south, east, west → mapped to room IDs
    private Map<String, String> exits;

    private ArrayList<Item> roomInventory;
    private Puzzle puzzle;

    // ======================== // CONSTRUCTOR // ========================
    public Room(String roomID, String roomName, String roomDesc)
    {
        this.roomID = roomID;
        this.roomName = roomName;
        this.roomDesc = roomDesc;

        this.visited = false;

        this.exits = new HashMap<>();
        this.roomInventory = new ArrayList<>();

        this.puzzle = null;
    }

    // ======================== // GETTERS // ========================
    public String getRoomID()
    {
        return roomID;
    }

    public String getRoomName()
    {
        return roomName;
    }

    public String getRoomDesc()
    {
        return roomDesc;
    }

    // ======================== // EXIT METHODS // ========================
    public void addExit(String direction, String roomID)
    {
        if (roomID != null && !roomID.equals("0"))
        {
            exits.put(direction.toLowerCase(), roomID);
        }
    }

    public String getExit(String direction)
    {
        return exits.get(direction.toLowerCase());
    }

    public Map<String, String> getAllExits()
    {
        return exits;
    }

    // ======================== // VISITED TRACKING // ========================
    public boolean isVisited()
    {
        return visited;
    }

    public void setVisited(boolean visited)
    {
        this.visited = visited;
    }

    // returns true if already visited, false if first time
    public boolean trackVisit()
    {
        if (visited)
        {
            return true;
        }

        visited = true;
        return false;
    }

    // ======================== // ROOM ITEMS // ========================
    public void addItem(Item item)
    {
        roomInventory.add(item);
    }

    public boolean removeItem(Item item)
    {
        return roomInventory.remove(item);
    }

    public Item removeItemByName(String itemName)
    {
        for (int i = 0; i < roomInventory.size(); i++)
        {
            if (roomInventory.get(i).getName().equalsIgnoreCase(itemName))
            {
                return roomInventory.remove(i);
            }
        }
        return null;
    }

    public ArrayList<Item> getItems()
    {
        return roomInventory;
    }

    public boolean hasItems()
    {
        return !roomInventory.isEmpty();
    }

    // ======================== // PUZZLE METHODS // ========================
    public void addPuzzle(Puzzle puzzle)
    {
        this.puzzle = puzzle;
    }

    public boolean hasPuzzle()
    {
        return puzzle != null && !puzzle.isSolved();
    }

    public Puzzle getPuzzle()
    {
        return puzzle;
    }

    // ======================== // PUZZLE INTERACTION // ========================
    public boolean attemptPuzzle(String answer)
    {
        if (puzzle == null)
        {
            return false;
        }

        if (puzzle.isSolved() || puzzle.isFailed())
        {
            return false;
        }

        if (answer.equalsIgnoreCase(puzzle.getAnswer()))
        {
            puzzle.setSolved(true);
            return true;
        }
        else
        {
            puzzle.decAttempts();
            return false;
        }
    }

    // ======================== // RESET PUZZLE // ========================
    public void resetPuzzle()
    {
        if (puzzle != null)
        {
            puzzle.resetPuzzle();
        }
    }
}