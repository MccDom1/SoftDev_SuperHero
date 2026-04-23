/**
 * GameResult Class Author Dominique M.
 *
 * PURPOSE:
 * Acts as a communication layer between the game logic (Model) and the View.
 * Instead of printing directly from the model, this class returns structured
 * information about the result of a player action.
 *
 * DESIGN:
 * - Supports separation of concerns (keeps logic independent from UI)
 * - Provides a message for display and flags for game state (puzzle/combat)
 *
 * NOTE:
 * This design makes the system easier to extend (e.g., GUI or web version)
 * and keeps the game flow organized and maintainable.
 */

public class GameResult {

    // Primary message returned to the View layer for display
    private String message;

    // Flag indicating whether the UI should transition into puzzle mode
    private boolean showPuzzle;

    // Flag indicating whether the player is currently engaged in combat
    private boolean inCombat;

    /**
     * Constructor initializes a default result state.
     *
     * REFLECTION:
     * Default flags are set to false to ensure predictable behavior.
     * This prevents accidental triggering of puzzle or combat states
     * unless explicitly set by the game logic.
     */
    public GameResult(String message) {
        this.message = message;
        this.showPuzzle = false;
        this.inCombat = false;
    }

    /**
     * Returns the message describing the result of a player action.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Updates the result message.
     *
     * REFLECTION:
     * Setter allows flexibility when multiple systems (movement, combat, puzzle)
     * contribute to the final output message.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Indicates whether the View should display a puzzle interaction.
     */
    public boolean shouldShowPuzzle() {
        return showPuzzle;
    }

    /**
     * Sets whether a puzzle should be triggered.
     *
     * REFLECTION:
     * This flag is intentionally simple to keep the controller logic clean.
     * The actual puzzle logic is handled elsewhere (Puzzle class / GameModel).
     */
    public void setShowPuzzle(boolean showPuzzle) {
        this.showPuzzle = showPuzzle;
    }

    /**
     * Indicates whether the player is currently in combat.
     */
    public boolean isInCombat() {
        return inCombat;
    }

    /**
     * Sets the combat state.
     *
     * REFLECTION:
     * This allows the game loop or controller to adjust behavior dynamically
     * (e.g., restrict movement, enable attack commands, etc.).
     */
    public void setInCombat(boolean inCombat) {
        this.inCombat = inCombat;
    }
}