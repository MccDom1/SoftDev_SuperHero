import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * GameWorld
 *
 * Notes to self:
 * This class loads the world data from the text files.
 * Keep all file-reading logic here so the rest of the game does not need
 * to know how rooms, items, puzzles, or monsters are stored.
 */
public class GameWorld {
    private final java.util.Map<Integer, Room> rooms;
    private final java.util.Map<String, Item> itemsById;
    private final java.util.Map<String, Puzzle> puzzlesById;
    private final java.util.Map<String, Monster> monstersById;

    public GameWorld() {
        rooms = new HashMap<>();
        itemsById = new HashMap<>();
        puzzlesById = new HashMap<>();
        monstersById = new HashMap<>();

        try {
            loadRooms("Rooms.txt");
            loadItems("Items.txt");
            loadPuzzles("PuzzleTextFile.txt");
            loadMonsters("Monsters.txt");
            assignRoomContents();
        } catch (IOException e) {
            System.out.println("Error loading world data: " + e.getMessage());
        }
    }

    public Room getRoom(int roomId) {
        return rooms.get(roomId);
    }

    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public Item getItemById(String itemId) {
        return itemsById.get(itemId);
    }

    public Monster getMonsterById(String monsterId) {
        return monstersById.get(monsterId);
    }

    /*
     * Notes to self:
     * Room IDs in text files use R_1, R_2, etc.
     * The game logic uses integer IDs internally.
     */
    private int parseRoomNumber(String roomCode) {
        return Integer.parseInt(roomCode.replace("R_", "").trim());
    }

    /*
     * Notes to self:
     * Rooms.txt format:
     * roomID, roomName, roomDescription, north, south, east, west
     */
    private void loadRooms(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().startsWith("roomid")) {
                        continue;
                    }
                }

                /*
                 * Notes to self:
                 * Room descriptions may contain commas, so do not use a simple split.
                 * Read the first two fields normally, then read the last four fields
                 * as directions from the end of the line.
                 */
                String[] firstParts = line.split(",", 3);

                if (firstParts.length < 3) {
                    continue;
                }

                String roomCode = firstParts[0].trim();
                String roomName = firstParts[1].trim();
                String remaining = firstParts[2].trim();

                int lastComma1 = remaining.lastIndexOf(",");
                int lastComma2 = remaining.lastIndexOf(",", lastComma1 - 1);
                int lastComma3 = remaining.lastIndexOf(",", lastComma2 - 1);
                int lastComma4 = remaining.lastIndexOf(",", lastComma3 - 1);

                if (lastComma1 == -1 || lastComma2 == -1 || lastComma3 == -1 || lastComma4 == -1) {
                    continue;
                }

                String roomDesc = remaining.substring(0, lastComma4).trim();

                int north = Integer.parseInt(remaining.substring(lastComma4 + 1, lastComma3).trim());
                int south = Integer.parseInt(remaining.substring(lastComma3 + 1, lastComma2).trim());
                int east = Integer.parseInt(remaining.substring(lastComma2 + 1, lastComma1).trim());
                int west = Integer.parseInt(remaining.substring(lastComma1 + 1).trim());

                int roomId = parseRoomNumber(roomCode);

                Room room = new Room(roomId, roomCode, roomName, roomDesc);

                room.addExit("N", north);
                room.addExit("S", south);
                room.addExit("E", east);
                room.addExit("W", west);

                rooms.put(roomId, room);
            }
        }
    }

    /*
     * Notes to self:
     * Items.txt format:
     * itemId, itemName, itemType, itemDescription, StatValue
     */
    private void loadItems(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().startsWith("itemid")) {
                        continue;
                    }
                }

                /*
                 * Notes to self:
                 * Item descriptions may contain commas, so do not split the whole line normally.
                 * First grab itemId, itemName, and itemType from the front.
                 * Then grab StatValue from the very last comma.
                 * Everything between itemType and StatValue is the description.
                 */
                String[] firstParts = line.split(",", 4);

                if (firstParts.length < 4) {
                    continue;
                }

                String itemId = firstParts[0].trim();
                String itemName = firstParts[1].trim();
                String itemType = firstParts[2].trim();
                String remaining = firstParts[3].trim();

                int lastComma = remaining.lastIndexOf(",");

                if (lastComma == -1) {
                    continue;
                }

                String itemDescription = remaining.substring(0, lastComma).trim();
                int statValue = Integer.parseInt(remaining.substring(lastComma + 1).trim());

                int healthEffect = 0;
                int damageEffect = 0;

                if (itemType.equalsIgnoreCase("consumable")) {
                    healthEffect = statValue;

                    if (itemName.equalsIgnoreCase("Milk")) {
                        healthEffect = -statValue;
                    }
                } else if (itemType.equalsIgnoreCase("weapon") || itemType.equalsIgnoreCase("equipment")) {
                    damageEffect = statValue;
                }

                boolean consumable = itemType.equalsIgnoreCase("consumable");
                boolean equippable = itemType.equalsIgnoreCase("weapon") || itemType.equalsIgnoreCase("equipment");

                /*
                 * roomId starts at 0 because this file defines the item itself.
                 * Room placement can happen through puzzle, monster, or map logic.
                 */
                Item item = new Item(
                        itemId,
                        itemName,
                        itemType,
                        itemDescription,
                        healthEffect,
                        damageEffect,
                        0,
                        consumable,
                        equippable
                );

                itemsById.put(item.getId(), item);
            }
        }
    }

    /*
     * Notes to self:
     * PuzzleTextFile.txt format:
     * puzzleID | puzzleName | puzzleDescription | puzzleSolution | numAttempts | isSolved | puzzleOutcome | roomName | roomID
     */
    private void loadPuzzles(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean firstLine = true;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().startsWith("puzzleid")) {
                        continue;
                    }
                }

                /*
                 * Notes to self:
                 * Puzzle file uses pipe separators.
                 * Keep empty or broken lines from crashing the game.
                 */
                String[] parts = line.split("\\|");

                if (parts.length < 9) {
                    continue;
                }

                String puzzleId = parts[0].trim();
                String puzzleName = parts[1].trim();
                String puzzleDescription = parts[2].trim();
                String puzzleSolution = parts[3].trim();
                int numAttempts = Integer.parseInt(parts[4].trim());
                String puzzleOutcome = parts[6].trim();
                String roomName = parts[7].trim();
                String roomId = parts[8].trim();

                Puzzle puzzle = new Puzzle(
                        puzzleId,
                        puzzleName,
                        roomId,
                        roomName,
                        puzzleDescription,
                        puzzleSolution,
                        numAttempts,
                        puzzleOutcome,
                        "Correct! " + puzzleOutcome,
                        "Incorrect answer. The puzzle resets."
                );

                puzzlesById.put(puzzle.getPuzzleId(), puzzle);
            }
        }
    }

    /*
     * Notes to self:
     * Monsters.txt format:
     * monsterId, monsterName, monsterDescr, isDead, healthPoints, attackPoints, spawnLocationType, roomID
     */
    private void loadMonsters(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean firstLine = true;
            boolean skipLegend = false;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) {
                    continue;
                }

                if (firstLine) {
                    firstLine = false;
                    if (line.toLowerCase().startsWith("monsterid")) {
                        continue;
                    }
                }

                /*
                 * Skip the explanatory spawnLocationType section.
                 */
                if (line.toLowerCase().startsWith("spawnlocationtype")) {
                    skipLegend = true;
                    continue;
                }

                if (skipLegend) {
                    if (!line.startsWith("M_")) {
                        continue;
                    }
                    skipLegend = false;
                }

                if (!line.startsWith("M_")) {
                    continue;
                }

                /*
                 * Notes to self:
                 * Monster descriptions may contain commas, so do not split the whole line normally.
                 * Read monsterId and monsterName from the front.
                 * Then read the last five fields from the end:
                 * isDead, healthPoints, attackPoints, spawnLocationType, roomID.
                 * Everything between monsterName and isDead is the description.
                 */
                String[] firstParts = line.split(",", 3);

                if (firstParts.length < 3) {
                    continue;
                }

                String monsterId = firstParts[0].trim();
                String monsterName = firstParts[1].trim();
                String remaining = firstParts[2].trim();

                int lastComma1 = remaining.lastIndexOf(",");
                int lastComma2 = remaining.lastIndexOf(",", lastComma1 - 1);
                int lastComma3 = remaining.lastIndexOf(",", lastComma2 - 1);
                int lastComma4 = remaining.lastIndexOf(",", lastComma3 - 1);
                int lastComma5 = remaining.lastIndexOf(",", lastComma4 - 1);

                if (lastComma1 == -1 || lastComma2 == -1 || lastComma3 == -1 ||
                        lastComma4 == -1 || lastComma5 == -1) {
                    continue;
                }

                String monsterDescr = remaining.substring(0, lastComma5).trim();
                boolean isDead = Boolean.parseBoolean(remaining.substring(lastComma5 + 1, lastComma4).trim());
                int healthPoints = Integer.parseInt(remaining.substring(lastComma4 + 1, lastComma3).trim());
                int attackPoints = Integer.parseInt(remaining.substring(lastComma3 + 1, lastComma2).trim());
                String spawnLocationType = remaining.substring(lastComma2 + 1, lastComma1).trim();
                String roomID = remaining.substring(lastComma1 + 1).trim();

                /*
                 * For now, fixed and special room monsters get attached to the first listed room.
                 * Random monsters are loaded but not attached directly to a specific room.
                 */
                if (roomID.equalsIgnoreCase("any")) {
                    Monster monster = new Monster(
                            monsterId,
                            monsterName,
                            monsterDescr,
                            0,
                            healthPoints,
                            attackPoints,
                            true
                    );

                    if (isDead) {
                        monster.takeDamage(healthPoints);
                    }

                    monstersById.put(monster.getId(), monster);
                    continue;
                }

                String firstRoom = roomID.split("\\|")[0].trim();
                int roomNumber = parseRoomNumber(firstRoom);

                boolean hostile = !monsterName.equalsIgnoreCase("Butterfly")
                        && !monsterName.equalsIgnoreCase("Butler");

                Monster monster = new Monster(
                        monsterId,
                        monsterName,
                        monsterDescr,
                        roomNumber,
                        healthPoints,
                        attackPoints,
                        hostile
                );

                if (isDead) {
                    monster.takeDamage(healthPoints);
                }

                monstersById.put(monster.getId(), monster);
            }
        }
    }

    /*
     * Notes to self:
     * After loading files separately, attach puzzles and monsters to rooms.
     */
    private void assignRoomContents() {
        for (Puzzle puzzle : puzzlesById.values()) {
            int roomNumber = parseRoomNumber(puzzle.getRoomId());
            Room room = rooms.get(roomNumber);

            if (room != null) {
                room.setPuzzle(puzzle);
            }
        }

        for (Monster monster : monstersById.values()) {
            if (monster.getRoomId() > 0) {
                Room room = rooms.get(monster.getRoomId());

                if (room != null) {
                    room.setMonster(monster);
                }
            }
        }
    }
}