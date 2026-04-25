public class GameView {

    public void display(GameResult result, GameModel model) {

        if (result.getMessage() != null && !result.getMessage().trim().isEmpty()) {
            System.out.println(result.getMessage());
        }

        if (result.shouldShowRoom()) {
            Room room = model.getCurrentRoom();

            if (room != null) {
                System.out.println("\n" + room.getRoomName());

                if (!room.getItems().isEmpty()) {
                    System.out.println("\nItems here:");
                    for (Item item : room.getItems()) {
                        System.out.println("- " + item.getName());
                    }
                    System.out.println("Available item commands: pickup <item>, loot <item>, inspect <item>, use <item>, equip <item>, drop <item>");
                }

                if (room.hasPuzzle()) {
                    System.out.println("\nPuzzle here: " + room.getPuzzle().getPuzzleName());
                    System.out.println("Available puzzle commands: interact puzzle, solve <answer>, ignore puzzle");
                }

                if (room.hasMonster()) {
                    System.out.println("\nMonster here: " + room.getMonster().getName());
                    System.out.println("Available monster commands: inspect monster, attack, defend, flee");
                }

                System.out.println("\nGeneral commands: n, s, e, w, explore, exits, inventory, status, help, quit");
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
                System.out.println("Other options: ignore puzzle");
            }
        }
    }
}