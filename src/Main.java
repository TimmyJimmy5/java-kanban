import java.util.*;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();

        inMemoryTaskManager.createTask(new Task("Покормить кота.", "Корм находится на верхней полке", TaskStatus.NEW));
        inMemoryTaskManager.createTask(new Task("Покормить собаку.", "Корм находится на средней полке", TaskStatus.NEW));
        inMemoryTaskManager.createTask(new Task("Покормить домашнего медведя.", "Корм находится на нижней полке", TaskStatus.NEW));

        inMemoryTaskManager.createEpic(new Epic("Построить зиккурат.", "Жизнь за Нер'Зула", TaskStatus.NEW));
        inMemoryTaskManager.createEpic(new Epic("Построить казарму.", "Здесь нельзя строить", TaskStatus.NEW));
        //3 подзадачи для эпика 3 (зиккурат)
        inMemoryTaskManager.createSubtask(new Subtask("Нужно больше золота.", "Построить рудники.", TaskStatus.NEW, 3));
        inMemoryTaskManager.createSubtask(new Subtask("Нужно больше дерева.", "Рубить лес.", TaskStatus.IN_PROGRESS, 3));
        inMemoryTaskManager.createSubtask(new Subtask("Нужно больше еды.", "Печем булки!.", TaskStatus.DONE, 3));
        //2 подзадачи для эпика 4 (казарма)
        inMemoryTaskManager.createSubtask(new Subtask("Нужно расчистить площадку", "Вырубим лес.", TaskStatus.DONE, 4));
        inMemoryTaskManager.createSubtask(new Subtask("Нужно немного подумать", "Но это не точно", TaskStatus.DONE, 4));

        System.out.println("Список Эпиков изначальный:");
        for (Task task : inMemoryTaskManager.getAllEpics()) {
            System.out.println(task);
        }
        System.out.println(" ");

        System.out.println("Список Задач изначальный:");
        for (Task task : inMemoryTaskManager.getAllTasks()) {
            System.out.println(task);
        }
        System.out.println(" ");

        System.out.println("Список Подзадач изначальный:");
        for (Task task : inMemoryTaskManager.getAllSubtasks()) {
            System.out.println(task);
        }
        System.out.println(" ");

        System.out.println("Тут должны быть 12 задач");
        System.out.println(inMemoryTaskManager.searchTaskById(0));
        System.out.println(inMemoryTaskManager.searchEpicById(3));
        System.out.println(inMemoryTaskManager.searchTaskById(1));
        System.out.println(inMemoryTaskManager.searchEpicById(3));
        System.out.println(inMemoryTaskManager.searchTaskById(2));
        System.out.println(inMemoryTaskManager.searchEpicById(3));
        System.out.println(inMemoryTaskManager.searchTaskById(2));
        System.out.println(inMemoryTaskManager.searchEpicById(3));
        System.out.println(inMemoryTaskManager.searchTaskById(0));
        System.out.println(inMemoryTaskManager.searchEpicById(3));
        System.out.println(inMemoryTaskManager.searchTaskById(1));
        System.out.println(inMemoryTaskManager.searchEpicById(3));
        System.out.println(" ");
        System.out.println("Тут должна быть история (10 единиц):");
        inMemoryTaskManager.getHistory();
        System.out.println(" ");
    }

}