public class Task {
    protected String description;
    protected boolean isDone;
    protected TaskType type;

    public Task(String description, TaskType type) {
        this.description = description;
        this.isDone = false;
        this.type = type;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getType() {
        return type;
    }

    /**
     * Returns this task formatted as a single line for saving to disk.
     * Subclasses append any extra fields they need (e.g. by/from/to).
     *
     * @return Pipe-delimited representation of this task.
     */
    public String toFileFormat() {
        return getTypeSymbol() + " | " + getStatusDigit() + " | " + description;
    }

    protected String getStatusDigit() {
        return isDone ? "1" : "0";
    }

    protected String getTypeSymbol() {
        switch (type) {
            case TODO:
                return "T";
            case DEADLINE:
                return "D";
            case EVENT:
                return "E";
            default:
                return "?";
        }
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}