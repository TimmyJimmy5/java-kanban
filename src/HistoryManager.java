import java.util.List;

public interface HistoryManager {
    void writeHistory(Task task);

    List<Task> getHistoryOfTasks();
}
