public class Epic extends Task{

    Epic(int epicId, String epicName, String epicDescription, TaskStatus epicStatus){
        super(epicId, epicName, epicDescription, epicStatus);
    }


    @Override
    public String toString() {
        return "Epic{" +
                "taskName='" + taskName + '\'' +
                ", taskDescription='" + taskDescription + '\'' +
                ", taskStatus=" + taskStatus +
                ", taskId=" + taskId +
                '}';
    }
}
