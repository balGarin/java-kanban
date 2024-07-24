package domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class Epic extends Task {
    private List<Subtask> subtasksOfEpic;
    private int sizeOfSubtasks ;


    private LocalDateTime endTime;




    public Epic(String name, String description, Status status) {
        super(name, description, status);
        setType(Type.EPIC);
        subtasksOfEpic=new ArrayList<>();
    }

    public void setSubtasksOfEpic(Subtask subtask) {
        subtasksOfEpic.add(subtask);
        setSizeOfSubtasks();
        if (subtask.getStartTime() == null) {
            return;
        }
        calculationTimeDotsOfEpic();
    }

    public void clearSubtasksOfEpic() {
        subtasksOfEpic.clear();
        setSizeOfSubtasks();
    }

    public List<Subtask> getSubtasksOfEpic() {
        return new ArrayList<>(subtasksOfEpic);
    }

    public void removeSubtask(Subtask subtask) {
        subtasksOfEpic.remove(subtask);
        setSizeOfSubtasks();
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    private void calculationTimeDotsOfEpic() {
        List<Subtask>subtasksWithTime= subtasksOfEpic.stream()
                .filter(subtask -> subtask.getStartTime() != null)
                .sorted(Comparator.comparing(Task::getStartTime)).toList();
           setStartTime(subtasksWithTime.get(0).getStartTime());
           setEndTime(subtasksWithTime.get(subtasksWithTime.size() - 1).getEndTime());
           long sumDuration = subtasksWithTime.stream()
                   .map(subtask -> subtask.getDuration().toMinutes())
                   .mapToLong(Long::longValue).sum();
           setDuration(Duration.ofMinutes(sumDuration));
    }

    public void setSubtasksOfEpic(List<Subtask> subtasksOfEpic) {
        this.subtasksOfEpic = subtasksOfEpic;
    }

    private void setSizeOfSubtasks(){
        if(subtasksOfEpic==null){
            sizeOfSubtasks=0;
        }else{
            sizeOfSubtasks=subtasksOfEpic.size();
        }
    }

    public int getSizeOfSubtasks() {
        return sizeOfSubtasks;
    }

    @Override
    public String toString() {
        return "EPIC" +
                "{name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", id=" + getId() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", sizeOfSubtasks=" + getSubtasksOfEpic().size() +
                '}';
    }

}
