package commands;

/**
 * Created by Evan on 1/17/2016.
 */
public abstract class Command {
    private String cmdType;
    private int workloadId;

    public abstract String getCmdType();

    public int getWorkloadId() {
        return workloadId;
    }

    public void setWorkloadId(int workloadId) {
        this.workloadId = workloadId;
    }
}
