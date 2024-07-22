import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import service.FileBackedTaskManager;

import java.io.File;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class FileBackedTaskManagerTest {
    File file = new File("./src/service/TaskSaveFileTest.csv");
    FileBackedTaskManager manager = new FileBackedTaskManager(Paths.get(""), file.toPath());

    @Test
    public void ifCreateFileWorks() {
        manager = new FileBackedTaskManager(Paths.get(""), Paths.get(""));
        File file = new File("./src/service/TaskSaveFileTest.csv");
        file.delete();
        Assertions.assertFalse(file.exists());
        manager = new FileBackedTaskManager(Paths.get(""), file.toPath());
        Assertions.assertTrue(file.exists());
    }

    @Test
    public void ifLoadFromFileWorks() throws IOException {
        manager = new FileBackedTaskManager(Paths.get(""), file.toPath());
        Task task0 = new Task("lol", "kek", TaskStatus.NEW);
        Epic task1 = new Epic("epicLol", "epicKek", TaskStatus.NEW);
        Subtask task2 = new Subtask("subLol", "subKek", TaskStatus.NEW, 1);
        Subtask task3 = new Subtask("subLol1", "subKek1", TaskStatus.NEW, 1);
        manager.createTask(task0);
        manager.createEpic(task1);
        manager.createSubtask(task2);
        manager.createSubtask(task3);

        FileBackedTaskManager managerLoad = new FileBackedTaskManager(Paths.get(""), file.toPath());
        managerLoad = managerLoad.loadFromFile(file);
        Assertions.assertEquals(task0, managerLoad.searchTaskById(0));
        Assertions.assertEquals(task1, managerLoad.searchEpicById(1));
        Assertions.assertEquals(task2, managerLoad.searchSubtaskById(2));
        Assertions.assertEquals(task3, managerLoad.searchSubtaskById(3));
    }

    @Test
    public void ifHistoryWorks() throws IOException {
        manager = new FileBackedTaskManager(Paths.get(""), file.toPath());
        Task task0 = new Task("lol", "kek", TaskStatus.NEW);
        Epic task1 = new Epic("epicLol", "epicKek", TaskStatus.NEW);
        Subtask task2 = new Subtask("subLol", "subKek", TaskStatus.NEW, 1);
        Subtask task3 = new Subtask("subLol1", "subKek1", TaskStatus.NEW, 1);
        manager.createTask(task0);
        manager.createEpic(task1);
        manager.createSubtask(task2);
        manager.createSubtask(task3);
        manager.searchTaskById(0);
        manager.searchEpicById(1);

        FileBackedTaskManager managerLoad = new FileBackedTaskManager(file);
        managerLoad = managerLoad.loadFromFile(file);
        System.out.println("\n" + managerLoad.getAllTasks() + "\n" + managerLoad.getAllEpics() + "\n" + managerLoad.getAllSubtasks());
        Assertions.assertEquals(manager.getHistory(), managerLoad.getHistory());
    }
}
