package domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Epic extends Task {
    private List<Subtask> subtasksOfEpic;

    private LocalDateTime endTime;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subtasksOfEpic = new ArrayList<>();
        setType(Type.EPIC);

    }
    public void setSubtasksOfEpic(Subtask subtask) {
        subtasksOfEpic.add(subtask);
        if(subtask.getStartTime()==null){
            return;
        }
        calculationTimeDotsOfEpic();
    }

    public void clearSubtasksOfEpic() {
        subtasksOfEpic.clear();
    }

    public List<Subtask> getSubtasksOfEpic() {
        return new ArrayList<>(subtasksOfEpic);
    }

    public void removeSubtask(Subtask subtask) {
        subtasksOfEpic.remove(subtask);
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    private void calculationTimeDotsOfEpic(){
        subtasksOfEpic.sort((task1,task2)->task1.getStartTime().getMinute()-task2.getEndTime().getMinute());
        setStartTime(subtasksOfEpic.get(subtasksOfEpic.size()-1).getStartTime());
        setEndTime(subtasksOfEpic.get(0).getEndTime());
        setDuration(Duration.between(getStartTime(),getEndTime()));

    }

    @Override
    public String toString() {
        return super.toString() + "\b, " +
                "subtasksOfEpic.size=" + subtasksOfEpic.size() +
                '}';
    }
}
