import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // ======================== // LOAD WORLD // ========================
        GameWorld world = new GameWorld();

        world.loadRooms("rooms.txt");
        world.loadItems("items.txt");
        world.loadPuzzles("puzzles.txt");
        world.loadMonsters("monsters.txt");

        // ======================== // MVC SETUP // ========================
        GameModel model = new GameModel(world);
        GameController controller = new GameController(model);
        GameView view = new GameView();

        Scanner input = new Scanner(System.in);

        System.out.println("You wake to silence. The mansion is unfamiliar; "
        		+ "closed doors and eerie halls hiding endless secrets.\n"
        		+ "Somewhere inside are the tools you'll need to escape... "
        		+ "and many dangers lurking in the shadows.\n"
        		+ "Explore carefully. Solve what you can. Survive what you must.\n"
        		+ "Type 'help' for a list of commands.");

        // ======================== // GAME LOOP // ========================
        while (true) {

            System.out.print("> ");
            String userInput = input.nextLine();

            if (userInput.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye.");
                break;
            }

            GameResult result = controller.processCommand(userInput);

            view.display(result, model);
        }

        input.close();
    }
}



