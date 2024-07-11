package domain;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int idOfEpic;

    public Subtask(String name, String description, Status status, int idOfEpic) {
        super(name, description, status);
        this.idOfEpic = idOfEpic;
        setType(Type.SUBTASK);
    }

    public Subtask(String name, String description, Status status, int idOfEpic, LocalDateTime startTime,
                   Duration duration) {
        super(name, description, status, startTime, duration);
        this.idOfEpic = idOfEpic;
        setType(Type.SUBTASK);
    }

    public int getIdOfEpic() {
        return idOfEpic;
    }

    public void setIdOfEpic(int idOfEpic) {
        this.idOfEpic = idOfEpic;
    }

    @Override
    public String toString() {
        return super.toString() + "\b, " +
                "idOfEpic=" + idOfEpic +
                '}';
    }
}
