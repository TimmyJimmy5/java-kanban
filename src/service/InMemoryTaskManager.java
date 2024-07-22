package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    Managers managers = new Managers();
    HistoryManager historyManager = managers.getDefaultHistory();
    private int counter = 0;

    private int counterIncrement() {
        return counter++;
    }

    @Override
    public int createTask(Task task) {
        int taskId = counterIncrement();
        task.setId(taskId);
        tasks.put(taskId, task);
        return tasks.get(taskId).getId();
    }

    @Override
    public int createEpic(Epic epic) {
        int epicId = counterIncrement();
        epic.setId(epicId);
        epics.put(epicId, epic);
        return epics.get(epicId).getId();
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        int subtaskId = counterIncrement();
        subtask.setId(subtaskId);
        Epic currentEpic = epics.get(subtask.getEpicId());
        if (!epics.isEmpty() && currentEpic != null) {
            subtasks.put(subtaskId, subtask);
            currentEpic.setSubtaskIds(subtaskId);
            changeEpicStatusIfNeeded(subtask.getEpicId());
            calculateStartEndTimesForEpic(subtask.getEpicId());
            return subtasks.get(subtaskId).getId();
        } else {
            return null;
        }
    }

    @Override
    public void changeTask(int id, Task task) {
        if (tasks.containsKey(id)) {
            task.setId(id);
            tasks.replace(id, task);
        } else {
            System.out.println("Такой задачи нет");
        }
    }

    @Override
    public void changeEpic(int id, Epic epic) {
        if (epics.containsKey(id)) {
            epic.setId(id);
            epics.replace(id, epic);
            changeEpicStatusIfNeeded(epics.get(id).getId());
            calculateStartEndTimesForEpic(epics.get(id).getId());
        } else {
            System.out.println("Такая составная задача отсутствует.");
        }
    }

    @Override
    public void changeSubtask(int id, Subtask subtask) {
        if (subtasks.containsKey(id)) {
            subtask.setId(id);
            subtasks.replace(id, subtask);
            changeEpicStatusIfNeeded(subtasks.get(id).getEpicId());
            calculateStartEndTimesForEpic(subtasks.get(id).getEpicId());
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
            historyManager.removeTask(id);
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
                    historyManager.removeTask(subtask);
                }
            }
            epics.remove(id);
            System.out.println("Составная задача и её подзадачи удалены.");
            historyManager.removeTask(id);
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
                List<Integer> subtasksId = currentEpic.getSubtaskIds();
                subtasksId.remove((Integer) subtask.getId());
                subtasks.remove(id);
                changeEpicStatusIfNeeded(currentEpic.getId());
                calculateStartEndTimesForEpic(currentEpic.getId());
                historyManager.removeTask(id);
            }
        } else {
            System.out.println("Подзадача не найдена");
        }
    }

    @Override
    public Task searchTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.addTask(tasks.get(id));
            return tasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Subtask searchSubtaskById(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.addTask(subtasks.get(id));
            return subtasks.get(id);
        } else {
            return null;
        }
    }

    @Override
    public Epic searchEpicById(int id) {
        if (epics.containsKey(id)) {
            historyManager.addTask(epics.get(id));
            return epics.get(id);
        } else {
            return null;
        }
    }

    public void calculateStartEndTimesForEpic(int epicId) {
        Epic currentEpic = epics.get(epicId);
        Subtask checkedSubtask;
        List<Integer> subtaskIdsToCheck = new ArrayList<>(currentEpic.getSubtaskIds());
        List<LocalDateTime> startTimes = new ArrayList<>();
        List<Duration> durTimes = new ArrayList<>();
        if (!subtaskIdsToCheck.isEmpty()) {
            for (int subtasksId : subtaskIdsToCheck) {
                checkedSubtask = subtasks.get(subtasksId);
                if (checkedSubtask.getStartTime() != null && checkedSubtask.getDuration() != null) {
                    startTimes.add(checkedSubtask.getStartTime());
                    durTimes.add(checkedSubtask.getDuration());
                }
            }

            LocalDateTime tempStart = startTimes.getFirst();
            for (LocalDateTime time : startTimes) {
                if (time.isBefore(tempStart)) {
                    tempStart = time;
                }
            }

            Duration accumulatedDuration = Duration.ZERO;
            for (Duration duration : durTimes) {
                accumulatedDuration = accumulatedDuration.plusMinutes(duration.toMinutes());
            }

            currentEpic.setStartTime(tempStart);
            currentEpic.setDuration(accumulatedDuration);
            currentEpic.setEndTime(tempStart.plusMinutes(accumulatedDuration.toMinutes()));
            epics.replace(epicId, currentEpic);
        }
    }

    @Override
    public void changeEpicStatusIfNeeded(int epicId) {
        Epic currentEpic = epics.get(epicId);
        Subtask checkedSubtask;
        TaskStatus flag = TaskStatus.NEW;
        List<TaskStatus> subtasksStatuses = new ArrayList<>();
        List<Integer> subtasksIdsToCheck = currentEpic.getSubtaskIds();
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
    public List<Task> getHistory() {
        if (historyManager.getHistoryOfTasks().isEmpty()) {
            return null;
        } else {
            return historyManager.getHistoryOfTasks();
        }
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public void autoloadPutTask(Task task) {
        if (task.getClass().equals(Task.class)) {
            tasks.put(task.getId(), task);
        } else if (task.getClass().equals(Epic.class)) {
            epics.put(task.getId(), (Epic) task);
        } else if (task.getClass().equals(Subtask.class)) {
            Subtask subtask = (Subtask) task;
            subtasks.put(subtask.getId(), subtask);
            epics.get(subtask.getEpicId()).setSubtaskIds(subtask.getId());
            calculateStartEndTimesForEpic(subtask.getEpicId());
        }
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}


