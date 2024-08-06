package service;

import java.io.File;
import java.nio.file.Paths;

public class Managers {
    public TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public FileBackedTaskManager getDefaultFileBackedTaskManager() {
        File file = new File("./src/service/TaskSaveFile1.csv");
        return new FileBackedTaskManager(Paths.get(""), file.toPath());
    }

    public FileBackedTaskManager getDefaultFileBackedTaskManagerFromFile(File file) {
        return new FileBackedTaskManager(file);
    }
}
