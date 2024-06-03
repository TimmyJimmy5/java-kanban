import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyOfTasks = new ArrayList<>();

    @Override
    public void writeHistory(Task task){
            if (historyOfTasks.size() >= 10){
                historyOfTasks.removeFirst();
                historyOfTasks.add(task);
            } else {
                historyOfTasks.add(task);
            }
        }

    @Override
    public  List<Task> getHistoryOfTasks() {
        if (historyOfTasks.isEmpty()) {
            return null;
        } else {
            return historyOfTasks;
        }
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "historyOfTasks=" + historyOfTasks +
                '}';
    }
}
