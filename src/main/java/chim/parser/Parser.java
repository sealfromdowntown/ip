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
 * on the task list, returning the response message to show the user.
 */
public class Parser {

    /**
     * Parses a single line of user input, executes it, and returns the
     * message to display to the user.
     *
     * @param input Raw line of input from the user.
     * @param tasks TaskList to operate on.
     * @param ui Ui used to format response messages.
     * @param storage Storage used to persist changes to disk.
     * @return The response message for this command.
     * @throws ChimException If the input is not a recognised or valid command.
     */
    public String parseAndExecute(String input, TaskList tasks, Ui ui, Storage storage) throws ChimException {
        if (input.equals("bye")) {
            return ui.getGoodbyeMessage();
        }

        if (input.equals("list")) {
            return ui.getTaskListMessage(tasks.getTasks());
        }

        if (input.startsWith("find")) {
            String keyword = input.length() > 4 ? input.substring(4).trim() : "";
            if (keyword.isEmpty()) {
                throw new ChimException("OOPS!!! Please provide a keyword to search for.");
            }
            return ui.getMatchingTasksMessage(tasks.find(keyword));
        }

        if (input.startsWith("mark ")) {
            int index = Integer.parseInt(input.substring(5).trim()) - 1;
            tasks.get(index).markAsDone();
            storage.save(tasks.getTasks());
            return ui.getTaskMarkedMessage(tasks.get(index));
        }

        if (input.startsWith("unmark")) {
            int index = parseIndex(input, "unmark", tasks.size());
            tasks.get(index).markAsNotDone();
            storage.save(tasks.getTasks());
            return ui.getTaskUnmarkedMessage(tasks.get(index));
        }

        if (input.startsWith("delete")) {
            int index = parseIndex(input, "delete", tasks.size());
            Task removed = tasks.delete(index);
            storage.save(tasks.getTasks());
            return ui.getTaskDeletedMessage(removed, tasks.size());
        }

        if (input.startsWith("todo")) {
            String description = input.length() > 4 ? input.substring(4).trim() : "";
            if (description.isEmpty()) {
                throw new ChimException("OOPS!!! The description of a todo cannot be empty.");
            }
            tasks.add(new Todo(description));
            storage.save(tasks.getTasks());
            return ui.getTaskAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
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
            return ui.getTaskAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
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
            return ui.getTaskAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
        }

        throw new ChimException("Chim does not understand what that means :-(");
    }

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
