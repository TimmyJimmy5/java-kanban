import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private static final HistoryManager historyManager = new InMemoryHistoryManager();

    @BeforeAll
    public static void isHistoryEmptyAtStart() {
        assertNull(historyManager.getHistoryOfTasks(), "Список истории не пуст изначально!");
    }

    @Test
    public void isHistoryWritten() {
        Task task = new Task("Abc", "Def", TaskStatus.NEW);
        historyManager.writeHistory(task);
        assertNotNull(historyManager.getHistoryOfTasks(), "История пустая. Возвращено null");
        assertEquals(1, historyManager.getHistoryOfTasks().size(), "История пустая. Размер списка 0");
    }
}