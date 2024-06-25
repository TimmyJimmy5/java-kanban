package model;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class TasksLinkedList {
    private Node<Task> head;
    private Node<Task> tail;


    private int size = 0;

    public Node<Task> addFirst(Task element) {
        final Node<Task> oldHead = head;
        final Node<Task> newNode = new Node<Task>(null, element, oldHead);
        head = newNode;
        if (oldHead == null) {
            tail = newNode;
            size++;
        } else {
            oldHead.prev = newNode;
            size++;
        }
        return newNode;
    }

    public Task getFirst() {
        final Node<Task> curHead = head;
        if (curHead == null)
            throw new NoSuchElementException();
        return head.data;
    }

    public List<Task> getTasks() {
        final List<Task> historyOfTasks = new ArrayList<>();
        if (head != null) {
            Node<Task> currentNode = head;
            for (; currentNode != null; currentNode = currentNode.next) {
                historyOfTasks.add(currentNode.data);
            }
        }
        return historyOfTasks;
    }

    public Node<Task> addLast(Task element) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<Task>(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
            size++;
        } else {
            oldTail.next = newNode;
            size++;
        }
        return newNode;
    }

    public void removeNode(Node<Task> node) {
        Node<Task> previousNode = node.prev;
        Node<Task> nextNode = node.next;
        if (size == 1) {
            head = null;
            tail = null;
            node.data = null;
        } else if (size > 1) {
            if (previousNode == null) {
                head = nextNode;
                nextNode.prev = null;
                node.next = null;
                node.data = null;
            } else if (nextNode == null) {
                tail = previousNode;
                previousNode.next = null;
                node.prev = null;
                node.data = null;
            } else {
                previousNode.next = nextNode;
                nextNode.prev = previousNode;
                node.next = null;
                node.prev = null;
                node.data = null;
            }
        }
        if (size != 0) {
            size--;
        }
    }

    public Task getLast() {
        final Node<Task> curLast = tail;
        if (curLast == null) {
            throw new NoSuchElementException();
        }
        return tail.data;
    }

    public int size() {
        return this.size;
    }
}
