package domain;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private List<Subtask> subtasksOfEpic;

    public Epic(String name, String description, Status status) {
        super(name, description, status);
        subtasksOfEpic = new ArrayList<>();
        setType(Type.EPIC);
    }


    public void setSubtasksOfEpic(Subtask subtask) {
        subtasksOfEpic.add(subtask);
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
    public String toString() {
        return super.toString() + "\b, " +
                "subtasksOfEpic.size=" + subtasksOfEpic.size() +
                '}';
    }
}
