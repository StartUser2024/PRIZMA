package prizma.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LogicalModel {
    private final List<ComponentInstance> components = new ArrayList<>();
    private final List<Connection> connections = new ArrayList<>();

    public void addComponent(ComponentInstance component) {
        components.add(component);
    }

    public void removeComponent(String componentId) {
        components.removeIf(c -> c.getId().equals(componentId));
        // Удаляем все связи, связанные с этим компонентом
        connections.removeIf(c ->
                c.getSourceId().equals(componentId) || c.getTargetId().equals(componentId)
        );
    }

    public void addConnection(Connection connection) {
        connections.add(connection);
    }

    public void removeConnection(String connectionId) {
        connections.removeIf(c -> c.getId().equals(connectionId));
    }

    public List<ComponentInstance> getComponents() {
        return new ArrayList<>(components);
    }

    public List<Connection> getConnections() {
        return new ArrayList<>(connections);
    }

    public Optional<ComponentInstance> findComponentById(String instanceId) {
        return components.stream()
                .filter(c -> c.getInstanceId().equals(instanceId))
                .findFirst();
    }

    public Optional<Connection> findConnectionById(String id) {
        return connections.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    public void clear() {
        components.clear();
        connections.clear();
    }

    public int getComponentCount() {
        return components.size();
    }

    public int getConnectionCount() {
        return connections.size();
    }
}