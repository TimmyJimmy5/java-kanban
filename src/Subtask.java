import java.util.*;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, TaskStatus status, int epicID) {
        super(name, description, status);
        this.epicId = epicID;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicID=" + getEpicId() +
                ", name='" + getName() + '\'' +
                ", ID=" + getId() +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}