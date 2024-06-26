import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private final TaskManager inMemoryTaskManager = Managers.getDefault();

    @Test
    public void checkIfTaskManagerWorking(){
        Task task = new Task("Lol", "Kek", TaskStatus.NEW);
        inMemoryTaskManager.createTask(task);
        assertNotNull(inMemoryTaskManager.getAllTasks(), "Таск менеджер не функционирует.");
    }
}