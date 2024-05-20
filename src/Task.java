public class Task {
    protected String taskName;
    protected String taskDescription;
    protected TaskStatus taskStatus;
    protected int taskId;

    public Task(int taskId, String taskName, String taskDescription, TaskStatus taskStatus){
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.taskStatus = taskStatus;
        this.taskId = taskId;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getTaskName() {
        return taskName;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }


    public int getTaskId() {
        return taskId;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskStatus=" + taskStatus +
                ", taskId=" + taskId +
                '}';
    }
}
