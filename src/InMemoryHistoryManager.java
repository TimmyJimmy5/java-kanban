import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history = new ArrayList<>();

    @Override
    public void writeHistory(Task task){
        if (InMemoryTaskManager.getTasks().containsKey(task.getId())){
            if (history.size() >= 10){
                history.removeFirst();
                history.add(task);
            } else {
                history.add(task);
            }
        } else if (InMemoryTaskManager.getSubtasks().containsKey(task.getId())) {
            if (history.size() >= 10) {
                history.removeFirst();
                history.add(task);
            } else {
                history.add(task);
            }
        } else if (InMemoryTaskManager.getEpics().containsKey(task.getId())) {
            if (history.size() >= 10) {
                history.removeFirst();
                history.add(task);
            } else {
                history.add(task);
            }
        }
    }

    @Override
    public void getHistory(){
        if (history.isEmpty()) {
            System.out.println("История просмотров отсутствует.");
        } else {
            for (Task task : history) {
                System.out.println(task);
            }
        }
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "history=" + history +
                '}';
    }
}
