package model;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(int id) {
        if (subtaskIds.contains(id) || id == getId()) {
            System.out.println("Нельзя назначить самого себя в виде подзадачи.");
        } else {
            subtaskIds.add(id);
        }
    }

    public void removeSubtasksIds() {
        subtaskIds.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }

    @Override
    public String toString() {
        return "model.Epic{" +
                "subtaskIDs=" + subtaskIds +
                ", name='" + getName() + '\'' +
                ", ID=" + getId() +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}