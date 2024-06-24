import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    final TaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void ifCreatedTaskAbsolutelyEqualToInput() {
       Task inputTask = new Task("Lol", "Kek", TaskStatus.NEW);
       final int inputTaskId = taskManager.createTask(inputTask);
       Task testedTask = taskManager.searchTaskById(inputTaskId);
       assertEquals(inputTask.getName(), testedTask.getName(), "Поля наименований не равны");
       assertEquals(inputTask.getDescription(), testedTask.getDescription(), "Поля описания не равны");
       assertEquals(inputTask.getId(), testedTask.getId(), "Поля идентификаторов не равны");
       assertEquals(inputTask, testedTask, "Добавленная и введенная задачи не совпадают.");
   }

    @Test
    public void ifCreatedEpicAbsolutelyEqualToInput() {
        Epic inputTask = new Epic("Lol", "Kek", TaskStatus.NEW);
        final int inputTaskId = taskManager.createEpic(inputTask);
        Epic testedTask = taskManager.searchEpicById(inputTaskId);
        assertEquals(inputTask.getName(), testedTask.getName(), "Поля наименований не равны");
        assertEquals(inputTask.getDescription(), testedTask.getDescription(), "Поля описания не равны");
        assertEquals(inputTask.getId(), testedTask.getId(), "Поля идентификаторов не равны");
        assertEquals(inputTask.getSubtaskIds(), testedTask.getSubtaskIds(), "Поля сабтасков не равны.");
        assertEquals(inputTask, testedTask, "Добавленная и введенная задачи не совпадают.");
    }

    @Test
    public void taskMustBeEqualIfSameId(){
        Task task = new Task("Lol", "Kek", TaskStatus.NEW);
        final int taskId = taskManager.createTask(task);
        task = taskManager.searchTaskById(taskId);
        Task task1 = taskManager.searchTaskById(taskId);
        assertEquals(task, task1, "Задачи не равны.");
    }

    @Test
    public void epicTaskMustBeEqualIfSameId() {
        Epic epic = new Epic("Lol", "Kek", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(epic);
        epic = taskManager.searchEpicById(epicId);
        Epic epic1 = taskManager.searchEpicById(epicId);
        assertEquals(epic, epic1, "Эпики не совпадают.");
    }

    @Test
    public void subTaskMustBeEqualIfSameId() {
        Epic epic = new Epic("Lol", "Kek", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Lol", "Kek", TaskStatus.NEW, epicId);
        final Integer subtaskId = taskManager.createSubtask(subtask);
        assertNotNull(subtaskId, "Подзадача не создалась, возвращено отрицательное значение.");
        subtask = taskManager.searchSubtaskById(subtaskId);
        Subtask subtask1 = taskManager.searchSubtaskById(subtaskId);
        assertEquals(subtask, subtask1, "Подзадачи не совпадают.");
    }

    @Test
    public void changeTaskReallyChangesTask() {
        Task task = new Task("Lol", "Kek", TaskStatus.NEW);
        final int taskId = taskManager.createTask(task);
        System.out.println(taskManager.getAllTasks());
        Task updatedTask = new Task("Покормить собаку.", "Кек", TaskStatus.NEW);
        taskManager.changeTask(taskId, updatedTask);
        Task taskViaSearch = taskManager.searchTaskById(taskId);
        System.out.println(taskManager.getAllTasks());
        assertEquals(task.getId(), taskViaSearch.getId(), "Изменился ID.");
        assertNotEquals(task.getName(), taskViaSearch.getName(), "Не изменилось наименование задачи.");
        assertNotEquals(task.getDescription(), taskViaSearch.getDescription(), "Не изменилось описание задачи.");
    }

    @Test
    public void ifRemoveTaskByIdWorks() {
        Task task = new Task("Lol", "Kek", TaskStatus.NEW);
        final int taskId = taskManager.createTask(task);
        taskManager.removeTaskById(taskId);
        assertNull(taskManager.searchTaskById(taskId), "Задача не удалена.");
    }

    @Test
    public void ifRemoveEpicByIdAlsoRemovesSubtasks() {
        Epic epic = new Epic("Lol", "Kek", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Lol", "Kek", TaskStatus.NEW, epicId);
        final int subtaskId = taskManager.createSubtask(subtask);
        assertNotEquals(-1, subtaskId, "Подзадача не создалась, возвращено отрицательное значение.");
        taskManager.removeEpicById(epicId);
        assertNull(taskManager.searchEpicById(epicId), "model.Epic не удалился");
        assertNull(taskManager.searchSubtaskById(subtaskId), "Подзадача эпика не удалилась");
    }

    @Test
    public void ifRemoveAllEpicsAlsoRemovesAllSubtasks() {
        Epic epic = new Epic("Lol", "Kek", TaskStatus.NEW);
        Epic epic2 = new Epic("Lol2", "Kek2", TaskStatus.NEW);
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("SubtaskLol", "Kek", TaskStatus.NEW, epicId);
        taskManager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("SubtaskLol2", "Kek", TaskStatus.NEW, epicId);
        taskManager.clearEpics();
        assertNull(taskManager.getAllEpics(), "Все эпики не удалились.");
        assertNull(taskManager.getAllSubtasks(), "Подзадачи не удалились вместе со всеми эпиками.");
    }

    @Test
    public void ifPastTaskKeptInHistoryAfterChange() {
        Task task = new Task("Lol", "Kek", TaskStatus.NEW);
        final int taskId = taskManager.createTask(task);
        Task changedTask = new Task("Lol22", "Kek22", TaskStatus.DONE);
        taskManager.searchTaskById(taskId);
        Task beforeChange = taskManager.getHistory().getFirst();
        taskManager.changeTask(taskId, changedTask);
        taskManager.searchTaskById(taskId);
        Task afterChange = taskManager.getHistory().getFirst();
        assertEquals(afterChange, beforeChange, "В истории сохранена не предыдущая версия задачи");
    }



}