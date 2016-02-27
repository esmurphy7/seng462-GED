package teamgid.deploy462;

/**
 * Created by DanielF on 2016-02-24.
 */
public class Deployment {
    private final ServerType type;
    private String serverLocation;

    public Deployment(ServerType type) {
        this.type = type;
    }

    public void addServerLocation(int locationId) {
        switch (type) {
            case WORKLOAD_GENERATOR_SERVER:
                serverLocation = StaticConstants.WORKLOAD_GENERATOR_SERVERS[locationId];
                break;
            case TRANSACTION_SERVER:
                serverLocation = StaticConstants.TX_SERVERS[locationId];
                break;
            case AUDIT_SERVER:
                serverLocation = StaticConstants.AUDIT_SERVERS[locationId];
                break;
            case WEB_SERVER:
                serverLocation = StaticConstants.WEB_SERVERS[locationId];
                break;
            default:
                serverLocation = "";
                break;
        }
    }

    public ServerType getServerType() {
        return type;
    }

    public String getServerLocation() {
        return serverLocation;
    }
}
