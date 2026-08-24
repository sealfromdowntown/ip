import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles loading tasks from, and saving tasks to, the data file on disk.
 */
public class Storage {

    private final Path filePath;

    public Storage(String filePath) {
        this.filePath = Paths.get(filePath);
    }

    /**
     * Loads tasks from the data file. If the file or its parent folder does
     * not exist yet, an empty task list is returned instead of failing.
     *
     * @return List of tasks read from the data file.
     */
    public ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();

        if (!Files.exists(filePath)) {
            return tasks;
        }

        try {
            List<String> lines = Files.readAllLines(filePath);
            for (String rawLine : lines) {
                Task task = parseLine(rawLine);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println(" OOPS!!! Could not read the data file. Starting with an empty list.");
        }

        return tasks;
    }

    /**
     * Saves the given task list to the data file, creating the parent
     * folder first if it does not already exist.
     *
     * @param tasks Tasks to save.
     */
    public void save(ArrayList<Task> tasks) {
        try {
            Path parentDir = filePath.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }

            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                for (Task task : tasks) {
                    writer.write(task.toFileFormat() + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            System.out.println(" OOPS!!! Could not save tasks to the data file.");
        }
    }

    /**
     * Parses a single line from the data file into a Task.
     * Corrupted or unrecognised lines are skipped by returning null.
     *
     * @param rawLine Line read from the data file.
     * @return The parsed task, or null if the line could not be parsed.
     */
    private Task parseLine(String rawLine) {
        try {
            String[] parts = rawLine.split("\\|");
            for (int i = 0; i < parts.length; i++) {
                parts[i] = parts[i].trim();
            }

            String typeSymbol = parts[0];
            boolean isDone = parts[1].equals("1");
            String description = parts[2];

            Task task;
            switch (typeSymbol) {
                case "T":
                    task = new Todo(description);
                    break;
                case "D":
                    task = new Deadline(description, parts[3]);
                    break;
                case "E":
                    task = new Event(description, parts[3], parts[4]);
                    break;
                default:
                    return null;
            }

            if (isDone) {
                task.markAsDone();
            }
            return task;
        } catch (Exception e) {
            // Line does not match the expected format; skip it
            return null;
        }
    }
}