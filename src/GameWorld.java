/**
 * GameWorld Class Author Dominique M.
 *
 * PURPOSE:
 * Responsible for loading and storing all game data from external files.
 * This includes rooms, items, puzzles, and monsters.
 *
 * DESIGN:
 * - Acts as the data layer of the game
 * - Reads from text files instead of hardcoding values
 * - Connects objects together after loading (rooms → items → puzzles → monsters)
 *
 * NOTE:
 * This keeps the game flexible. The world can be changed just by editing
 * the text files without modifying the code.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameWorld {

    // Stores all rooms by their ID for quick lookup
    private final Map<Integer, Room> rooms;

    // Stores all items by ID
    private final Map<String, Item> itemsById;

    // Stores all puzzles by ID
    private final Map<String, Puzzle> puzzlesById;

    // Stores all monsters by ID
    private final Map<String, Monster> monstersById;

    /**
     * Constructor
     *
     * Loads all game data from files and connects everything together.
     */
    public GameWorld() {
        rooms = new HashMap<>();
        itemsById = new HashMap<>();
        puzzlesById = new HashMap<>();
        monstersById = new HashMap<>();

        try {
            // Load all data from external files
            loadRooms("Rooms.txt");
            loadItems("Items.txt");
            loadPuzzles("PuzzleTextFile.txt");
            loadMonsters("Monsters.txt");

            // After loading, link items/puzzles/monsters to their rooms
            assignRoomContents();

        } catch (IOException e) {
            // Fail fast if something goes wrong with loading
            throw new RuntimeException("Error loading world data: " + e.getMessage(), e);
        }
    }

    // Returns a room by ID
    public Room getRoom(int roomId) {
        return rooms.get(roomId);
    }

    // Returns a list of all rooms
    public List<Room> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    // Returns an item by ID
    public Item getItemById(String itemId) {
        return itemsById.get(itemId);
    }

    // Returns a monster by ID
    public Monster getMonsterById(String monsterId) {
        return monstersById.get(monsterId);
    }

    /**
     * Connects items, puzzles, and monsters to their corresponding rooms.
     * This is done after loading everything separately.
     */
    private void assignRoomContents() {

        // Assign items to rooms
        for (Item item : itemsById.values()) {
            if (item.getRoomId() > 0) {
                Room room = rooms.get(item.getRoomId());
                if (room != null) {
                    room.addItem(item);
                }
            }
        }

        // Assign puzzles to rooms (uses room code like R_1 → 1)
        for (Puzzle puzzle : puzzlesById.values()) {
            Integer roomId = parseRoomCode(puzzle.getRoomId());
            if (roomId != null) {
                Room room = rooms.get(roomId);
                if (room != null) {
                    room.setPuzzle(puzzle);
                }
            }
        }

        // Assign monsters to rooms
        for (Monster monster : monstersById.values()) {
            Room room = rooms.get(monster.getRoomId());
            if (room != null) {
                room.setMonster(monster);
            }
        }
    }

    /**
     * Converts room codes like "R_1" into integer IDs.
     */
    private Integer parseRoomCode(String code) {
        if (code == null) return null;

        String normalized = code.trim().toUpperCase();

        if (normalized.startsWith("R_")) {
            try {
                return Integer.parseInt(normalized.substring(2));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    /**
     * Loads room data from file.
     * Format:
     * id|code|name|description|north|east|south|west
     */
    private void loadRooms(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {

                // Skip empty lines and comments
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|");
                if (parts.length < 8) continue;

                int id = Integer.parseInt(parts[0].trim());
                String code = parts[1].trim();
                String name = parts[2].trim();
                String desc = parts[3].trim();

                // Create room object
                Room room = new Room(id, code, name, desc);

                // Add exits (N, E, S, W)
                room.addExit("N", Integer.parseInt(parts[4].trim()));
                room.addExit("E", Integer.parseInt(parts[5].trim()));
                room.addExit("S", Integer.parseInt(parts[6].trim()));
                room.addExit("W", Integer.parseInt(parts[7].trim()));

                rooms.put(id, room);
            }
        }
    }

    /**
     * Loads item data from file.
     */
    private void loadItems(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|");
                if (parts.length < 9) continue;

                // Create item object
                Item item = new Item(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim(),
                        Integer.parseInt(parts[4].trim()),
                        Integer.parseInt(parts[5].trim()),
                        Integer.parseInt(parts[6].trim()),
                        Boolean.parseBoolean(parts[7].trim()),
                        Boolean.parseBoolean(parts[8].trim())
                );

                itemsById.put(item.getId(), item);
            }
        }
    }

    /**
     * Loads puzzle data from file.
     */
    private void loadPuzzles(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|");
                if (parts.length < 10) continue;

                Puzzle puzzle = new Puzzle(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim(),
                        parts[4].trim(),
                        parts[5].trim(),
                        Integer.parseInt(parts[6].trim()),
                        parts[7].trim(),
                        parts[8].trim(),
                        parts[9].trim()
                );

                puzzlesById.put(puzzle.getPuzzleId(), puzzle);
            }
        }
    }

    /**
     * Loads monster data from file.
     */
    private void loadMonsters(String fileName) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|");
                if (parts.length < 8) continue;

                Monster monster = new Monster(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        Integer.parseInt(parts[3].trim()),
                        Integer.parseInt(parts[4].trim()),
                        Integer.parseInt(parts[5].trim()),
                        Boolean.parseBoolean(parts[6].trim())
                );

                // Load loot if present
                if (!parts[7].trim().isEmpty()) {
                    String[] loot = parts[7].split(",");
                    for (String lootId : loot) {
                        monster.addLootId(lootId.trim());
                    }
                }

                monstersById.put(monster.getId(), monster);
            }
        }
    }
}