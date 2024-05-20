import java.util.*;

public class Main {
    private static boolean isOperational = true;
    public static Scanner scanner = new Scanner(System.in);
    public static TaskManager taskManager = new TaskManager();

    public static void main(String[] args) {
        while (isOperational) {
            printMenu();
        }
    }

    public static void printMenu() {
        System.out.println("Выберите пункт меню:");
        System.out.println("1. Создать задачу");
        System.out.println("2. Создать подзадачу");
        System.out.println("3. Посмотреть список задач.");
        System.out.println("4. Поиск задачи по идентификатору.");
        System.out.println("5. Изменить задачу");
        System.out.println("6. Изменить статус задачи");
        System.out.println("7. Удалить задачу по идентификатору.");
        System.out.println("8. Очистить список задач.");
        System.out.println("9. Завершить работу.");
        System.out.println("10. Тестовое наполнение 4 задачами.");
        int commandValue = scanner.nextInt();

        switch (commandValue) {
            case 1 :
                taskManager.createTask();
                break;
            case 2 :
                taskManager.addingSubtaskToEpic();
                break;
            case 3 :
                taskManager.getAllTasks();
                break;
            case 4 :
                taskManager.searchById();
                break;
            case 5 :
                taskManager.changeTaskContent();
                break;
            case 6 :
                taskManager.changeStatus();
                break;
            case 7 :
                taskManager.clearById();
                break;
            case 8 :
                taskManager.clearAll();
                break;
            case 9 :
                System.out.println("Останавливаюсь.");
                isOperational = false;
                break;
            case 10 :
                taskManager.test();
                break;
            default :
                System.out.println("Такого пункта меню еще нет.");
                break;
        }
    }
}
