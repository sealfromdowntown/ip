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

    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_FIND = "find";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_PRIORITY = "priority";

    private static final String DEADLINE_SEPARATOR = "/by";
    private static final String EVENT_FROM_SEPARATOR = "/from";
    private static final String EVENT_TO_SEPARATOR = "/to";

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
        if (input.equals(COMMAND_BYE)) {
            return ui.getGoodbyeMessage();
        }

        if (input.equals(COMMAND_LIST)) {
            return ui.getTaskListMessage(tasks.getTasks());
        }

        if (input.startsWith(COMMAND_FIND)) {
            String keyword = input.length() > COMMAND_FIND.length()
                    ? input.substring(COMMAND_FIND.length()).trim()
                    : "";
            if (keyword.isEmpty()) {
                throw new ChimException("OOPS!!! Please provide a keyword to search for.");
            }
            return ui.getMatchingTasksMessage(tasks.find(keyword));
        }

        if (input.startsWith(COMMAND_MARK)) {
            int index = parseIndex(input, COMMAND_MARK, tasks.size());
            Task task = tasks.get(index);
            task.markAsDone();
            storage.save(tasks.getTasks());
            return ui.getTaskMarkedMessage(task);
        }

        if (input.startsWith(COMMAND_UNMARK)) {
            int index = parseIndex(input, COMMAND_UNMARK, tasks.size());
            Task task = tasks.get(index);
            task.markAsNotDone();
            storage.save(tasks.getTasks());
            return ui.getTaskUnmarkedMessage(task);
        }

        if (input.startsWith(COMMAND_DELETE)) {
            int index = parseIndex(input, COMMAND_DELETE, tasks.size());
            Task removed = tasks.delete(index);
            storage.save(tasks.getTasks());
            return ui.getTaskDeletedMessage(removed, tasks.size());
        }

        if (input.startsWith(COMMAND_PRIORITY)) {
            String rest = input.length() > COMMAND_PRIORITY.length()
                    ? input.substring(COMMAND_PRIORITY.length()).trim()
                    : "";

            String[] parts = rest.split(" ", 2);
            if (parts.length < 2) {
                throw new ChimException("OOPS!!! Please provide a task number and priority.");
            }

            int index = parseIndex(COMMAND_PRIORITY + " " + parts[0], COMMAND_PRIORITY, tasks.size());
            String priority = parts[1].trim().toLowerCase();

            if (!priority.equals("high") && !priority.equals("medium")
                    && !priority.equals("low") && !priority.equals("none")) {
                throw new ChimException("OOPS!!! Priority must be high, medium, low, or none.");
            }

            Task task = tasks.get(index);
            task.setPriority(priority);
            storage.save(tasks.getTasks());
            return ui.getTaskPriorityMessage(task);
        }

        if (input.startsWith(COMMAND_TODO)) {
            String description = input.length() > COMMAND_TODO.length()
                    ? input.substring(COMMAND_TODO.length()).trim()
                    : "";
            if (description.isEmpty()) {
                throw new ChimException("OOPS!!! The description of a todo cannot be empty.");
            }
            tasks.add(new Todo(description));
            storage.save(tasks.getTasks());
            return ui.getTaskAddedMessage(tasks.get(tasks.size() - 1), tasks.size());
        }

        if (input.startsWith(COMMAND_DEADLINE)) {
            String rest = input.length() > COMMAND_DEADLINE.length()
                    ? input.substring(COMMAND_DEADLINE.length()).trim()
                    : "";
            if (rest.isEmpty()) {
                throw new ChimException("OOPS!!! The description of a deadline cannot be empty.");
            }
            if (!rest.contains(DEADLINE_SEPARATOR)) {
                throw new ChimException("OOPS!!! A deadline needs a '/by' with the due date/time.");
            }

            String[] parts = rest.split(DEADLINE_SEPARATOR, 2);
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

        if (input.startsWith(COMMAND_EVENT)) {
            String rest = input.length() > COMMAND_EVENT.length()
                    ? input.substring(COMMAND_EVENT.length()).trim()
                    : "";

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

        assert index >= 0 && index < taskCount : "Parsed index should be within task list bounds";

        return index;
    }
}
