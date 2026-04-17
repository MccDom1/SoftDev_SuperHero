/*
 * GameResult
 *
 * Notes to self:
 * - The model should not print directly.
 * - Instead, the model returns one of these "result packages."
 * - This gives the controller / view a clean object to work with.
 *
 * For now this package carries:
 * 1. A message to display
 * 2. Whether the puzzle should be shown after the action
 *
 * This class is intentionally small.
 * If the project grows later, this can also carry:
 * - combat flags
 * - warning flags
 * - game over state
 * - room refresh state
 */
public class GameResult {

    private String message;
    private boolean showPuzzle;

    public GameResult(String message) {
        this.message = message;
        this.showPuzzle = false;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean shouldShowPuzzle() {
        return showPuzzle;
    }

    public void setShowPuzzle(boolean showPuzzle) {
        this.showPuzzle = showPuzzle;
    }
}