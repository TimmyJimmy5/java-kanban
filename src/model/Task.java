package model;

import service.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task implements Comparable<Task> {
    private final String name;
    private final String description;
    private int id;
    private TaskStatus status;
    TaskType taskType;
    private Duration duration;
    private LocalDateTime startTime;

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public TaskType getTaskType() {
        return taskType;
    }

    public Task(String name, String description, TaskStatus status) {
        this.description = description;
        this.name = name;
        this.status = status;
        this.taskType = TaskType.TASK;
        this.startTime = InMemoryTaskManager.getDEFAULT_DATE_TIME();
        this.duration = Duration.ZERO;
    }

    public Task(String name, String description, TaskStatus status, int durationMins, String startDate) {
        this.description = description;
        this.name = name;
        this.status = status;
        this.taskType = TaskType.TASK;
        this.duration = Duration.ofMinutes(durationMins);
        this.startTime = LocalDateTime.parse(startDate, DATE_TIME_FORMATTER);
    }

    public LocalDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description)
                && status == task.status && taskType == task.taskType
                && Objects.equals(startTime, task.startTime) && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, id, name, status);
    }

    @Override
    public String toString() {
        return "model.Task{" +
                "name='" + name + '\'' +
                ", ID=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}' + "\n";
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public int compareTo(Task o) {
        int value = -1;
        if (o.getId() == (this.getId())) {
            value = 0;
        } else if ((o.getStartTime().isBefore(this.getStartTime())) || o.getStartTime().isEqual(this.getStartTime())) {
            value = 1;
        } else {
            value = -1;
        }
        return value;
    }
}