package chim.ui;

import java.util.List;

import chim.task.Task;

/**
 * Formats messages for the chatbot's responses and handles printing
 * for the command-line interface.
 */
public class Ui {

    private static final String LINE = "____________________________________________________________";

    /**
     * Prints the welcome banner and greeting shown when Chim starts up (CLI only).
     */
    public void showWelcome() {
        String logo =
                "  #####  #     # ###  #     # \n"
                        + " #     # #     #  #   ##   ## \n"
                        + " #       #     #  #   # # # # \n"
                        + " #       #######  #   #  #  # \n"
                        + " #       #     #  #   #     # \n"
                        + " #     # #     #  #   #     # \n"
                        + "  #####  #     # ###  #     # \n";
        System.out.println(LINE);
        System.out.println(logo);
        System.out.println("Hello! I'm Chim.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    /**
     * Prints a message wrapped in Chim's standard divider lines (CLI only).
     *
     * @param message Message to print.
     */
    public void printMessage(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }

    /**
     * Returns the goodbye message shown when the user exits Chim.
     *
     * @return Goodbye message.
     */
    public String getGoodbyeMessage() {
        return "Bye. Hope to see you again soon!";
    }

    /**
     * Returns every task in the given list, numbered from 1.
     *
     * @param tasks Tasks to display.
     * @return Formatted task list message.
     */
    public String getTaskListMessage(List<Task> tasks) {
        return formatTaskList(" Here are the tasks in your list:", tasks);
    }

    /**
     * Returns every task that matches a search keyword, numbered from 1.
     *
     * @param matchingTasks Tasks that matched the search.
     * @return Formatted matching-tasks message.
     */
    public String getMatchingTasksMessage(List<Task> matchingTasks) {
        return formatTaskList(" Here are the matching tasks in your list:", matchingTasks);
    }

    /**
     * Returns confirmation that a task was added.
     *
     * @param task Task that was added.
     * @param taskCount Total number of tasks after adding.
     * @return Formatted task-added message.
     */
    public String getTaskAddedMessage(Task task, int taskCount) {
        return joinLines(
                " Got it. I've added this task:",
                "   " + task,
                " Now you have " + taskCount + " tasks in the list."
        );
    }

    /**
     * Returns confirmation that a task was deleted.
     *
     * @param task Task that was removed.
     * @param taskCount Total number of tasks after removal.
     * @return Formatted task-deleted message.
     */
    public String getTaskDeletedMessage(Task task, int taskCount) {
        return joinLines(
                " Noted. I've removed this task:",
                "   " + task,
                " Now you have " + taskCount + " tasks in the list."
        );
    }

    /**
     * Returns confirmation that a task was marked as done.
     *
     * @param task Task that was marked.
     * @return Formatted task-marked message.
     */
    public String getTaskMarkedMessage(Task task) {
        return joinLines(
                " Nice! I've marked this task as done:",
                "   " + task
        );
    }

    /**
     * Returns confirmation that a task was marked as not done.
     *
     * @param task Task that was unmarked.
     * @return Formatted task-unmarked message.
     */
    public String getTaskUnmarkedMessage(Task task) {
        return joinLines(
                " OK, I've marked this task as not done yet:",
                "   " + task
        );
    }

    private String joinLines(String... lines) {
        return String.join("\n", lines);
    }

    private String formatTaskList(String header, List<Task> tasks) {
        StringBuilder sb = new StringBuilder(header);
        for (int i = 0; i < tasks.size(); i++) {
            sb.append("\n ").append(i + 1).append(".").append(tasks.get(i));
        }
        return sb.toString();
    }
}
