import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    Managers managers = new Managers();
    private final TaskManager inMemoryTaskManager = managers.getDefault();

    @Test
    public void checkIfTaskManagerWorking(){
        Task task = new Task("Lol", "Kek", TaskStatus.NEW,  10, "12:00 24.07.2024");
        inMemoryTaskManager.createTask(task);
        assertNotNull(inMemoryTaskManager.getAllTasks(), "Таск менеджер не функционирует.");
    }
}