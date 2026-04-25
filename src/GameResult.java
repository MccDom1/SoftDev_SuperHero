public class GameResult {

    // ======================== // VARIABLES // ========================
    private String message;
    private boolean showRoom;
    private boolean showPuzzle;

    // ======================== // CONSTRUCTOR // ========================
    public GameResult(String message, boolean showRoom, boolean showPuzzle) {
        this.message = message;
        this.showRoom = showRoom;
        this.showPuzzle = showPuzzle;
    }

    // ======================== // GETTERS // ========================
    public String getMessage() {
        return message;
    }

    public boolean shouldShowRoom() {
        return showRoom;
    }

    public boolean shouldShowPuzzle() {
        return showPuzzle;
    }
}