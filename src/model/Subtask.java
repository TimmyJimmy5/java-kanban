package model;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicID) {
        super(name, description, status);
        this.epicId = epicID;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, TaskStatus status, int epicID, int durationMins, String startDate) {
        super(name, description, status, durationMins, startDate);
        this.epicId = epicID;
        this.taskType = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int id) {
        if (id == getId()) {
            System.out.println("Нельзя назначить себя в качестве эпика.");
        } else {
            this.epicId = id;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "model.Subtask{" +
                "epicID=" + getEpicId() +
                ", name='" + getName() + '\'' +
                ", ID=" + getId() +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                '}' + "\n";
    }
}