import java.util.*;

public class TaskManager {
    private static int idCounter = 4;
    Map<Integer, Epic> epics = new HashMap<>();
    Map<Integer, Task> tasks = new HashMap<>();
    Map<Integer, Subtask> subtasks = new HashMap<>();
    int currentlyCheckedEpicId;

    Task test1 = new Task(0, "Test1", "Desc1", TaskStatus.NEW);
    Task test2 = new Task(1, "Test2", "Desc1", TaskStatus.NEW);
    Task test3 = new Task(2, "Test3", "Desc1", TaskStatus.NEW);
    Task test4 = new Task(3, "Test4", "Desc1", TaskStatus.NEW);

    public void test() {
        tasks.put(0, test1);
        tasks.put(1, test2);
        tasks.put(2, test3);
        tasks.put(3, test4);
    }

    public void createTask() {
        System.out.println("Введите название задачи: ");
        String userInputTaskName = Main.scanner.next();
        System.out.println("Введите описание: ");
        String userInputTaskDescription = Main.scanner.next();
        int newTaskId = idCounter;
        TaskStatus newTaskStatus = TaskStatus.NEW;
        idCounter++;
        Task newTask = new Task(newTaskId, userInputTaskName, userInputTaskDescription, newTaskStatus);
        tasks.put(newTaskId, newTask);
        System.out.println("Задача ID" + (newTaskId + 1) + " была добавлена в список дел.");
    }

    public void getAllTasks() {
        int globalTaskCounter = 1;
        for (Integer epicId : epics.keySet()) {
            Epic shownEpicTask = epics.get(epicId);
            int subTaskCounter = 1;
            System.out.println("(ID " + (epicId + 1) + ") Задача " + globalTaskCounter + " " + shownEpicTask.getTaskName()
                    + ": " + shownEpicTask.getTaskDescription() + " Статус: " + shownEpicTask.getTaskStatus());
            for (Integer subtaskId : subtasks.keySet()) {
                Subtask shownSubTask = subtasks.get(subtaskId);
                if (shownSubTask.getRelatedToEpic() == epicId) {
                    System.out.println("--- (ID " + (subtaskId + 1) + ") Подзадача " + globalTaskCounter + "."
                            + subTaskCounter++ + " "
                            + shownSubTask.getTaskName() + ": " + shownSubTask.getTaskDescription() + " Статус: " + shownSubTask.getTaskStatus());
                }
            }
            globalTaskCounter++;
        }
        for (Integer taskId : tasks.keySet()) {
            Task shownTask = tasks.get(taskId);
            System.out.println("(ID " + (taskId + 1) + ") Задача " + globalTaskCounter++ + " " + shownTask.getTaskName()
                    + ": " + shownTask.getTaskDescription() + " Статус: " + shownTask.getTaskStatus());
        }
    }

    public void changeTaskContent() {
        System.out.println("Введите ID изменяемой задачи: ");
        int userInputTaskId = Main.scanner.nextInt();
        int realTaskId = userInputTaskId - 1;
        if (tasks.containsKey(realTaskId)) {
            Task currentTask = tasks.get(realTaskId);
            System.out.println("Введите название задачи: ");
            String userInputTaskName = Main.scanner.next();
            System.out.println("Введите описание: ");
            String userInputTaskDescription = Main.scanner.next();
            Task newTask = new Task(currentTask.getTaskId(), userInputTaskName, userInputTaskDescription, currentTask.getTaskStatus());
            tasks.put(realTaskId, newTask);
            System.out.println("Задача ID" + userInputTaskId + " обновлена.");
        } else if (subtasks.containsKey(realTaskId)) {
            Subtask currentTask = subtasks.get(realTaskId);
            System.out.println("Введите название задачи: ");
            String userInputTaskName = Main.scanner.next();
            System.out.println("Введите описание: ");
            String userInputTaskDescription = Main.scanner.next();
            Subtask newTask = new Subtask(currentTask.getTaskId(), userInputTaskName, userInputTaskDescription, currentTask.getTaskStatus(), currentTask.getRelatedToEpic());
            subtasks.put(realTaskId, newTask);
            System.out.println("Задача ID" + userInputTaskId + " обновлена.");
        } else if (epics.containsKey(realTaskId)) {
            Epic currentTask = epics.get(realTaskId);
            System.out.println("Введите название задачи: ");
            String userInputTaskName = Main.scanner.next();
            System.out.println("Введите описание: ");
            String userInputTaskDescription = Main.scanner.next();
            Epic newTask = new Epic(currentTask.getTaskId(), userInputTaskName, userInputTaskDescription, currentTask.getTaskStatus());
            epics.put(realTaskId, newTask);
            System.out.println("Задача ID" + userInputTaskId + " обновлена.");
        } else {
            System.out.println("Задачи под таким ID нет, повторите ввод.");
        }
    }

    public void makeTaskEpicTask(int id) {
        Task currentTask = tasks.get(id);
        int currentId = currentTask.getTaskId();
        String currentName = currentTask.getTaskName();
        String currentDescription = currentTask.getTaskDescription();
        TaskStatus currentStatus = currentTask.getTaskStatus();
        Epic convertedToEpic = new Epic(currentId, currentName, currentDescription, currentStatus);
        epics.put(id, convertedToEpic);
        tasks.remove(id);
    }

    public void addingSubtaskToEpic() {
        System.out.println("Введите ID задачи, к которой хотите добавить подзадачу:");
        int userInputTaskId = Main.scanner.nextInt();
        int realTaskId = userInputTaskId - 1;
        if (subtasks.containsKey(realTaskId) || tasks.containsKey(realTaskId) || epics.containsKey(realTaskId)) {
            if (tasks.containsKey(realTaskId)) {
                makeTaskEpicTask(realTaskId);
                createSubtask(realTaskId);
            } else {
                createSubtask(realTaskId);
            }
        } else {
            System.out.println("Задача по номеру ID " + userInputTaskId + " не найдена.");
        }
    }

    public void createSubtask(int relatedToEpicId) {
        Main.scanner.nextLine();
        System.out.println("Введите название задачи: ");
        String userInputTaskName = Main.scanner.nextLine();
        System.out.println("Введите описание: ");
        String userInputTaskDescription = Main.scanner.nextLine();
        int newTaskId = idCounter;
        TaskStatus newTaskStatus = TaskStatus.NEW;
        idCounter++;
        Subtask newSubtask = new Subtask(newTaskId, userInputTaskName, userInputTaskDescription, newTaskStatus, relatedToEpicId);
        subtasks.put(newTaskId, newSubtask);
        System.out.println("Подзадача создана.");
    }

    public void changeStatus() {
        Main.scanner.nextLine();
        System.out.println("Введите ID задачи, у которой нужно изменить статус выполнения(кроме составных задач):");
        int userInputTaskId = Main.scanner.nextInt();
        int realTaskId = userInputTaskId - 1;
        if (subtasks.containsKey(realTaskId)) {
            System.out.println("Какой статус присвоить подзадаче?:");
            System.out.println("1. Новая");
            System.out.println("2. Выполняется");
            System.out.println("3. Завершена");
            int userInputTaskStatus = Main.scanner.nextInt();
            Subtask currentSubtask = subtasks.get(realTaskId);
            int currentId = currentSubtask.getTaskId();
            String currentName = currentSubtask.getTaskName();
            String currentDescription = currentSubtask.getTaskDescription();
            int attachedToEpic = currentSubtask.getRelatedToEpic();
            currentlyCheckedEpicId = currentSubtask.getRelatedToEpic();
            if (userInputTaskStatus == 1) {
                TaskStatus currentStatus = TaskStatus.NEW;
                Subtask taskWithNewStatus = new Subtask(currentId, currentName, currentDescription, currentStatus, attachedToEpic);
                subtasks.replace(realTaskId, taskWithNewStatus);
                System.out.println("Подзадача отмечена как новая.");
                epicStatusCheck();
            } else if (userInputTaskStatus == 2) {
                TaskStatus currentStatus = TaskStatus.IN_PROGRESS;
                Subtask taskWithNewStatus = new Subtask(currentId, currentName, currentDescription, currentStatus, attachedToEpic);
                subtasks.replace(realTaskId, taskWithNewStatus);
                System.out.println("Подзадача отмечена как исполняемая.");
                epicStatusCheck();
            } else if (userInputTaskStatus == 3) {
                TaskStatus currentStatus = TaskStatus.DONE;
                Subtask taskWithNewStatus = new Subtask(currentId, currentName, currentDescription, currentStatus, attachedToEpic);
                subtasks.replace(realTaskId, taskWithNewStatus);
                System.out.println("Подзадача отмечена как завершенная.");
                epicStatusCheck();
            } else {
                System.out.println("Такого статуса нет.");
            }
        } else if (tasks.containsKey(realTaskId)) {
            System.out.println("Какой статус присвоить задаче?:");
            System.out.println("1. Новая");
            System.out.println("2. Выполняется");
            System.out.println("3. Завершена");
            int userInputTaskStatus = Main.scanner.nextInt();
            Task currentTask = tasks.get(realTaskId);
            int currentId = currentTask.getTaskId();
            String currentName = currentTask.getTaskName();
            String currentDescription = currentTask.getTaskDescription();
            if (userInputTaskStatus == 1) {
                TaskStatus currentStatus = TaskStatus.NEW;
                Task taskWithNewStatus = new Task(currentId, currentName, currentDescription, currentStatus);
                tasks.replace(realTaskId, taskWithNewStatus);
                System.out.println("Задача отмечена как новая.");
            } else if (userInputTaskStatus == 2) {
                TaskStatus currentStatus = TaskStatus.IN_PROGRESS;
                Task taskWithNewStatus = new Task(currentId, currentName, currentDescription, currentStatus);
                tasks.replace(realTaskId, taskWithNewStatus);
                System.out.println("Задача отмечена как исполняемая.");
            } else if (userInputTaskStatus == 3) {
                TaskStatus currentStatus = TaskStatus.DONE;
                Task taskWithNewStatus = new Task(currentId, currentName, currentDescription, currentStatus);
                tasks.replace(realTaskId, taskWithNewStatus);
                System.out.println("Задача отмечена как завершенная.");
            } else {
                System.out.println("Такого статуса нет.");
            }
        } else {
            System.out.println("Задача по номеру ID " + userInputTaskId + " не найдена или вы пытаетесь изменить статус составной задачи.");
        }
        Main.scanner.nextLine();
    }

    public void epicStatusCheck() {
        List<Subtask> subtasksToCheck = new ArrayList<>();
        TaskStatus flag = TaskStatus.DONE;
        for (Integer subtaskId : subtasks.keySet()) {
            Subtask shownSubTask = subtasks.get(subtaskId);
            if (shownSubTask.getRelatedToEpic() == currentlyCheckedEpicId) {
                subtasksToCheck.add(shownSubTask);
            }
        }
        for (Subtask checkedSubtask : subtasksToCheck) {
            if (checkedSubtask.getTaskStatus() == TaskStatus.IN_PROGRESS) {
                flag = TaskStatus.IN_PROGRESS;
                break;
            } else if (checkedSubtask.getTaskStatus() != TaskStatus.DONE) {
                flag = TaskStatus.NEW;
            }
        }
        for (Subtask checkedSubtask : subtasksToCheck) {
            if (flag == TaskStatus.IN_PROGRESS) {
                Epic changedEpicStatus = epics.get(checkedSubtask.getRelatedToEpic());
                changedEpicStatus.setTaskStatus(TaskStatus.IN_PROGRESS);
                epics.replace(checkedSubtask.getRelatedToEpic(), changedEpicStatus);
            } else if (flag == TaskStatus.DONE) {
                Epic changedEpicStatus = epics.get(checkedSubtask.getRelatedToEpic());
                changedEpicStatus.setTaskStatus(TaskStatus.DONE);
                epics.replace(checkedSubtask.getRelatedToEpic(), changedEpicStatus);
            }
        }
    }

    public void clearAll(){
        tasks.clear();
        epics.clear();
        subtasks.clear();
        System.out.println("Все задачи удалены.");
    }

    public void clearById(){
        System.out.println("Введите номер ID для удаляемой задачи (крупные задачи удаляются с подзадачами):");
        int requestedId = Main.scanner.nextInt();
        int realId = requestedId - 1;
        if (subtasks.containsKey(realId)){
            subtasks.remove(realId);
            System.out.println("Подзадача "+ requestedId + " удалена.");
        } else if (tasks.containsKey(realId)) {
            tasks.remove(realId);
            System.out.println("Задача "+ requestedId + " удалена.");
        } else if (epics.containsKey(realId)) {
            epics.remove(realId);
            for (Integer subtaskId : subtasks.keySet()) {
                Subtask shownSubTask = subtasks.get(subtaskId);
                if (shownSubTask.getRelatedToEpic() == realId) {
                    subtasks.remove(subtaskId);
                }
            }
            System.out.println("Задача "+ requestedId + " и её подзадачи удалены.");
        } else {
            System.out.println("Идентификатор не найден.");
        }
    }

    public void searchById() {
        System.out.println("Введите номер ID для искомой задачи:");
        int requestedId = Main.scanner.nextInt();
        int realId = requestedId - 1;
        if (tasks.containsKey(realId)) {
            Task shownTask = tasks.get(realId);
            System.out.println("(ID " + (realId + 1) + ") Задача " + shownTask.getTaskName()
                    + ": " + shownTask.getTaskDescription() + " Статус: " + shownTask.getTaskStatus());
        } else if (subtasks.containsKey(realId)) {
            Subtask shownTask = subtasks.get(realId);
            System.out.println("(ID " + (realId + 1) + ") Задача " + shownTask.getTaskName()
                    + ": " + shownTask.getTaskDescription() + " Статус: " + shownTask.getTaskStatus());
        } else if (epics.containsKey(realId)) {
            Epic shownTask = epics.get(realId);
            System.out.println("(ID " + (realId + 1) + ") Задача " + shownTask.getTaskName()
                    + ": " + shownTask.getTaskDescription() + " Статус: " + shownTask.getTaskStatus());
        }
    }
}
