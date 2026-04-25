public class GameResult {

    private String message;
    private boolean showRoom;
    private boolean showPuzzle;
    private boolean gameOver;

    public GameResult(String message, boolean showRoom, boolean showPuzzle) {
        this.message = message;
        this.showRoom = showRoom;
        this.showPuzzle = showPuzzle;
        this.gameOver = false;
    }

    public GameResult(String message, boolean showRoom, boolean showPuzzle, boolean gameOver) {
        this.message = message;
        this.showRoom = showRoom;
        this.showPuzzle = showPuzzle;
        this.gameOver = gameOver;
    }

    public String getMessage() {
        return message;
    }

    public boolean shouldShowRoom() {
        return showRoom;
    }

    public boolean shouldShowPuzzle() {
        return showPuzzle;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}