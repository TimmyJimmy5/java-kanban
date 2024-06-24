import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import service.InMemoryHistoryManager;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;
import service.HistoryManager;

class ManagersTest {

    Managers managers = new Managers();

    @Test
    void ifGetDefault() {
        TaskManager taskManager = managers.getDefault();
        assertInstanceOf(InMemoryTaskManager.class, taskManager);
    }

    @Test
    void ifGetDefaultHistory() {
        HistoryManager historyManager = managers.getDefaultHistory();
        assertInstanceOf(InMemoryHistoryManager.class, historyManager);
    }
}