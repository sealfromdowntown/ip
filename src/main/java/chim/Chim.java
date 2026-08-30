package chim;

import java.util.Scanner;

import chim.exception.ChimException;
import chim.parser.Parser;
import chim.storage.Storage;
import chim.task.TaskList;
import chim.ui.Ui;

/**
 * Represents the Chim chatbot: wires together Ui, Storage, TaskList, and
 * Parser, and runs the main read-process-respond loop.
 */
public class Chim {

    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final Parser parser;

    /**
     * Creates a Chim chatbot that loads and saves its tasks at the given
     * file path.
     *
     * @param filePath Relative path to the data file, e.g. "./data/chim.txt".
     */
    public Chim(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        parser = new Parser();
        tasks = new TaskList(storage.load());
    }

    /**
     * Prints the greeting, then repeatedly reads and processes user
     * commands until the user types "bye".
     */
    public void run() {
        ui.showWelcome();
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            String input = scanner.nextLine().trim();
            try {
                isRunning = parser.parseAndExecute(input, tasks, ui, storage);
            } catch (ChimException e) {
                ui.showError(e.getMessage());
            } catch (NumberFormatException e) {
                ui.showError("OOPS!!! Please provide a valid task number.");
            } catch (ArrayIndexOutOfBoundsException e) {
                ui.showError("OOPS!!! That task number doesn't exist in your list.");
            }
        }

        scanner.close();
    }

    /**
     * Starts the Chim chatbot.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Chim("./data/chim.txt").run();
    }
}
