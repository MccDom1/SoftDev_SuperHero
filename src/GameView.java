public class GameView {

    public void display(GameResult result, GameModel model) {

        if (result.getMessage() != null && !result.getMessage().trim().isEmpty()) {
            System.out.println(result.getMessage());
        }

        if (result.shouldShowRoom()) {
            Room room = model.getCurrentRoom();

            if (room != null) {
                System.out.println("\nYou're in the " + room.getRoomName() + ".");
                System.out.println(room.getRoomDesc());

                if (!room.getItems().isEmpty()) {
                    System.out.println("\nItems here:");
                    for (Item item : room.getItems()) {
                        System.out.println("- " + item.getName());
                    }
                }

                if (room.hasPuzzle()) {
                    System.out.println("\nThere is a puzzle here.");
                }

                if (room.hasMonster()) {
                    System.out.println("\nMonster here: " + room.getMonster().getName());
                }
            }
        }

        if (result.shouldShowPuzzle()) {
            Room room = model.getCurrentRoom();

            if (room != null && room.hasPuzzle()) {
                Puzzle puzzle = room.getPuzzle();

                System.out.println("\nPuzzle:");
                System.out.println(puzzle.getDescription());
                System.out.println("Attempts left: " + puzzle.getRemainingAttempts());
                System.out.println("Submit answer with: solve <answer>");
            }
        }
    }
}