import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents a chatbot that manages a list of tasks based on user commands
 * entered via the command line, and persists them to disk between runs.
 */
public class Chim {

    /**
     * Runs the Chim chatbot: prints the greeting, loads any saved tasks
     * from disk, then repeatedly reads and processes user commands until
     * the user types "bye".
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        String logo =
                "  #####  #     # ###  #     # \n"
                        + " #     # #     #  #   ##   ## \n"
                        + " #       #     #  #   # # # # \n"
                        + " #       #######  #   #  #  # \n"
                        + " #       #     #  #   #     # \n"
                        + " #     # #     #  #   #     # \n"
                        + "  #####  #     # ###  #     # \n";

        String line = "____________________________________________________________";

        System.out.println(line);
        System.out.println(logo);
        System.out.println("Hello! I'm Chim.");
        System.out.println("What can I do for you?");
        System.out.println(line);

        Storage storage = new Storage("./data/chim.txt");
        ArrayList<Task> tasks = storage.load();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                if (input.equals("bye")) {
                    System.out.println(line);
                    System.out.println("Bye. Hope to see you again soon!");
                    System.out.println(line);
                    break;
                }

                if (input.equals("list")) {
                    System.out.println(line);
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + "." + tasks.get(i));
                    }
                    System.out.println(line);
                    continue;
                }

                if (input.startsWith("mark ")) {
                    int index = Integer.parseInt(input.substring(5).trim()) - 1;
                    tasks.get(index).markAsDone();
                    storage.save(tasks);
                    System.out.println(line);
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(index));
                    System.out.println(line);
                    continue;
                }

                if (input.startsWith("unmark")) {
                    int index = parseIndex(input, "unmark", tasks.size());
                    tasks.get(index).markAsNotDone();
                    storage.save(tasks);
                    System.out.println(line);
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(index));
                    System.out.println(line);
                    continue;
                }

                if (input.startsWith("delete")) {
                    int index = parseIndex(input, "delete", tasks.size());
                    Task removed = tasks.remove(index);
                    storage.save(tasks);
                    System.out.println(line);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + removed);
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    System.out.println(line);
                    continue;
                }

                if (input.startsWith("todo")) {
                    // Everything after the word todo is the description
                    String description = input.length() > 4 ? input.substring(4).trim() : "";

                    // Validate: description must not be empty
                    if (description.isEmpty()) {
                        throw new ChimException("OOPS!!! The description of a todo cannot be empty.");
                    }

                    tasks.add(new Todo(description));
                    storage.save(tasks);
                    printAddedMessage(line, tasks.get(tasks.size() - 1), tasks.size());
                    continue;
                }

                if (input.startsWith("deadline")) {
                    String rest = input.length() > 8 ? input.substring(8).trim() : "";
                    if (rest.isEmpty()) {
                        throw new ChimException("OOPS!!! The description of a deadline cannot be empty.");
                    }
                    if (!rest.contains("/by")) {
                        throw new ChimException("OOPS!!! A deadline needs a '/by' with the due date/time.");
                    }
                    // Split into description and "by" date/time, only on first occurrence
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
                    storage.save(tasks);
                    printAddedMessage(line, tasks.get(tasks.size() - 1), tasks.size());
                    continue;
                }

                if (input.startsWith("event")) {
                    String rest = input.length() > 5 ? input.substring(5).trim() : "";

                    if (rest.isEmpty()) {
                        throw new ChimException("OOPS!!! The description of an event cannot be empty.");
                    }
                    // Events require both "/from" and "/to" markers
                    if (!rest.contains("/from") || !rest.contains("/to")) {
                        throw new ChimException("OOPS!!! An event needs both '/from' and '/to' times.");
                    }

                    // First split off the description from the "/from ... /to ..." part
                    String[] fromSplit = rest.split("/from", 2);
                    String description = fromSplit[0].trim();
                    String remainder = fromSplit[1].trim();

                    // Then split the remainder into "from" and "to" times
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
                    storage.save(tasks);
                    printAddedMessage(line, tasks.get(tasks.size() - 1), tasks.size());
                    continue;
                }

                throw new ChimException("Chim does not understand what that means :-(");

            } catch (ChimException e) {
                // Catches all custom Chim-specific errors thrown above
                System.out.println(line);
                System.out.println(" " + e.getMessage());
                System.out.println(line);
            } catch (NumberFormatException e) {
                // Thrown by Integer.parseInt() if user types something non-numeric
                // after "mark"/"unmark", e.g. "mark abc"
                System.out.println(line);
                System.out.println(" OOPS!!! Please provide a valid task number.");
                System.out.println(line);
            } catch (ArrayIndexOutOfBoundsException e) {
                // Safety net in case an index slips past manual validation
                System.out.println(line);
                System.out.println(" OOPS!!! That task number doesn't exist in your list.");
                System.out.println(line);
            }
        }

        scanner.close();
    }

    private static int parseIndex(String input, String command, int taskCount) throws ChimException {
        String numberPart = input.length() > command.length() ? input.substring(command.length()).trim() : "";

        if (numberPart.isEmpty()) {
            throw new ChimException("OOPS!!! Please specify which task number to " + command + ".");
        }

        // May throw NumberFormatException if numberPart isn't a valid integer;
        // that's caught by the caller's try/catch
        int index = Integer.parseInt(numberPart) - 1;

        if (index < 0 || index >= taskCount) {
            throw new ChimException("OOPS!!! That task number doesn't exist in your list.");
        }

        return index;
    }

    private static void printAddedMessage(String line, Task task, int taskCount) {
        System.out.println(line);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(line);
    }
}