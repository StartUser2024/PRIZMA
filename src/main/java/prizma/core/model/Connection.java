package prizma.core.model;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Connection {
    private final String id;
    private final String sourceId;
    private final String targetId;
    private String protocol;
    private boolean encrypted;
    private int sourcePointIndex; // Индекс точки подключения источника (0-7)
    private int targetPointIndex; // Индекс точки подключения цели (0-7)
    private List<Point2D> waypoints = new ArrayList<>(); // Промежуточные точки для ломаной

    public Connection(String id, String sourceId, String targetId) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.protocol = "TCP";
        this.encrypted = false;
        this.sourcePointIndex = -1; // По умолчанию не задан
        this.targetPointIndex = -1;
    }

    public String getId() { return id; }
    public String getSourceId() { return sourceId; }
    public String getTargetId() { return targetId; }
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

    public boolean isEncrypted() { return encrypted; }
    public void setEncrypted(boolean encrypted) { this.encrypted = encrypted; }

    @Override
    public String toString() {
        return sourceId + " → " + targetId + " (" + protocol + ")";
    }

    // Геттеры и сеттеры для точек подключения
    public int getSourcePointIndex() { return sourcePointIndex; }
    public void setSourcePointIndex(int index) { this.sourcePointIndex = index; }

    public int getTargetPointIndex() { return targetPointIndex; }
    public void setTargetPointIndex(int index) { this.targetPointIndex = index; }

    // Геттеры и сеттеры для waypoints
    public List<Point2D> getWaypoints() { return waypoints; }
    public void setWaypoints(List<Point2D> waypoints) { this.waypoints = waypoints; }
    public void addWaypoint(Point2D point) { this.waypoints.add(point); }
    public void clearWaypoints() { this.waypoints.clear(); }
}