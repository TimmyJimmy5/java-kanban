import java.util.*;

public class Task {
    private final String name;
    private final String description;
    private TaskStatus status;
    private int id;

    public Task(String name, String description, TaskStatus status) {
        this.description = description;
        this.name = name;
        this.status = status;
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
        return id == task.id && Objects.equals(description, task.description) && Objects.equals(name, task.name)
                && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, id, name, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", ID=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}