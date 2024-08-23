import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.Test;
import service.InMemoryTaskManager;
import exceptions.ManagerSaveException;
import service.TaskManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    final TaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void ifCreatedTaskAbsolutelyEqualToInput() {
       Task inputTask = new Task("Lol", "Kek", TaskStatus.NEW, 10, "12:00 24.07.2024");
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
        Task task = new Task("Lol", "Kek", TaskStatus.NEW,  10, "12:00 24.07.2024");
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
        Subtask subtask = new Subtask("Lol", "Kek", TaskStatus.NEW, epicId,  10, "12:00 24.07.2024");
        final Integer subtaskId = taskManager.createSubtask(subtask);
        assertNotNull(subtaskId, "Подзадача не создалась, возвращено отрицательное значение.");
        subtask = taskManager.searchSubtaskById(subtaskId);
        Subtask subtask1 = taskManager.searchSubtaskById(subtaskId);
        assertEquals(subtask, subtask1, "Подзадачи не совпадают.");
    }

    @Test
    public void changeTaskReallyChangesTask() {
        Task task = new Task("Lol", "Kek", TaskStatus.NEW,  10, "12:00 24.07.2024");
        final int taskId = taskManager.createTask(task);
        System.out.println(taskManager.getAllTasks());
        Task updatedTask = new Task("Покормить собаку.", "Кек", TaskStatus.NEW,  15, "12:00 25.07.2024");
        taskManager.changeTask(taskId, updatedTask);
        Task taskViaSearch = taskManager.searchTaskById(taskId);
        System.out.println(taskManager.getAllTasks());
        assertEquals(task.getId(), taskViaSearch.getId(), "Изменился ID.");
        assertNotEquals(task.getName(), taskViaSearch.getName(), "Не изменилось наименование задачи.");
        assertNotEquals(task.getDescription(), taskViaSearch.getDescription(), "Не изменилось описание задачи.");
        assertNotEquals(task.getDuration(), taskViaSearch.getDuration(), "Не изменилась продолжительность задачи.");
        assertNotEquals(task.getStartTime(), taskViaSearch.getStartTime(), "Не изменилось время начала задачи.");
    }

    @Test
    public void ifRemoveTaskByIdWorks() {
        Task task = new Task("Lol", "Kek", TaskStatus.NEW,  10, "12:00 24.07.2024");
        final int taskId = taskManager.createTask(task);
        taskManager.removeTaskById(taskId);
        assertNull(taskManager.searchTaskById(taskId), "Задача не удалена.");
    }

    @Test
    public void ifRemoveEpicByIdAlsoRemovesSubtasks() {
        Epic epic = new Epic("Lol", "Kek", TaskStatus.NEW);
        final int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Lol", "Kek", TaskStatus.NEW, epicId,  10, "12:00 24.07.2024");
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
        Subtask subtask = new Subtask("SubtaskLol", "Kek", TaskStatus.NEW, epicId, 10, "12:00 24.07.2024");
        taskManager.createSubtask(subtask);
        Subtask subtask2 = new Subtask("SubtaskLol2", "Kek", TaskStatus.NEW, epicId, 10, "12:10 24.07.2024");
        taskManager.clearEpics();
        assertNull(taskManager.getAllEpics(), "Все эпики не удалились.");
        assertNull(taskManager.getAllSubtasks(), "Подзадачи не удалились вместе со всеми эпиками.");
    }

    @Test
    public void ifPastTaskKeptInHistoryAfterChange() {
        Task task = new Task("Lol", "Kek", TaskStatus.NEW,  10, "12:00 24.07.2024");
        final int taskId = taskManager.createTask(task);
        Task changedTask = new Task("Lol22", "Kek22", TaskStatus.DONE,  15, "12:00 25.07.2024");
        taskManager.searchTaskById(taskId);
        Task beforeChange = taskManager.getHistory().getFirst();
        taskManager.changeTask(taskId, changedTask);
        taskManager.searchTaskById(taskId);
        Task afterChange = taskManager.getHistory().getFirst();
        assertEquals(afterChange, changedTask, "В истории сохранена не предыдущая версия задачи");
    }

    @Test
    public void isUnableToSetSubtaskIdAsEpicId() {
        Epic inputTask = new Epic("Lol", "Kek", TaskStatus.NEW);
        int epicId = taskManager.createEpic(inputTask);
        Subtask subtask = new Subtask("SubtaskLol", "Kek", TaskStatus.NEW, epicId,  10, "12:00 24.07.2024");
        int subtaskId = taskManager.createSubtask(subtask);
        Epic epicTaskFromManager = taskManager.searchEpicById(epicId);
        epicTaskFromManager.setSubtaskIds(epicTaskFromManager.getId());
        assertFalse(epicTaskFromManager.getSubtaskIds().contains(epicTaskFromManager.getId()), "Эпик назначил себя в виде подзадачи.");
        Subtask subtaskFromManager = taskManager.searchSubtaskById(subtaskId);
        subtaskFromManager.setEpicId(subtaskId);
        assertNotEquals(subtaskFromManager.getEpicId(), subtaskId, "Подзадача не может быть подзадачей самой себя.");
    }

    @Test
    public void changeEpic() {
        Epic epic = new Epic("Lol2", "Kek2", TaskStatus.NEW);
        Epic changedEpic = new Epic("Lol3", "Kek3", TaskStatus.NEW);
        int epicId = taskManager.createEpic(epic);
        taskManager.changeEpic(epicId, changedEpic);
        System.out.println(taskManager.searchEpicById(epicId));
        assertEquals(taskManager.searchEpicById(epicId), changedEpic, "Эпик не поменялся.");
    }

    @Test
    public void clearTasks() {
        Task task0 = new Task("lol", "kek", TaskStatus.NEW, 10, "12:00 24.07.2024");
        Task task1 = new Task("lol1", "1111", TaskStatus.NEW, 10, "12:20 24.07.2024");
        taskManager.createTask(task0);
        taskManager.createTask(task1);
        assertEquals(2, taskManager.getAllTasks().size(), "Не две задачи создались.");
        taskManager.clearTasks();
        assertNull(taskManager.getAllTasks(), "Таски не нулевые.");
    }

    @Test
    public void clearEpicsAndSubtasks() {
        Epic epic = new Epic("Lol2", "Kek2", TaskStatus.NEW);
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("SubtaskLol", "Kek", TaskStatus.NEW, epicId, 10, "12:00 24.07.2024");
        taskManager.createSubtask(subtask);
        assertEquals(1, taskManager.getAllEpics().size(), "Эпик не создался.");
        assertEquals(1, taskManager.getAllSubtasks().size(), "Подзадача не создалась.");
        taskManager.clearEpics();
        taskManager.clearSubtasks();
        assertNull(taskManager.getAllEpics(), "Эпики все еще есть в списке.");
        assertNull(taskManager.getAllSubtasks(), "Сабтаски все еще есть в списке.");
    }

    @Test
    public void addToPrioritizedTasks() {
        Task task0 = new Task("lol", "kek", TaskStatus.NEW, 10, "12:00 24.07.2024");
        Task taskChanged = new Task("lol111", "kek", TaskStatus.NEW, 10, "12:00 24.07.2024");
        int taskId = taskManager.createTask(task0);
        Epic epic = new Epic("Lol2", "Kek2", TaskStatus.NEW);
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("SubtaskLol", "Kek", TaskStatus.NEW, epicId, 10, "12:30 24.07.2024");
        int subtaskId = taskManager.createSubtask(subtask);
        assertEquals(2, taskManager.getPrioritizedTasks().size(), "prioritySet is not of size 2");
        List<Task> priorityList = taskManager.getPrioritizedTasks().stream().toList();
        assertEquals(task0, priorityList.getFirst(), "Task not equals to task in prioritizedSet");
        assertEquals(subtask, priorityList.get(1), "Subtask not equals to Subtask in prioritizedSet");
        taskManager.changeTask(taskId, taskChanged);
        priorityList = taskManager.getPrioritizedTasks().stream().toList();
        assertEquals(taskChanged, priorityList.getFirst(), "Не обновилась задача в отсортированном множестве.");
        taskManager.removeTaskById(taskId);
        assertEquals(1, taskManager.getPrioritizedTasks().size(), "prioritySet is not of size 1 after deletion");
        taskManager.removeSubtaskById(subtaskId);
        assertNull(taskManager.getPrioritizedTasks(), "prioritySet is not of size 0 after deletion");
    }

    @Test
    public void removeSubtaskById() {
        Epic epic = new Epic("Lol2", "Kek2", TaskStatus.NEW);
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("SubtaskLol", "Kek", TaskStatus.NEW, epicId, 10, "12:30 24.07.2024");
        int subtaskId = taskManager.createSubtask(subtask);
        Epic epicInManager = taskManager.searchEpicById(epicId);
        assertEquals(epicInManager.getSubtaskIds().getFirst(), subtaskId, "Subtask is not on list inside Epic");
        taskManager.removeSubtaskById(subtaskId);
        epicInManager = taskManager.searchEpicById(epicId);
        assertTrue(epicInManager.getSubtaskIds().isEmpty(), "Subtask Id was not removed inside epic");
    }

    @Test
    public void changeTaskOrSubtaskStatus() {
        Task task0 = new Task("lol", "kek", TaskStatus.NEW, 10, "12:00 24.07.2024");
        int taskId = taskManager.createTask(task0);
        taskManager.changeTaskOrSubtaskStatus(taskId, TaskStatus.IN_PROGRESS);
        assertNotEquals(TaskStatus.NEW, taskManager.searchTaskById(taskId).getStatus(), "Status was not changed to IN PROGRESS");
        Epic epic = new Epic("Lol2", "Kek2", TaskStatus.NEW);
        int epicId = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("SubtaskLol", "Kek", TaskStatus.NEW, epicId, 10, "12:30 24.07.2024");
        int subtaskId = taskManager.createSubtask(subtask);
        taskManager.changeTaskOrSubtaskStatus(subtaskId, TaskStatus.IN_PROGRESS);
        assertNotEquals(TaskStatus.NEW, taskManager.searchSubtaskById(subtaskId).getStatus(), "Subtask status was not changed to IN PROGRESS");
    }

    @Test
    public void epicStatusCalculation() {
        Epic epic = new Epic("Lol2", "Kek2", TaskStatus.NEW);
        int epicId = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("SubtaskLol1", "Kek1", TaskStatus.NEW, epicId, 30, "12:30 24.07.2024");
        Subtask subtask2 = new Subtask("SubtaskLol2", "Kek2", TaskStatus.NEW, epicId, 30, "13:00 24.07.2024");
        Subtask subtask3 = new Subtask("SubtaskLol3", "Kek3", TaskStatus.NEW, epicId, 30, "13:30 24.07.2024");
        int subtask1Id = taskManager.createSubtask(subtask1);
        int subtask2Id = taskManager.createSubtask(subtask2);
        int subtask3Id = taskManager.createSubtask(subtask3);
        assertEquals(taskManager.searchEpicById(epicId).getStatus(), TaskStatus.NEW, "Epic Status not NEW.");
        taskManager.changeTaskOrSubtaskStatus(subtask1Id, TaskStatus.DONE);
        taskManager.changeTaskOrSubtaskStatus(subtask2Id, TaskStatus.DONE);
        taskManager.changeTaskOrSubtaskStatus(subtask3Id, TaskStatus.DONE);
        assertEquals(taskManager.searchEpicById(epicId).getStatus(), TaskStatus.DONE, "Epic Status not DONE.");
        taskManager.changeTaskOrSubtaskStatus(subtask1Id, TaskStatus.NEW);
        assertEquals(taskManager.searchEpicById(epicId).getStatus(), TaskStatus.IN_PROGRESS, "Epic Status not IN PROGRESS.");
        taskManager.changeTaskOrSubtaskStatus(subtask1Id, TaskStatus.IN_PROGRESS);
        taskManager.changeTaskOrSubtaskStatus(subtask2Id, TaskStatus.IN_PROGRESS);
        taskManager.changeTaskOrSubtaskStatus(subtask3Id, TaskStatus.IN_PROGRESS);
        assertEquals(taskManager.searchEpicById(epicId).getStatus(), TaskStatus.IN_PROGRESS, "Epic Status not IN PROGRESS.");
    }

    @Test
    public void intersectionsValidation() {
        assertThrows(ManagerSaveException.class, () -> {
            Task task0 = new Task("lol", "kek", TaskStatus.NEW, 10, "12:00 24.07.2024");
            Task task1 = new Task("lol1", "kek1", TaskStatus.NEW, 10, "12:05 24.07.2024");
            taskManager.createTask(task0);
            task1.setId(1);
            //taskManager.createTask(task1);
            taskManager.addToPrioritizedTasks(task1);
            System.out.println(taskManager.getPrioritizedTasks());
        }, "Попытка добавить в существующий временной интервал должна давать ошибку.");
    }
}