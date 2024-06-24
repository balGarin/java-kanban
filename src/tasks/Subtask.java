package tasks;

public class Subtask extends Task {


    private int idOfEpic;

    public Subtask(String name, String description, Status status, int idOfEpic) {
        super(name, description, status);
        this.idOfEpic = idOfEpic;
        setType("tasks.Subtask");
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
