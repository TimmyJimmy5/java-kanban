import java.util.*;

public class TaskManager {
    private static int counter = 0;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    private int globalCounterIncrement() {
        return counter++;
    }

    public void createTask(Task task) {
        int taskId = globalCounterIncrement();
        task.setId(taskId);
        tasks.put(taskId, task);
    }

    public void createEpic(Epic epic) {
        int epicId = globalCounterIncrement();
        epic.setId(epicId);
        epics.put(epicId, epic);
    }

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

    public void changeTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.replace(task.getId(), task);
        } else {
            System.out.println("Такой задачи нет");
        }
    }

    public void changeEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.replace(epic.getId(), epic);
            changeEpicStatusIfNeeded(epic.getId());
        } else {
            System.out.println("Такая составная задача отсутствует.");
        }
    }

    public void changeSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.replace(subtask.getId(), subtask);
            changeEpicStatusIfNeeded(subtask.getEpicId());
        } else {
            System.out.println("Подзадача не найдена.");
        }
    }

    public List<Epic> getAllEpics() {
        if (epics.isEmpty()) {
            System.out.println("Крупных задач нет.");
            return null;
        }
        return new ArrayList<>(epics.values());
    }

    public List<Task> getAllTasks() {
        if (tasks.isEmpty()) {
            System.out.println("Задач в списке нет.");
            return Collections.emptyList();
        }
        return new ArrayList<>(tasks.values());
    }

    public List<Subtask> getAllSubtasks() {
        if (subtasks.isEmpty()) {
            System.out.println("Задач в списке нет.");
            return null;
        }
        return new ArrayList<>(subtasks.values());
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearSubtasks() {
        for (Integer epicId: epics.keySet()) {
            Epic currentEpic = epics.get(epicId);
            currentEpic.removeSubtasksIds();
        }
        subtasks.clear();
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }


    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
            System.out.println("Задача удалена.");
        } else {
            System.out.println("Задача не найдена");
        }
    }

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

    public Task searchTaskById(int id) {
        if (tasks.containsKey(id)) {
            return tasks.get(id);
        } else {
            System.out.println("Задача не найдена");
            return null;
        }
    }

    public Subtask searchSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            return subtasks.get(id);
        } else {
            System.out.println("Задача не найдена");
            return null;
        }
    }

    public Epic searchEpicById(int id) {
        if (epics.containsKey(id)) {
            return epics.get(id);
        } else {
            System.out.println("Задача не найдена");
            return null;
        }
    }

    public void changeEpicStatusIfNeeded(int epicId) {
        List<Subtask> subtasksListToCheck = new ArrayList<>();
        Epic currentEpic = epics.get(epicId);
        Subtask checkedSubtask;
        TaskStatus flag = TaskStatus.DONE;
        ArrayList<Integer> subtasksToCheck = currentEpic.getSubtaskIds();
        for (Integer subtasksId : subtasksToCheck){
            checkedSubtask = subtasks.get(subtasksId);
            subtasksListToCheck.add(checkedSubtask);
            if (checkedSubtask.getStatus() == TaskStatus.IN_PROGRESS) {
                flag = TaskStatus.IN_PROGRESS;
                break;
            } else if (checkedSubtask.getStatus() != TaskStatus.DONE) {
                flag = TaskStatus.IN_PROGRESS;
            }
        }
        for (Subtask anySubtask : subtasksListToCheck) {
            if (flag == TaskStatus.IN_PROGRESS) {
                currentEpic.setStatus(TaskStatus.IN_PROGRESS);
                epics.replace(epicId, currentEpic);
            } else {
                currentEpic.setStatus(TaskStatus.DONE);
                epics.replace(epicId, currentEpic);
            }
        }
    }

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
}


