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

        Ui ui = new Ui();
        ui.showWelcome();

        Storage storage = new Storage("./data/chim.txt");
        TaskList tasks = new TaskList(storage.load());
        Parser parser = new Parser();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                boolean isRunning = parser.parseAndExecute(input, tasks, ui, storage);
                if (!isRunning) {
                    break;
                }
            } catch (ChimException e) {
                // Catches all custom Chim-specific errors thrown above
                ui.showError(e.getMessage());
            } catch (NumberFormatException e) {
                // Thrown by Integer.parseInt() if user types something non-numeric
                // after "mark"/"unmark", e.g. "mark abc"
                ui.showError("OOPS!!! Please provide a valid task number.");
            } catch (ArrayIndexOutOfBoundsException e) {
                // Safety net in case an index slips past manual validation
                ui.showError("OOPS!!! That task number doesn't exist in your list.");
            }
        }
        scanner.close();
    }
}