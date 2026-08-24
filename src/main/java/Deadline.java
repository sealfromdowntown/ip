/**
 * Represents a task that needs to be completed by a specific date/time.
 */
public class Deadline extends Task {

    protected String by;

    /**
     * Creates a Deadline with the given description and due date/time.
     *
     * @param description Description of the deadline task.
     * @param by Date/time the task is due by.
     */
    public Deadline(String description, String by) {
        super(description, TaskType.DEADLINE);
        this.by = by;
    }

    @Override
    public String toFileFormat() {
        return super.toFileFormat() + " | " + by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}