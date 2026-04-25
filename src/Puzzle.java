public class Puzzle {

    private String puzzleID;
    private String puzzleName;
    private String puzzleDesc;
    private String puzzleSolution;
    private int numAttempts;
    private int remainingAttempts;
    private int wrongAttempts;

    private boolean solved;
    private boolean failed;

    private String outcome;

    public Puzzle(String puzzleID, String puzzleName, String puzzleDesc,
                  String puzzleSolution, int numAttempts,
                  boolean isSolved, String outcome) {
        this.puzzleID = puzzleID;
        this.puzzleName = puzzleName;
        this.puzzleDesc = puzzleDesc;
        this.puzzleSolution = puzzleSolution;
        this.numAttempts = numAttempts;
        this.remainingAttempts = numAttempts;
        this.wrongAttempts = 0;
        this.solved = isSolved;
        this.failed = false;
        this.outcome = outcome;
    }

    public String getPuzzleID() { return puzzleID; }
    public String getPuzzleName() { return puzzleName; }
    public String getDescription() { return puzzleDesc; }
    public String getSolution() { return puzzleSolution; }

    public int getRemainingAttempts() { return remainingAttempts; }
    public int getWrongAttempts() { return wrongAttempts; }
    public int getWrongAttempt() { return wrongAttempts; }

    public boolean isSolved() { return solved; }
    public boolean isFailed() { return failed; }

    public String getOutcome() { return outcome; }

    public boolean attemptAnswer(String answer) {
        if (solved || failed) {
            return false;
        }

        if (answer.equalsIgnoreCase(puzzleSolution)) {
            solved = true;
            return true;
        }

        remainingAttempts--;
        wrongAttempts++;

        if (remainingAttempts <= 0) {
            failed = true;
        }

        return false;
    }

    public void resetPuzzle() {
        this.remainingAttempts = this.numAttempts;
        this.wrongAttempts = 0;
        this.failed = false;
        this.solved = false;
    }
}