public class Subtask extends Task{
    protected int relatedToEpic;

    Subtask(int subtaskId, String subtaskName, String subtaskDescription, TaskStatus subtaskStatus, int relatedToEpic){
        super(subtaskId, subtaskName, subtaskDescription, subtaskStatus);
        this.relatedToEpic = relatedToEpic;
    }

    public int getRelatedToEpic() {
        return relatedToEpic;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "taskId=" + taskId +
                ", taskStatus=" + taskStatus +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskName='" + taskName + '\'' +
                ", relatedToEpic=" + relatedToEpic +
                '}';
    }
}
