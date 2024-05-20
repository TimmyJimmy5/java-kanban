import java.util.*;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.createTask(new Task("Покормить кота.", "Корм находится на верхней полке", TaskStatus.NEW));
        taskManager.createTask(new Task("Покормить собаку.", "Корм находится на средней полке", TaskStatus.NEW));
        taskManager.createTask(new Task("Покормить домашнего медведя.", "Корм находится на нижней полке", TaskStatus.NEW));

        taskManager.createEpic(new Epic("Построить зиккурат.", "Жизнь за Нер'Зула", TaskStatus.NEW));
        taskManager.createEpic(new Epic("Построить казарму.", "Здесь нельзя строить", TaskStatus.NEW));
        //3 подзадачи для эпика 3 (зиккурат)
        taskManager.createSubtask(new Subtask("Нужно больше золота.", "Построить рудники.", TaskStatus.NEW, 3));
        taskManager.createSubtask(new Subtask("Нужно больше дерева.", "Рубить лес.", TaskStatus.IN_PROGRESS, 3));
        taskManager.createSubtask(new Subtask("Нужно больше еды.", "Печем булки!.", TaskStatus.DONE, 3));
        //2 подзадачи для эпика 4 (казарма)
        taskManager.createSubtask(new Subtask("Нужно расчистить площадку", "Вырубим лес.", TaskStatus.DONE, 4));
        taskManager.createSubtask(new Subtask("Нужно немного подумать", "Но это не точно", TaskStatus.DONE, 4));




        System.out.println("Список Эпиков изначальный:");
        ArrayList<Epic> epicsList = (ArrayList<Epic>) taskManager.getAllEpics();
        System.out.println(epicsList);
        System.out.println(" ");

        System.out.println("Список Задач изначальный:");
        ArrayList<Task> taskList = (ArrayList<Task>) taskManager.getAllTasks();
        System.out.println(taskList);
        System.out.println(" ");

        System.out.println("Список Подзадач изначальный:");
        ArrayList<Subtask> subtasksList = (ArrayList<Subtask>) taskManager.getAllSubtasks();
        System.out.println(subtasksList);
        System.out.println(" ");

        taskManager.changeTaskOrSubtaskStatus(8, TaskStatus.NEW); //статус эпика с казармой изменится на В ПРОЦЕССЕ
        taskManager.changeTaskOrSubtaskStatus(0, TaskStatus.DONE); //статус таска с котом будет ЗАВЕРШЕН

        System.out.println("Список Эпиков с измененным статусом:");
        epicsList = (ArrayList<Epic>) taskManager.getAllEpics();
        System.out.println(epicsList);
        System.out.println(" ");

        System.out.println("Список Задач с измененным статусом:");
        taskList = (ArrayList<Task>) taskManager.getAllTasks();
        System.out.println(taskList);
        System.out.println(" ");

        System.out.println("Список Подзадач с измененным статусом:");
        subtasksList = (ArrayList<Subtask>) taskManager.getAllSubtasks();
        System.out.println(subtasksList);
        System.out.println(" ");

        taskManager.removeTaskById(0);
        taskManager.removeEpicById(4);
        taskManager.removeSubtaskById(5);

        System.out.println("Список Эпиков после удаления казармы:");
        epicsList = (ArrayList<Epic>) taskManager.getAllEpics();
        System.out.println(epicsList);
        System.out.println(" ");

        System.out.println("Список Задач после удаления задачи с котом:");
        taskList = (ArrayList<Task>) taskManager.getAllTasks();
        System.out.println(taskList);
        System.out.println(" ");

        System.out.println("Список Подзадач после удаления подзадачи Зиккурата на постройку рудников:");
        subtasksList = (ArrayList<Subtask>) taskManager.getAllSubtasks();
        System.out.println(subtasksList);
        System.out.println(" ");
    }

}