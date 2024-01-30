import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TaskList {
    private final ArrayList<Task> tasks;

    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        this.tasks.add(task);
        Format.printSepLine();
        System.out.println("Got it. I've added this task:");
        System.out.println(task);
        System.out.println("Now you have " + this.tasks.size()  + " tasks in the list.");
        Format.printSepLine();
    }

    public void markTask(int index) throws CoDriverException {
        int listIndex = index - 1;
        if (listIndex >= this.tasks.size()) {
            throw new CoDriverException("Error! Given index exceeds list size of " + this.tasks.size() + ".");
        }
        Task t = this.tasks.get(listIndex);
        t.markDone();
        Format.printSepLine();
        System.out.println("Nice! I've marked this task as done:");
        System.out.println(t);
        Format.printSepLine();
    }

    public void unmarkTask(int index) throws CoDriverException{
        int listIndex = index - 1;
        if (listIndex >= this.tasks.size()) {
            throw new CoDriverException("Error! Given index exceeds list size of " + this.tasks.size() + ".");
        }
        Task t = this.tasks.get(listIndex);
        t.markNotDone();
        Format.printSepLine();
        System.out.println("Ok, I've marked this task as not done yet:");
        System.out.println(t);
        Format.printSepLine();
    }

    public void listTasks() {
        Format.printSepLine();
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < this.tasks.size(); i++) {
            int listIndex = i + 1;
            System.out.println(listIndex + ". " + tasks.get(i));
        }
        Format.printSepLine();
    }

    public void deleteTask(int index) throws CoDriverException {
        int listIndex = index - 1;
        if (listIndex >= this.tasks.size()) {
            throw new CoDriverException("Error! Given index exceeds list size of " + this.tasks.size() + ".");
        }
        Task t = this.tasks.get(listIndex);
        Format.printSepLine();
        System.out.println("Noted. I've removed this task:");
        System.out.println(t);
        this.tasks.remove(t);
        System.out.println("Now you have " + this.tasks.size() + " tasks in the list.");
        Format.printSepLine();
    }

    public void saveTaskList(String filePath) {
        try {
            // if the data directory does not exist, create it
            Files.createDirectory(Paths.get("./data"));
        } catch (IOException e) {
            // do nothing
        }
        try {
            // if the file exists, delete the old file
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
            // create the file
            Files.createFile(path);
        } catch (IOException e) {
            System.out.println("Error! Unable to delete file!");
        }
        try {
            FileWriter fw = new FileWriter(filePath, true);
            for (Task t : this.tasks) {
                if (t instanceof ToDo) {
                    fw.write(t.toSaveString() + "\n");
                } else if (t instanceof Deadline) {
                    fw.write(t.toSaveString() + "\n");
                } else if (t instanceof Event) {
                    fw.write(t.toSaveString() + "\n");
                }
            }
            fw.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static TaskList openTaskList(String filePath) {
        TaskList tl = new TaskList();
        File f = new File(filePath);
        Scanner scanner;
        try {
            scanner = new Scanner(f);
        } catch (FileNotFoundException e) {
            return tl;
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] arguments = line.split(" ");
            String type = arguments[0];
            boolean isDone = arguments[1].equals("1");
            switch (type) {
            case "T": // ToDo
                String description = line.substring(6);
                ToDo t = new ToDo(description);
                if (isDone) {
                    t.markDone();
                }
                tl.addTask(t);
                break;
            case "D": // Deadline
                String deadlineDescription = line.substring(6, line.indexOf("|") - 1);
                String deadlineDate = line.substring(line.indexOf("|") + 2);
                Deadline d = new Deadline(deadlineDescription, deadlineDate);
                if (isDone) {
                    d.markDone();
                }
                tl.addTask(d);
                break;
            case "E": // Event
                String eventDescription = line.substring(6, line.indexOf("|") - 1);
                String eventFrom = line.substring(line.indexOf("|") + 2, line.indexOf("-") - 1);
                String eventTo = line.substring(line.indexOf("-") + 2);
                Event e = new Event(eventDescription, eventFrom, eventTo);
                if (isDone) {
                    e.markDone();
                }
                tl.addTask(e);
                break;
            }
        }
        return tl;
    }
}
