import model.Epic;
import model.Subtask;
import model.Task;
import model.TaskStatus;
import service.FileBackedTaskManager;
import service.ManagerSaveException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws ManagerSaveException, IOException {

        File file = new File("./src/service/TaskSaveFile1.csv");
        //Блок мейна, посвященный проверке сохранения в файл
        FileBackedTaskManager manager = new FileBackedTaskManager(Paths.get(""), file.toPath());
        //Создаю 3 таски, 2 эпика и 5 сабтасок для них
        Task testTask1 = new Task("Покормить кота.", "Корм находится на верхней полке", TaskStatus.NEW, 30, "12:00 24.07.2024");
        Task testTask2 = new Task("Покормить собаку.", "Корм находится на средней полке", TaskStatus.NEW,  30, "12:00 25.07.2024");
        Task testTask3 = new Task("Покормить домашнего медведя.", "Корм находится на нижней полке", TaskStatus.NEW,  30, "12:00 26.07.2024");
        manager.createTask(testTask1);
        manager.createTask(testTask2);
        manager.createTask(testTask3);
        manager.createEpic(new Epic("Построить зиккурат.", "Жизнь за Нер'Зула", TaskStatus.NEW));
        manager.createEpic(new Epic("Построить казарму.", "Здесь нельзя строить", TaskStatus.NEW));
        manager.createSubtask(new Subtask("Нужно больше золота.", "Построить рудники.", TaskStatus.NEW, 3, 30, "12:00 23.07.2024"));
        manager.createSubtask(new Subtask("Нужно больше дерева.", "Рубить лес.", TaskStatus.IN_PROGRESS, 3, 30, "12:30 23.07.2024"));
        manager.createSubtask(new Subtask("Нужно больше еды.", "Печем булки!.", TaskStatus.DONE, 3, 30, "13:00 23.07.2024"));
        manager.createSubtask(new Subtask("Нужно расчистить площадку", "Вырубим лес.", TaskStatus.DONE, 4,30, "13:30 23.07.2024"));
        manager.createSubtask(new Subtask("Нужно немного подумать", "Но это не точно", TaskStatus.DONE, 4,30, "14:00 23.07.2024"));
        //Генерация записей истории, путем вызова по ИД, как было сделано в предыдущих ТЗ:
        manager.searchTaskById(1);
        System.out.println(manager.searchEpicById(3));
        manager.searchSubtaskById(5);
        System.out.println("=== Начинаю блок загрузки из файла ===");
        /*Этот блок посвящен загрузке из файла. Суть в том, чтобы увидеть новую задачу с ИД (крайняя задача+1).
         А также проверка работы загрузки из файла. */
        FileBackedTaskManager managerLoad = new FileBackedTaskManager(file);
        managerLoad = managerLoad.loadFromFile(file);
        managerLoad.createTask(new Task("Построить портал.", "Камень я дам.", TaskStatus.NEW, 30, "14:00 28.07.2024"));
        System.out.println("\n" + managerLoad.getAllTasks() + "\n" + managerLoad.getAllEpics() + "\n" + managerLoad.getAllSubtasks());
        System.out.println("=== Показываю историю ===");
        System.out.println(managerLoad.getHistory());

    }
}