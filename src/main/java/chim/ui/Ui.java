package chim.ui;

import chim.task.Task;

import java.util.List;

/**
 * Handles all interactions with the user: printing messages and
 * reading input from the command line.
 */
public class Ui {

    private static final String LINE = "____________________________________________________________";

    /**
     * Prints the welcome banner and greeting shown when chim.Chim starts up.
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
        System.out.println("Hello! I'm chim.Chim.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    /**
     * Prints the goodbye message shown when the user exits chim.Chim.
     */
    public void showGoodbye() {
        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    /**
     * Prints every task in the given list, numbered from 1.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        System.out.println(LINE);
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
        System.out.println(LINE);
    }

    /**
     * Prints confirmation that a task was added.
     *
     * @param task chim.task.Task that was added.
     * @param taskCount Total number of tasks after adding.
     */
    public void showTaskAdded(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Prints confirmation that a task was deleted.
     *
     * @param task chim.task.Task that was removed.
     * @param taskCount Total number of tasks after removal.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        System.out.println(LINE);
        System.out.println(" Noted. I've removed this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    /**
     * Prints confirmation that a task was marked as done.
     *
     * @param task chim.task.Task that was marked.
     */
    public void showTaskMarked(Task task) {
        System.out.println(LINE);
        System.out.println(" Nice! I've marked this task as done:");
        System.out.println("   " + task);
        System.out.println(LINE);
    }

    /**
     * Prints confirmation that a task was marked as not done.
     *
     * @param task chim.task.Task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println(LINE);
        System.out.println(" OK, I've marked this task as not done yet:");
        System.out.println("   " + task);
        System.out.println(LINE);
    }

    /**
     * Prints an error message in chim.Chim's standard message box.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        System.out.println(LINE);
        System.out.println(" " + message);
        System.out.println(LINE);
    }
}