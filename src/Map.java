import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

// ======================== // MAP DATA // ========================
public class Map
{
    private HashMap<String, Room> roomMap = new HashMap<>();
    private ArrayList<Item> itemList = new ArrayList<>();
    private ArrayList<Puzzle> puzzleList = new ArrayList<>();
    private ArrayList<Monster> monsterList = new ArrayList<>();

    // ======================== // ROOM LOADING // ========================
    public HashMap<String, Room> loadRooms(String line)
    {
        try
        {
            // ======================== // FILE PARSING // ========================
            String[] parts = line.split(",");

            if(parts.length < 7)
            {
                return roomMap;
            }

            // ======================== // ROOM DATA // ========================
            String roomID = parts[0].trim();
            String roomName = parts[1].trim();
            String roomDesc = parts[2].trim();

            // ======================== // ROOM EXITS // ========================
            String north = parts[3].trim();
            String south = parts[4].trim();
            String east  = parts[5].trim();
            String west  = parts[6].trim();

            // ======================== // ROOM CREATION // ========================
            Room room = new Room(roomID, roomName, roomDesc);

            // ======================== // ADD EXITS // ========================
            if (!north.equals("0"))
            {
                room.addExit("north", "R_" + north);
            }

            if (!south.equals("0"))
            {
                room.addExit("south", "R_" + south);
            }

            if (!east.equals("0"))
            {
                room.addExit("east", "R_" + east);
            }

            if (!west.equals("0"))
            {
                room.addExit("west", "R_" + west);
            }

            // ======================== // STORE ROOM // ========================
            roomMap.put(roomID, room);
            return roomMap;
        }

        catch (Exception e)
        {
            return roomMap;
        }
    }

    // ======================== // ITEM LOADING // ========================
    public ArrayList<Item> loadItems(String fileName)
    {
        try(Scanner scan = new Scanner(new File(fileName)))
        {
            while(scan.hasNextLine())
            {
                String line = scan.nextLine();

                if(line.equals("") || line.startsWith("itemId"))
                {
                    continue;
                }

                String[] parts = line.split(",");

                // ======================== // FILE CHECK // ========================

                if(parts.length < 5)
                {
                    continue;
                }

                // ======================== // ITEM DATA (FIXED ORDER) // ========================

                String itemID = parts[0].trim();
                String itemName = parts[1].trim();
                String itemType = parts[2].trim();
                String itemDesc = parts[3].trim();
                int statValue = Integer.parseInt(parts[4].trim());

                Item item = new Item(itemID, itemName, itemType, itemDesc, statValue);

                itemList.add(item);
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException("Error while loading items", e);
        }

        return itemList;
    }

    // ======================== // PUZZLE LOADING // ========================
    public ArrayList<Puzzle> loadPuzzles(String fileName)
    {
        try(Scanner scan = new Scanner(new File(fileName)))
        {
            while(scan.hasNextLine())
            {
                String line = scan.nextLine();

                if(line.equals("") || line.startsWith("puzzleID"))
                {
                    continue;
                }

                // Puzzle text file format 
                String[] parts = line.split("\\|");

                if(parts.length < 9)
                {
                    continue;
                }

                // ======================== // PUZZLE DATA // ========================
                String puzzleID = parts[0].trim();
                String puzzleName = parts[1].trim();
                String puzzleDesc = parts[2].trim();
                String puzzleSolution = parts[3].trim();
                int numAttempts = Integer.parseInt(parts[4].trim());
                boolean isSolved = Boolean.parseBoolean(parts[5].trim());
                String outcome = parts[6].trim();
                String roomID = parts[8].trim(); //

                Puzzle puzzle = new Puzzle(
                        puzzleID,
                        puzzleName,
                        puzzleDesc,
                        puzzleSolution,
                        numAttempts,
                        isSolved,
                        outcome
                );

                puzzleList.add(puzzle);

                // ======================== // ADD TO ROOM // ========================
                Room room = roomMap.get(roomID);

                if(room != null)
                {
                    room.addPuzzle(puzzle);
                }
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException("Error while loading puzzle", e);
        }

        return puzzleList;
    }

    // ======================== // MONSTER LOADING // ========================
    public ArrayList<Monster> loadMonsters(String fileName)
    {
        try(Scanner scan = new Scanner(new File(fileName)))
        {
            while(scan.hasNextLine())
            {
                String line = scan.nextLine().trim();

                if(line.equals("") || line.startsWith("monsterId"))
                {
                    continue;
                }

                String[] parts = line.split(",");

                if(parts.length < 8)
                {
                    continue;
                }

                String monsterID = parts[0].trim();
                String monsterName = parts[1].trim();
                String monsterDesc = parts[2].trim();
                boolean isDead = Boolean.parseBoolean(parts[3].trim());
                int health = Integer.parseInt(parts[4].trim());
                int attack = Integer.parseInt(parts[5].trim());
                String spawnType = parts[6].trim();
                String roomID = parts[7].trim();

                Monster monster = new Monster(monsterID, monsterName, monsterDesc,
                                              isDead, health, attack, spawnType, roomID);

                monsterList.add(monster);
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException("Error while loading monsters", e);
        }

        return monsterList;
    }

    // ======================== // GETTERS // ========================
    public HashMap<String, Room> getRoomMap()
    {
        return roomMap;
    }

    public ArrayList<Item> getItemList()
    {
        return itemList;
    }

    public ArrayList<Puzzle> getPuzzleList()
    {
        return puzzleList;
    }
}