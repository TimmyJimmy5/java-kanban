package service;

import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import model.TaskType;

import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File tasksFile;

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public Integer createSubtask(Subtask subtask) {
        Integer id = super.createSubtask(subtask);
        save();
        return id;
    }

    private void setIdCounter(Task task) {
        if (task.getId() >= getCounter()) {
            setCounter(task.getId() + 1);
        }
    }

    public FileBackedTaskManager(Path pathValue, Path fileName) throws ManagerSaveException {
        String path;
        if ((!pathValue.toString().isBlank()) || !pathValue.toString().isEmpty()) {
            path = String.valueOf(pathValue);
        } else {
            path = "./src/service/";
        }
        if (fileName.toString().isBlank() || fileName.toString().isEmpty()) {
            fileName = Paths.get(path, "TaskSaveFile.csv");
        } else {
            fileName = Path.of(fileName.toString());
        }
        if (!Files.isDirectory(pathValue)) {
            try {
                Files.createDirectory(pathValue);
            } catch (IOException exception) {
                exception.printStackTrace();
                throw new ManagerSaveException("Не удалось создать директорию.");
            }
        }
        if (!Files.exists(fileName)) {
            try {
                Files.createFile(fileName);
                this.tasksFile = new File(String.valueOf(fileName));
            } catch (IOException exception) {
                exception.printStackTrace();
                throw new ManagerSaveException("Не удалось создать файл сохранения.");
            }
        } else {
            this.tasksFile = new File(String.valueOf(fileName));
        }
    }

    public FileBackedTaskManager(File file) throws ManagerSaveException {
        this.tasksFile = file;
    }

    private String taskToString(Task task) throws ManagerSaveException, IllegalArgumentException {
        try {
            if (task.getTaskType() != null) {
                if (task.getTaskType() == TaskType.TASK) {
                    return String.format("%d,%S,%s,%S,%s,%s,%s", task.getId(), task.getTaskType(), task.getName(), task.getStatus(), task.getDescription(), task.getDuration().toMinutes(), task.getStartTime().format(Task.DATE_TIME_FORMATTER));
                } else if (task.getTaskType() == TaskType.EPIC) {
                    Epic epic = (Epic) task;
                    StringBuilder sb = new StringBuilder();
                    String subtaskIDs = "";
                    for (Integer subtaskId : epic.getSubtaskIds()) {
                        sb.append(subtaskId);
                    }
                    subtaskIDs = sb.toString();
                    return String.format("%d,%S,%s,%S,%s,%s", epic.getId(), epic.getTaskType(), epic.getName(), epic.getStatus(), epic.getDescription(), subtaskIDs);
                } else if (task.getTaskType() == TaskType.SUBTASK) {
                    Subtask subtask = (Subtask) task;
                    return String.format("%d,%S,%s,%S,%s,%s,%s,%s", subtask.getId(), subtask.getTaskType(), subtask.getName(), subtask.getStatus(), subtask.getDescription(), subtask.getEpicId(), subtask.getDuration().toMinutes(), subtask.getStartTime().format(Task.DATE_TIME_FORMATTER));
                }
            }
        } catch (IllegalArgumentException exception) {
            exception.printStackTrace();
            throw new IllegalArgumentException(exception.getMessage());
        }
        return null;
    }

    private Task taskFromString(String stringTask) throws ManagerSaveException, NumberFormatException {
        String[] task = stringTask.split(",");
        Task returnedTask = null;
        TaskStatus taskStatus;

        try {
            Integer.parseInt(task[0]);
        } catch (NumberFormatException exception) {
            throw new ManagerSaveException("Не удалось установить ID задачи. Введен неверный ID из файла.");
        }
        if (stringTask.isBlank() && stringTask.isEmpty()) {
            throw new ManagerSaveException("Не удалось перенести задачу из файла, т.к. строка пустая.");
        }
        try {
            switch (TaskType.valueOf(task[1])) {
                case TASK:
                    taskStatus = TaskStatus.valueOf(task[3]);
                    returnedTask = new Task(task[2], task[4], taskStatus, Integer.parseInt(task[5]), task[6]);
                    returnedTask.setId(Integer.parseInt(task[0]));
                    returnedTask.setTaskType(TaskType.valueOf(task[1]));
                    setIdCounter(returnedTask);
                    return returnedTask;
                case EPIC:
                    taskStatus = TaskStatus.valueOf(task[3]);
                    returnedTask = new Epic(task[2], task[4], taskStatus);
                    returnedTask.setId(Integer.parseInt(task[0]));
                    returnedTask.setTaskType(TaskType.valueOf(task[1]));
                    setIdCounter(returnedTask);
                    return returnedTask;
                case SUBTASK:
                    taskStatus = TaskStatus.valueOf(task[3]);
                    returnedTask = new Subtask(task[2], task[4], taskStatus, Integer.parseInt(task[5]), Integer.parseInt(task[6]), task[7]);
                    returnedTask.setId(Integer.parseInt(task[0]));
                    returnedTask.setTaskType(TaskType.valueOf(task[1]));
                    setIdCounter(returnedTask);
                    return returnedTask;
            }
        } catch (NumberFormatException exception) {
            exception.printStackTrace();
            throw new NumberFormatException(exception.getMessage());
        }
        return returnedTask;
    }

    public void save() throws ManagerSaveException {
        if (tasksFile != null) {
            try (BufferedWriter fr = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tasksFile), StandardCharsets.UTF_8))) {
                fr.append("id,type,name,status,description,epic" + "\n");
                for (Task task : getTasks().values()) {
                    fr.write(taskToString(task) + "\n");
                }
                for (Task task : getEpics().values()) {
                    fr.write(taskToString(task) + "\n");
                }
                for (Task task : getSubtasks().values()) {
                    fr.write(taskToString(task) + "\n");
                }
                fr.append(" " + "\n");
                if (getHistory() != null) {
                    for (Task history : getHistory()) {
                        Integer historyId = history.getId();
                        fr.append(String.valueOf(historyId)).append(",");
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new ManagerSaveException("Не удалось внести задачи в файл сохранения.");
            }
        }
    }

    public FileBackedTaskManager loadFromFile(File file) throws ManagerSaveException, IOException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8));
        List<String> tasks = new ArrayList<>();
        List<Integer> historyIds = new ArrayList<>();
        while (bufferedReader.ready()) {
            String line = bufferedReader.readLine();
            tasks.add(line);
        }
        for (int i = 1; i < tasks.size(); i++) {
            if (tasks.get(i).isBlank()) {
                break;
            }
            Task taskToReadFromFile = taskFromString(tasks.get(i));
            fileBackedTaskManager.autoloadPutTask(taskToReadFromFile);
            fileBackedTaskManager.setIdCounter(taskToReadFromFile);
            if (taskToReadFromFile.getTaskType() != TaskType.EPIC) {
                fileBackedTaskManager.addToPrioritizedTasks(taskToReadFromFile);
            }
        }
        String[] history = tasks.getLast().split(",");
        for (String string : history) {
            if (!string.isBlank()) {
                historyIds.add(Integer.parseInt(string));
            }
        }
        for (Integer id : historyIds) {
            if (fileBackedTaskManager.getTasks().containsKey(id)) {
                fileBackedTaskManager.searchTaskById(id);
            } else if (fileBackedTaskManager.getEpics().containsKey(id)) {
                fileBackedTaskManager.searchEpicById(id);
            } else if (fileBackedTaskManager.getSubtasks().containsKey(id)) {
                fileBackedTaskManager.searchSubtaskById(id);
            }
        }
        bufferedReader.close();
        //fileBackedTaskManager.restorePrioritizedTasks();
        return fileBackedTaskManager;
    }

    @Override
    public void changeTask(int id, Task task) {
        super.changeTask(id, task);
        save();
    }

    @Override
    public void changeEpic(int id, Epic epic) {
        super.changeEpic(id, epic);
        save();
    }

    @Override
    public void changeSubtask(int id, Subtask subtask) {
        super.changeSubtask(id, subtask);
        save();
    }

    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return super.getAllSubtasks();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubtaskById(int id) {
        super.removeSubtaskById(id);
        save();
    }

    @Override
    public Task searchTaskById(int id) {
        Task task = super.searchTaskById(id);
        save();
        return task;
    }

    @Override
    public Subtask searchSubtaskById(int id) {
        Subtask task = super.searchSubtaskById(id);
        save();
        return task;
    }

    @Override
    public Epic searchEpicById(int id) {
        Epic epic = super.searchEpicById(id);
        save();
        return epic;
    }

    @Override
    public void changeEpicStatusIfNeeded(int epicId) {
        super.changeEpicStatusIfNeeded(epicId);
        save();
    }

    @Override
    public void changeTaskOrSubtaskStatus(int id, TaskStatus status) {
        super.changeTaskOrSubtaskStatus(id, status);
        save();
    }
}
