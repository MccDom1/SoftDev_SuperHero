public class Puzzle {

    private String puzzleId;
    private String puzzleName;
    private String roomId;
    private String roomName;
    private String description;
    private String solution;
    private int maxAttempts;
    private int attemptsUsed;
    private boolean solved;
    private boolean active;
    private String reward;
    private String successMessage;
    private String failMessage;

    public Puzzle(String puzzleId, String puzzleName, String roomId, String roomName, String description,
                  String solution, int maxAttempts, String reward,
                  String successMessage, String failMessage) {
        this.puzzleId = puzzleId;
        this.puzzleName = puzzleName;
        this.roomId = roomId;
        this.roomName = roomName;
        this.description = description;
        this.solution = solution;
        this.maxAttempts = maxAttempts;
        this.attemptsUsed = 0;
        this.solved = false;
        this.active = true;
        this.reward = reward;
        this.successMessage = successMessage;
        this.failMessage = failMessage;
    }

    // ======================== // CONSTRUCTOR OVERLOAD FOR MAP DATA // ========================
    public Puzzle(String puzzleId, String puzzleName, String description, String solution,
                  int maxAttempts, boolean solved, String outcome) {
        this(puzzleId, puzzleName, "", "", description, solution, maxAttempts,
                outcome, outcome, "Puzzle failed. Try again.");
        this.solved = solved;
        this.active = !solved;
    }

    public String getPuzzleId() { return puzzleId; }
    public String getPuzzleName() { return puzzleName; }
    public String getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public String getDescription() { return description; }
    public String getReward() { return reward; }
    public String getSuccessMessage() { return successMessage; }
    public String getFailMessage() { return failMessage; }
    public int getAttemptsUsed() { return attemptsUsed; }
    public int getRemainingAttempts() { return maxAttempts - attemptsUsed; }
    public boolean isSolved() { return solved; }
    public boolean isActive() { return active; }

    public boolean hasAttemptsRemaining() {
        return attemptsUsed < maxAttempts;
    }

    public boolean checkAnswer(String answer) {
        if (!active || solved || !hasAttemptsRemaining()) {
            return false;
        }

        attemptsUsed++;

        String normalizedInput = answer.trim();
        String normalizedSolution = solution.trim();

        if (normalizedSolution.equalsIgnoreCase(normalizedInput)) {
            solved = true;
            active = false;
            return true;
        }

        return false;
    }

    public boolean isFailed() {
        return !solved && attemptsUsed >= maxAttempts;
    }

    public void resetPuzzle() {
        attemptsUsed = 0;
        solved = false;
        active = true;
    }

    public void deactivatePuzzle() {
        active = false;
    }
}
