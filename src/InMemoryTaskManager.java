import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private static int counter = 0;

    private static final HashMap<Integer, Task> tasks = new HashMap<>();
    private static final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private static final HashMap<Integer, Epic> epics = new HashMap<>();
    HistoryManager historyManager = Managers.getDefaultHistory();

    private int globalCounterIncrement() {
        return InMemoryTaskManager.counter++;
    }

    @Override
    public void createTask(Task task) {
        int taskId = globalCounterIncrement();
        task.setId(taskId);
        tasks.put(taskId, task);
    }

    @Override
    public void createEpic(Epic epic) {
        int epicId = globalCounterIncrement();
        epic.setId(epicId);
        epics.put(epicId, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        int subtaskId = globalCounterIncrement();
        subtask.setId(subtaskId);
        Epic currentEpic = epics.get(subtask.getEpicId());
        if (!epics.isEmpty() && currentEpic != null) {
            subtasks.put(subtaskId, subtask);
            currentEpic.setSubtaskIds(subtaskId);
            changeEpicStatusIfNeeded(subtask.getEpicId());
        }
    }

    @Override
    public void changeTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.replace(task.getId(), task);
        } else {
            System.out.println("Такой задачи нет");
        }
    }

    @Override
    public void changeEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.replace(epic.getId(), epic);
            changeEpicStatusIfNeeded(epic.getId());
        } else {
            System.out.println("Такая составная задача отсутствует.");
        }
    }

    @Override
    public void changeSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.replace(subtask.getId(), subtask);
            changeEpicStatusIfNeeded(subtask.getEpicId());
        } else {
            System.out.println("Подзадача не найдена.");
        }
    }

    @Override
    public List<Epic> getAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Крупных задач нет.");
            return null;
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Task> getAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Задач в списке нет.");
            return null;
        }
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        if (subtasks.isEmpty()) {
            System.out.println("Задач в списке нет.");
            return null;
        }
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Integer epicId : epics.keySet()) {
            Epic currentEpic = epics.get(epicId);
            currentEpic.removeSubtasksIds();
        }
        subtasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }


    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            System.out.println("Задача удалена.");
        } else {
            System.out.println("Задача не найдена");
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic currentEpic = epics.get(id);
        if (epics.containsKey(id)) {
            if (currentEpic != null) {
                for (Integer subtask : currentEpic.getSubtaskIds()) {
                    subtasks.remove(subtask);
                }
            }
            epics.remove(id);
            System.out.println("Составная задача и её подзадачи удалены.");
        } else {
            System.out.println("Составная задача не найдена");
        }
    }

    @Override
    public void removeSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtasks.containsKey(id)) {
            if (subtask != null) {
                Epic currentEpic = epics.get(subtask.getEpicId());
                ArrayList<Integer> subtasksId = currentEpic.getSubtaskIds();
                subtasksId.remove((Integer) subtask.getId());
                subtasks.remove(id);
                changeEpicStatusIfNeeded(currentEpic.getId());
            }
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    @Override
    public Task searchTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.writeHistory(tasks.get(id));
            return tasks.get(id);
        } else {
            System.out.println("Задача не найдена");
            return null;
        }
    }

    @Override
    public Subtask searchSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.writeHistory(subtasks.get(id));
            return subtasks.get(id);
        } else {
            System.out.println("Задача не найдена");
            return null;
        }
    }

    @Override
    public Epic searchEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.writeHistory(epics.get(id));
            return epics.get(id);
        } else {
            System.out.println("Задача не найдена");
            return null;
        }
    }

    @Override
    public void changeEpicStatusIfNeeded(int epicId) {
        Epic currentEpic = epics.get(epicId);
        Subtask checkedSubtask;
        TaskStatus flag = TaskStatus.NEW;
        ArrayList<TaskStatus> subtasksStatuses = new ArrayList<>();
        ArrayList<Integer> subtasksIdsToCheck = currentEpic.getSubtaskIds();
        for (Integer subtasksId : subtasksIdsToCheck) {
            checkedSubtask = subtasks.get(subtasksId);
            subtasksStatuses.add(checkedSubtask.getStatus());
        }
        if (subtasksStatuses.contains(TaskStatus.IN_PROGRESS)) {
            flag = TaskStatus.IN_PROGRESS;
        } else if (subtasksStatuses.contains(TaskStatus.NEW) && subtasksStatuses.contains(TaskStatus.DONE)) {
            flag = TaskStatus.IN_PROGRESS;
        } else if (!subtasksStatuses.contains(TaskStatus.NEW)) {
            flag = TaskStatus.DONE;
        }
        if (flag == TaskStatus.IN_PROGRESS) {
            currentEpic.setStatus(TaskStatus.IN_PROGRESS);
            epics.replace(epicId, currentEpic);
        } else if (flag == TaskStatus.DONE) {
            currentEpic.setStatus(TaskStatus.DONE);
            epics.replace(epicId, currentEpic);
        } else {
            currentEpic.setStatus(TaskStatus.NEW);
            epics.replace(epicId, currentEpic);
        }
    }

    @Override
    public void changeTaskOrSubtaskStatus(int id, TaskStatus status) {
        if (tasks.containsKey(id)) {
            Task currentTask = tasks.get(id);
            currentTask.setStatus(status);
            tasks.replace(id, currentTask);
        } else if (subtasks.containsKey(id)) {
            Subtask currentSubtask = subtasks.get(id);
            currentSubtask.setStatus(status);
            subtasks.replace(id, currentSubtask);
            changeEpicStatusIfNeeded(currentSubtask.getEpicId());
        } else {
            System.out.println("Такой задачи или подзадачи нет.");
        }
    }

    @Override
    public void getHistory() {
        historyManager.getHistory();
    }

    public static HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public static HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public static HashMap<Integer, Epic> getEpics() {
        return epics;
    }
}


