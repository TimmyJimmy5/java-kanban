import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.Managers;
import service.TaskManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    Managers managers = new Managers();
    TaskManager inMemoryTaskManager = managers.getDefault();
    HistoryManager historyManager = new InMemoryHistoryManager();

    @BeforeEach
    void start(){
        Managers managers = new Managers();
        TaskManager inMemoryTaskManager = managers.getDefault();
        HistoryManager historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void isHistoryEmptyAtStart() {
        assertEquals(Collections.emptyList(),historyManager.getHistoryOfTasks(), "Список истории не пуст изначально!");
    }

    @Test
    public void isHistoryObjectsAreCorrect() {

        Task task = new Task("Abc", "Def", TaskStatus.NEW,  10, "12:00 24.07.2024");
        Epic taskE = new Epic("Abc1", "Def1", TaskStatus.NEW);
        Subtask taskS = new Subtask("Abc2", "Def2", TaskStatus.NEW, 1,  10, "12:00 25.07.2024");

        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createEpic(taskE);
        inMemoryTaskManager.createSubtask(taskS);
        inMemoryTaskManager.searchTaskById(0);
        inMemoryTaskManager.searchEpicById(1);
        inMemoryTaskManager.searchSubtaskById(2);

        historyManager.addTask(task);
        historyManager.addTask(taskE);
        historyManager.addTask(taskS);

        List<Task> compareToSeparateHistory = new ArrayList<>();
        compareToSeparateHistory.add(task);
        compareToSeparateHistory.add(taskE);
        compareToSeparateHistory.add(taskS);

        List<Task> compareToHistoryManagerList = historyManager.getHistoryOfTasks();
        assertEquals(compareToHistoryManagerList, compareToSeparateHistory, "Списки не равны(отдельный и в менеджере истории).");

        List<Task> compareToTaskManagerHistoryList = inMemoryTaskManager.getHistory();
        assertEquals(compareToTaskManagerHistoryList, compareToSeparateHistory, "Списки не равны(Отдельный и в менеджере истории.");
        assertEquals(compareToTaskManagerHistoryList, compareToHistoryManagerList, "Списки не равны(В менеджере истории и менеджере задач).");
    }

    @Test
    public void isHistoryWrittenAndDeleted() {

        Task task = new Task("Abc", "Def", TaskStatus.NEW,  10, "12:00 24.07.2024");
        Epic taskE = new Epic("Abc1", "Def1", TaskStatus.NEW);
        Subtask taskS = new Subtask("Abc2", "Def2", TaskStatus.NEW, 1,  10, "12:00 25.07.2024");

        inMemoryTaskManager.createTask(task);
        inMemoryTaskManager.createEpic(taskE);
        inMemoryTaskManager.createSubtask(taskS);

        inMemoryTaskManager.searchTaskById(0);
        inMemoryTaskManager.searchEpicById(1);
        inMemoryTaskManager.searchSubtaskById(2);
        assertNotNull(inMemoryTaskManager.getHistory(), "История пустая. Возвращено null");
        assertEquals(3, inMemoryTaskManager.getHistory().size(), "История пустая. Размер списка 0");

        inMemoryTaskManager.removeTaskById(0);
        assertEquals(2, inMemoryTaskManager.getHistory().size(), "История не сократилась на 1.");
        inMemoryTaskManager.removeEpicById(1);
        assertNull(inMemoryTaskManager.getHistory(), "Эпик не удалился вместе с подзадачами из истории.");
    }
}