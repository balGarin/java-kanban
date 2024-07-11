package domain;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


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
        if (subtask.getStartTime() == null) {
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

    private void calculationTimeDotsOfEpic() {
        subtasksOfEpic.sort(Comparator.comparing(Task::getStartTime));
        setStartTime(subtasksOfEpic.get(0).getStartTime());
        setEndTime(subtasksOfEpic.get(subtasksOfEpic.size() - 1).getEndTime());
        long sumDuration = subtasksOfEpic.stream()
                .map(subtask -> subtask.getDuration().toMinutes())
                .mapToLong(Long::longValue).sum();
        setDuration(Duration.ofMinutes(sumDuration));
    }

    @Override
    public String toString() {
        return super.toString() + "\b, " +
                "subtasksOfEpic.size=" + subtasksOfEpic.size() +
                '}';
    }
}
