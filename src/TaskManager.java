import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void changeTask(Task task);

    void changeEpic(Epic epic);

    void changeSubtask(Subtask subtask);

    List<Epic> getAllEpics();

    List<Task> getAllTasks();

    List<Subtask> getAllSubtasks();

    void clearTasks();

    void clearSubtasks();

    void clearEpics();

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubtaskById(int id);

    Task searchTaskById(int id);

    Subtask searchSubtaskById(int id);

    Epic searchEpicById(int id);

    void changeEpicStatusIfNeeded(int epicId);

    void changeTaskOrSubtaskStatus(int id, TaskStatus status);

    void getHistory();
}
