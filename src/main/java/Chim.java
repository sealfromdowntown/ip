import java.util.Scanner;

public class Chim {
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

        Task[] tasks = new Task[100];
        int taskCount = 0;

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine().trim();

            if (input.equals("bye")) {
                System.out.println(line);
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println(line);
                break;
            }

            if (input.equals("list")) {
                System.out.println(line);
                System.out.println(" Here are the tasks in your list:");
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + "." + tasks[i]);
                }
                System.out.println(line);
                continue;
            }

            if (input.startsWith("mark ")) {
                int index = Integer.parseInt(input.substring(5).trim()) - 1;
                tasks[index].markAsDone();
                System.out.println(line);
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + tasks[index]);
                System.out.println(line);
                continue;
            }

            if (input.startsWith("unmark ")) {
                int index = Integer.parseInt(input.substring(7).trim()) - 1;
                tasks[index].markAsNotDone();
                System.out.println(line);
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + tasks[index]);
                System.out.println(line);
                continue;
            }

            if (input.startsWith("todo ")) {
                String description = input.substring(5).trim();
                tasks[taskCount] = new Todo(description);
                taskCount++;
                printAddedMessage(line, tasks[taskCount - 1], taskCount);
                continue;
            }

            if (input.startsWith("deadline ")) {
                String rest = input.substring(9).trim();
                String[] parts = rest.split("/by", 2);
                String description = parts[0].trim();
                String by = parts.length > 1 ? parts[1].trim() : "";
                tasks[taskCount] = new Deadline(description, by);
                taskCount++;
                printAddedMessage(line, tasks[taskCount - 1], taskCount);
                continue;
            }

            if (input.startsWith("event ")) {
                String rest = input.substring(6).trim();
                String[] fromSplit = rest.split("/from", 2);
                String description = fromSplit[0].trim();
                String remainder = fromSplit.length > 1 ? fromSplit[1].trim() : "";
                String[] toSplit = remainder.split("/to", 2);
                String from = toSplit[0].trim();
                String to = toSplit.length > 1 ? toSplit[1].trim() : "";
                tasks[taskCount] = new Event(description, from, to);
                taskCount++;
                printAddedMessage(line, tasks[taskCount - 1], taskCount);
                continue;
            }


            tasks[taskCount] = new Todo(input);
            taskCount++;
            printAddedMessage(line, tasks[taskCount - 1], taskCount);
        }

        scanner.close();
    }

    private static void printAddedMessage(String line, Task task, int taskCount) {
        System.out.println(line);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + taskCount + " tasks in the list.");
        System.out.println(line);
    }
}