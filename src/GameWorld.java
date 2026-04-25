import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class GameWorld {

    // ======================== // VARIABLES // ========================
    private HashMap<String, Room> roomMap = new HashMap<>();
    private ArrayList<Item> itemList = new ArrayList<>();
    private ArrayList<Puzzle> puzzleList = new ArrayList<>();
    private ArrayList<Monster> monsterList = new ArrayList<>();

    // ======================== // ROOM LOADING // ========================
    public void loadRooms(String fileName) {
        try (Scanner scan = new Scanner(new File(fileName))) {

            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();

                if (line.equals("") || line.startsWith("roomID")) {
                    continue;
                }

                String[] parts = line.split(",");

                if (parts.length < 7) continue;

                String roomID = parts[0].trim();
                String roomName = parts[1].trim();
                String roomDesc = parts[2].trim();

                String north = parts[3].trim();
                String south = parts[4].trim();
                String east = parts[5].trim();
                String west = parts[6].trim();

                Room room = new Room(roomID, roomName, roomDesc);

                if (!north.equals("0")) room.addExit("north", "R_" + north);
                if (!south.equals("0")) room.addExit("south", "R_" + south);
                if (!east.equals("0")) room.addExit("east", "R_" + east);
                if (!west.equals("0")) room.addExit("west", "R_" + west);

                roomMap.put(roomID, room);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error loading rooms", e);
        }
    }

    // ======================== // ITEM LOADING // ========================
    public void loadItems(String fileName) {
        try (Scanner scan = new Scanner(new File(fileName))) {

            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();

                if (line.equals("") || line.startsWith("itemId")) continue;

                String[] parts = line.split(",");

                if (parts.length < 6) continue;

                String itemID = parts[0].trim();
                String itemName = parts[1].trim();
                String itemType = parts[2].trim();
                String itemDesc = parts[3].trim();
                int statValue = Integer.parseInt(parts[4].trim());
                String roomID = parts[5].trim();

                Item item = new Item(itemID, itemName, itemType, itemDesc, statValue);
                itemList.add(item);

                Room room = roomMap.get(roomID);
                if (room != null) {
                    room.addItem(item);
                }

            }

        } catch (Exception e) {
            throw new RuntimeException("Error loading items", e);
        }
    }

    // ======================== // PUZZLE LOADING // ========================
    public void loadPuzzles(String fileName) {
        try (Scanner scan = new Scanner(new File(fileName))) {

            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();

                if (line.equals("") || line.startsWith("puzzleID")) continue;

                String[] parts = line.split("\\|");

                if (parts.length < 9) continue;

                String puzzleID = parts[0].trim();
                String puzzleName = parts[1].trim();
                String puzzleDesc = parts[2].trim();
                String puzzleSolution = parts[3].trim();
                int numAttempts = Integer.parseInt(parts[4].trim());
                boolean isSolved = Boolean.parseBoolean(parts[5].trim());
                String outcome = parts[6].trim();
                String roomID = parts[8].trim();

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

                Room room = roomMap.get(roomID);
                if (room != null) {
                    room.addPuzzle(puzzle);
                }

            }

        } catch (Exception e) {
            throw new RuntimeException("Error loading puzzles", e);
        }
    }

    // ======================== // MONSTER LOADING (FIXED) ========================
    public void loadMonsters(String fileName) {
        try (Scanner scan = new Scanner(new File(fileName))) {

            while (scan.hasNextLine()) {
                String line = scan.nextLine().trim();

                if (line.equals("") || line.startsWith("monsterId")) continue;

                String[] parts = line.split("\\|");

                if (parts.length < 8) continue;

                String monsterID = parts[0].trim();
                String monsterName = parts[1].trim();
                String monsterDesc = parts[2].trim();
                boolean isDead = Boolean.parseBoolean(parts[3].trim());
                int health = Integer.parseInt(parts[4].trim());
                int attack = Integer.parseInt(parts[5].trim());
                String spawnType = parts[6].trim();
                String roomID = parts[7].trim();

                int roomNumber = -1;

                if (!roomID.equalsIgnoreCase("any")) {
                    roomNumber = Integer.parseInt(roomID.replace("R_", "").trim());
                }

                Monster monster = new Monster(
                        monsterID,
                        monsterName,
                        monsterDesc,
                        roomNumber,
                        health,
                        attack,
                        spawnType.equalsIgnoreCase("hostile")
                );

                if (isDead) {
                    monster.takeDamage(health);
                }

                monsterList.add(monster);

                if (!roomID.equalsIgnoreCase("any")) {
                    Room room = roomMap.get(roomID);
                    if (room != null) {
                        room.setMonster(monster);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error loading monsters", e);
        }
    }

    // ======================== // GETTERS // ========================
    public HashMap<String, Room> getRoomMap() {
        return roomMap;
    }

    public ArrayList<Item> getItemList() {
        return itemList;
    }

    public ArrayList<Puzzle> getPuzzleList() {
        return puzzleList;
    }

    public ArrayList<Monster> getMonsterList() {
        return monsterList;
    }
}