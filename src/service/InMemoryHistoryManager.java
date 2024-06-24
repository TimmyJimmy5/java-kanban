package service;

import model.Task;
import model.Node;
import model.TasksLinkedList;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node<Task>> linkNodesToTaskIds = new HashMap<>();
    private TasksLinkedList history = new TasksLinkedList();

    public InMemoryHistoryManager() {
        linkNodesToTaskIds = new HashMap<>();
    }

    @Override
    public void addTask(Task task) {
        if (task != null) {
            removeTask(task.getId());
            linkNodesToTaskIds.put(task.getId(), history.addLast(task));
        }
    }

    @Override
    public  List<Task> getHistoryOfTasks() {
        return history.getTasks();
    }

    @Override
    public void removeTask(int id) {
        if (linkNodesToTaskIds.containsKey(id)) {
            history.removeNode(linkNodesToTaskIds.get(id));
            linkNodesToTaskIds.remove(id);
        }
    }
}