package ga.asev.model;

public enum NotificationType {
    EPISODE_DOWNLOADED("Downloaded");

    private String message;

    NotificationType(String message) {
        this.message = message;
    }

    public String toString(UserSerial userSerial, int episode) {
        return message + ": " + userSerial.getName() + " - " + episode;
    }
}
