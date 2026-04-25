public class GameView {

    // ======================== // DISPLAY RESULT // ========================
    public void display(GameResult result, GameModel model) {

        // Print main message
        System.out.println(result.getMessage());

        // Show room if needed
        if (result.shouldShowRoom()) {
            Room room = model.getCurrentRoom();

            System.out.println("\n" + room.getRoomName());
            System.out.println(room.getRoomDesc());

            // Show items in room
            if (!room.getItems().isEmpty()) {
                System.out.println("\nItems here:");
                for (Item item : room.getItems()) {
                    System.out.println("- " + item.getName());
                }
            }

            // Show puzzle hint
            if (room.hasPuzzle()) {
                System.out.println("\nThere is a puzzle here.");
            }
        }

        // Show puzzle prompt if needed
        if (result.shouldShowPuzzle()) {
            Room room = model.getCurrentRoom();

            if (room.hasPuzzle()) {
                Puzzle puzzle = room.getPuzzle();

                System.out.println("\nPuzzle:");
                System.out.println(puzzle.getDescription());
                System.out.println("Attempts left: " + puzzle.getRemainingAttempts());
            }
        }
    }
}