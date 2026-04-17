/*
 * GameModel
 *
 * Notes to self:
 * - This is the core logic layer.
 * - This class should answer game-state questions and execute game actions.
 * - This class should NOT print directly.
 * - Instead, it returns a GameResult so the controller/view can decide what to display.
 *
 * This class is responsible for things like:
 * - movement
 * - room exploration
 * - item pickup / drop / inspect / equip
 * - puzzle access / puzzle solving
 */
public class GameModel {

    // The world holds all rooms/items/puzzles loaded from data files.
    private GameWorld world;

    // The player object tracks player-specific state.
    private Player player;

    /*
     * Constructor
     *
     * Notes to self:
     * - Load the world first.
     * - Create the player in the start room.
     * - Mark the start room as visited because the game begins there.
     */
    public GameModel() {
        this.world = new GameWorld();
        this.player = new Player(1);   // Room 1 = starting room

        Room startRoom = getCurrentRoom();
        if (startRoom != null) {
            startRoom.markVisited();
        }
    }

    // -----------------------------
    // Basic getters
    // -----------------------------

    public Player getPlayer() {
        return player;
    }

    public GameWorld getWorld() {
        return world;
    }

    public Room getCurrentRoom() {
        return world.getRoom(player.getCurrentRoomId());
    }

    public Puzzle getCurrentPuzzle() {
        Room room = getCurrentRoom();

        // Keep this safe. If room is somehow missing, just return null.
        return room != null ? room.getPuzzle() : null;
    }

    public boolean hasUnsolvedPuzzle() {
        Puzzle puzzle = getCurrentPuzzle();
        return puzzle != null && !puzzle.isSolved();
    }

    // -----------------------------
    // Movement
    // -----------------------------

    public GameResult movePlayer(String direction) {
        /*
         * Notes to self:
         * - Ask current room for the exit in the given direction.
         * - If the exit does not exist, return a clean message.
         * - If it does exist, update the player room id.
         * - Then build the message for the next room.
         * - Also check if that room contains an unsolved puzzle.
         */

        Room currentRoom = getCurrentRoom();

        if (currentRoom == null) {
            return new GameResult("Current room is invalid.");
        }

        int nextRoomId = currentRoom.getExit(direction);

        // By convention, -1 means "no exit in that direction".
        if (nextRoomId == -1) {
            return new GameResult("You cannot travel this way!");
        }

        Room nextRoom = world.getRoom(nextRoomId);

        if (nextRoom == null) {
            return new GameResult("That room does not exist.");
        }

        // Update player location first.
        player.setCurrentRoomId(nextRoomId);

        StringBuilder message = new StringBuilder();
        message.append(nextRoom.getName()).append("\n");
        message.append(nextRoom.getDescription());

        if (nextRoom.isVisited()) {
            message.append("\nYou have visited this room before.");
        } else {
            nextRoom.markVisited();
        }

        GameResult result = new GameResult(message.toString());

        // If the new room has an unsolved puzzle, tell the UI layer it may need to show it.
        if (nextRoom.getPuzzle() != null && !nextRoom.getPuzzle().isSolved()) {
            result.setShowPuzzle(true);
        }

        return result;
    }

    // -----------------------------
    // Exploration
    // -----------------------------

    public GameResult exploreRoom() {
        /*
         * Notes to self:
         * - Exploring should not move the player.
         * - It should simply tell the player what is here.
         * - Keep room description, items, and puzzle status all together.
         */

        Room room = getCurrentRoom();

        if (room == null) {
            return new GameResult("Current room is invalid.");
        }

        StringBuilder message = new StringBuilder();
        message.append(room.getName()).append("\n");
        message.append(room.getDescription());

        if (!room.getItems().isEmpty()) {
            message.append("\nItems in this room:");
            for (Item item : room.getItems()) {
                message.append("\n- ").append(item.getName());
            }
        } else {
            message.append("\nThere is nothing to explore in this region.");
        }

        if (room.getPuzzle() != null && !room.getPuzzle().isSolved()) {
            message.append("\nThere is an unsolved puzzle here.");
        }

        GameResult result = new GameResult(message.toString());

        if (room.getPuzzle() != null && !room.getPuzzle().isSolved()) {
            result.setShowPuzzle(true);
        }

        return result;
    }

    // -----------------------------
    // Inventory display
    // -----------------------------

    public GameResult getInventory() {
        /*
         * Notes to self:
         * - Keep inventory formatting simple and clean.
         * - Also show which item is equipped.
         */

        if (player.getInventory().isEmpty()) {
            return new GameResult("Your inventory is empty.");
        }

        StringBuilder message = new StringBuilder("Inventory:");
        for (Item item : player.getInventory()) {
            message.append("\n- ").append(item.getName());

            if (player.getEquippedItem() == item) {
                message.append(" (equipped)");
            }
        }

        return new GameResult(message.toString());
    }

    // -----------------------------
    // Pick up item
    // -----------------------------

    public GameResult pickupItem(String itemName) {
        /*
         * Notes to self:
         * - First validate room.
         * - Then validate input.
         * - Then search room for the item.
         * - If found, remove from room and add to player.
         */

        Room room = getCurrentRoom();

        if (room == null) {
            return new GameResult("Current room is invalid.");
        }

        if (itemName == null || itemName.trim().isEmpty()) {
            return new GameResult("You must specify an item to pick up.");
        }

        Item item = room.findItem(itemName);

        if (item == null) {
            return new GameResult("That item is not in this room.");
        }

        room.removeItem(item);
        player.addItem(item);

        return new GameResult(item.getName() + " was added to your inventory.");
    }

    // -----------------------------
    // Drop item
    // -----------------------------

    public GameResult dropItem(String itemName) {
        /*
         * Notes to self:
         * - Item must exist in player inventory before it can be dropped.
         * - If it is equipped, clear the equipped state after dropping.
         */

        Room room = getCurrentRoom();

        if (room == null) {
            return new GameResult("Current room is invalid.");
        }

        if (itemName == null || itemName.trim().isEmpty()) {
            return new GameResult("You must specify an item to drop.");
        }

        Item item = player.findItemInInventory(itemName);

        if (item == null) {
            return new GameResult("That item is not in your inventory.");
        }

        player.removeItem(item);
        room.addItem(item);
        item.setRoomId(room.getRoomId());

        if (player.getEquippedItem() == item) {
            player.clearEquippedItem();
        }

        return new GameResult(item.getName() + " was dropped into the room.");
    }

    // -----------------------------
    // Inspect item
    // -----------------------------

    public GameResult inspectItem(String itemName) {
        /*
         * Notes to self:
         * - Try inventory first because that is the more likely player expectation.
         * - Then try the room.
         * - Return a clean error if not found anywhere.
         */

        if (itemName == null || itemName.trim().isEmpty()) {
            return new GameResult("You must specify an item to inspect.");
        }

        Item inventoryItem = player.findItemInInventory(itemName);
        if (inventoryItem != null) {
            return new GameResult(inventoryItem.getDescription());
        }

        Room room = getCurrentRoom();
        if (room != null) {
            Item roomItem = room.findItem(itemName);
            if (roomItem != null) {
                return new GameResult(roomItem.getDescription());
            }
        }

        return new GameResult("That item could not be found.");
    }

    // -----------------------------
    // Equip item
    // -----------------------------

    public GameResult equipItem(String itemName) {
        /*
         * Notes to self:
         * - Keep equip logic simple for now.
         * - The player must own the item before it can be equipped.
         */

        if (itemName == null || itemName.trim().isEmpty()) {
            return new GameResult("You must specify an item to equip.");
        }

        Item item = player.findItemInInventory(itemName);

        if (item == null) {
            return new GameResult("That item is not in your inventory.");
        }

        player.setEquippedItem(item);
        return new GameResult(item.getName() + " is now equipped.");
    }

    // -----------------------------
    // Puzzle solve
    // -----------------------------

    public GameResult solvePuzzle(String answer) {
        /*
         * Notes to self:
         * - Puzzle must exist.
         * - Puzzle must still be unsolved.
         * - Answer must not be blank.
         * - If wrong, keep the puzzle visible.
         */

        Puzzle puzzle = getCurrentPuzzle();

        if (puzzle == null) {
            return new GameResult("There is no puzzle in this room.");
        }

        if (puzzle.isSolved()) {
            return new GameResult("This puzzle has already been solved.");
        }

        if (answer == null || answer.trim().isEmpty()) {
            return new GameResult("You must enter an answer.");
        }

        boolean solved = puzzle.solve(answer.trim());

        if (solved) {
            return new GameResult("Correct! The puzzle has been solved.");
        }

        GameResult result = new GameResult("Incorrect answer. Try again.");
        result.setShowPuzzle(true);
        return result;
    }

    // -----------------------------
    // Optional helper: starting room text
    // -----------------------------

    public GameResult getStartingRoomResult() {
        /*
         * Notes to self:
         * - Handy if the controller wants a clean "start game" response.
         * - This avoids repeating room display logic elsewhere.
         */

        Room room = getCurrentRoom();

        if (room == null) {
            return new GameResult("Starting room could not be found.");
        }

        GameResult result = new GameResult(room.getName() + "\n" + room.getDescription());

        if (room.getPuzzle() != null && !room.getPuzzle().isSolved()) {
            result.setShowPuzzle(true);
        }

        return result;
    }
}