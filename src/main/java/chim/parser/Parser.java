package chim.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import chim.exception.ChimException;
import chim.storage.Storage;
import chim.task.Deadline;
import chim.task.Event;
import chim.task.Task;
import chim.task.TaskList;
import chim.task.Todo;
import chim.ui.Ui;

/**
 * Interprets raw user input and carries out the corresponding action
 * on the task list, printing results via the given Ui.
 */
public class Parser {

    /**
     * Parses a single line of user input and executes the command it
     * represents.
     *
     * @param input Raw line of input from the user.
     * @param tasks Task list to operate on.
     * @param ui Ui used to display results.
     * @param storage Storage used to persist changes to disk.
     * @return false if the command was "bye" (signals the run loop to
     *         stop), true otherwise.
     * @throws ChimException If the input is not a recognised or valid command.
     */
    public boolean parseAndExecute(String input, TaskList tasks, Ui ui, Storage storage) throws ChimException {
        if (input.equals("bye")) {
            ui.showGoodbye();
            return false;
        }

        if (input.equals("list")) {
            ui.showTaskList(tasks.getTasks());
            return true;
        }

        if (input.startsWith("find")) {
            String keyword = input.length() > 4 ? input.substring(4).trim() : "";
            if (keyword.isEmpty()) {
                throw new ChimException("OOPS!!! Please provide a keyword to search for.");
            }
            ui.showMatchingTasks(tasks.find(keyword));
            return true;
        }

        if (input.startsWith("mark ")) {
            int index = Integer.parseInt(input.substring(5).trim()) - 1;
            tasks.get(index).markAsDone();
            storage.save(tasks.getTasks());
            ui.showTaskMarked(tasks.get(index));
            return true;
        }

        if (input.startsWith("unmark")) {
            int index = parseIndex(input, "unmark", tasks.size());
            tasks.get(index).markAsNotDone();
            storage.save(tasks.getTasks());
            ui.showTaskUnmarked(tasks.get(index));
            return true;
        }

        if (input.startsWith("delete")) {
            int index = parseIndex(input, "delete", tasks.size());
            Task removed = tasks.delete(index);
            storage.save(tasks.getTasks());
            ui.showTaskDeleted(removed, tasks.size());
            return true;
        }

        if (input.startsWith("todo")) {
            String description = input.length() > 4 ? input.substring(4).trim() : "";
            if (description.isEmpty()) {
                throw new ChimException("OOPS!!! The description of a todo cannot be empty.");
            }
            tasks.add(new Todo(description));
            storage.save(tasks.getTasks());
            ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
            return true;
        }

        if (input.startsWith("deadline")) {
            String rest = input.length() > 8 ? input.substring(8).trim() : "";
            if (rest.isEmpty()) {
                throw new ChimException("OOPS!!! The description of a deadline cannot be empty.");
            }
            if (!rest.contains("/by")) {
                throw new ChimException("OOPS!!! A deadline needs a '/by' with the due date/time.");
            }

            String[] parts = rest.split("/by", 2);
            String description = parts[0].trim();
            String by = parts[1].trim();

            if (description.isEmpty()) {
                throw new ChimException("OOPS!!! The description of a deadline cannot be empty.");
            }
            if (by.isEmpty()) {
                throw new ChimException("OOPS!!! Please tell me when the deadline is due.");
            }

            LocalDate byDate;
            try {
                byDate = LocalDate.parse(by);
            } catch (DateTimeParseException e) {
                throw new ChimException("OOPS!!! Please give the date in yyyy-mm-dd format, e.g. 2019-10-15.");
            }

            tasks.add(new Deadline(description, byDate));
            storage.save(tasks.getTasks());
            ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
            return true;
        }

        if (input.startsWith("event")) {
            String rest = input.length() > 5 ? input.substring(5).trim() : "";

            if (rest.isEmpty()) {
                throw new ChimException("OOPS!!! The description of an event cannot be empty.");
            }
            if (!rest.contains("/from") || !rest.contains("/to")) {
                throw new ChimException("OOPS!!! An event needs both '/from' and '/to' times.");
            }

            String[] fromSplit = rest.split("/from", 2);
            String description = fromSplit[0].trim();
            String remainder = fromSplit[1].trim();

            String[] toSplit = remainder.split("/to", 2);
            String from = toSplit[0].trim();
            String to = toSplit.length > 1 ? toSplit[1].trim() : "";

            if (description.isEmpty()) {
                throw new ChimException("OOPS!!! The description of an event cannot be empty.");
            }
            if (from.isEmpty() || to.isEmpty()) {
                throw new ChimException("OOPS!!! Please provide both a start and end time for the event.");
            }

            tasks.add(new Event(description, from, to));
            storage.save(tasks.getTasks());
            ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
            return true;
        }

        throw new ChimException("Chim does not understand what that means :-(");
    }

    /**
     * Extracts and validates the task index following a command word,
     * e.g. the "2" in "delete 2".
     *
     * @param input Full raw user input.
     * @param command Command word the index follows (e.g. "delete").
     * @param taskCount Current number of tasks, used to validate the index is in range.
     * @return Zero-based index of the referenced task.
     * @throws ChimException If no index was given or the index is out of range.
     */
    private int parseIndex(String input, String command, int taskCount) throws ChimException {
        String numberPart = input.length() > command.length() ? input.substring(command.length()).trim() : "";

        if (numberPart.isEmpty()) {
            throw new ChimException("OOPS!!! Please specify which task number to " + command + ".");
        }

        int index = Integer.parseInt(numberPart) - 1;

        if (index < 0 || index >= taskCount) {
            throw new ChimException("OOPS!!! That task number doesn't exist in your list.");
        }

        return index;
    }
}
