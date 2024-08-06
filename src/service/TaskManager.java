package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import java.util.Set;

import java.util.List;

public interface TaskManager {

    int createTask(Task task);

    int createEpic(Epic epic);

    Integer createSubtask(Subtask subtask);

    void changeTask(int id, Task task);

    void changeEpic(int id, Epic epic);

    void changeSubtask(int id, Subtask subtask);

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

    List<Task> getHistory();

    Set<Task> getPrioritizedTasks();

    void addToPrioritizedTasks(Task task);

    int getCounter();
}
