package chim.task;

import java.util.ArrayList;

/**
 * Represents the list of tasks and the operations that can be performed
 * on it, such as adding, deleting, and retrieving tasks.
 */
public class TaskList {

    private final ArrayList<Task> tasks;

    /**
     * Creates an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a TaskList containing the given tasks, e.g. tasks loaded
     * from disk on startup.
     *
     * @param tasks Initial tasks to populate the list with.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task Task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes and returns the task at the given index.
     *
     * @param index Zero-based index of the task to remove.
     * @return The removed task.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given index.
     *
     * @param index Zero-based index of the task.
     * @return The task at that index.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    public int size() {
        return tasks.size();
    }

    /**
     * Returns the underlying list of tasks, e.g. for saving to disk or
     * displaying to the user.
     *
     * @return The list of all tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }

    /**
     * Returns the tasks whose description contains the given keyword,
     * ignoring case.
     *
     * @param keyword Keyword to search for within task descriptions.
     * @return List of tasks whose description contains the keyword.
     */
    public ArrayList<Task> find(String keyword) {
        ArrayList<Task> matches = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(lowerKeyword)) {
                matches.add(task);
            }
        }
        return matches;
    }
}
