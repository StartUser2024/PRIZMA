package prizma.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ComponentInstance {
    private final String instanceId; // Уникальный ID экземпляра на холсте
    private final ComponentProfile profile; // Профиль (содержит ID из каталога, тип, вид и т.д.)
    private Contour contour;
    private Segment segment;

    private double x;
    private double y;
    private double width = 120;
    private double height = 70;
    private final List<ConnectionPoint> connectionPoints = new ArrayList<>();

    // Карта счётчиков для каждого вида компонента
    // Ключ: очищенное имя вида (например, "DallasLock80")
    // Значение: количество созданных экземпляров этого вида
    private static final Map<String, Integer> typeCounters = new HashMap<>();

    // Конструктор больше не требует внешний ID. Он генерирует его сам.
    public ComponentInstance(ComponentProfile profile) {
        // Получаем имя вида из профиля
        String typeName = profile.getName();

        // Используем оригинальное имя вида (с пробелами и точками) для человекочитаемости
        // Если имя пустое, используем заглушку
        if (typeName.isEmpty()) {
            typeName = "Component";
        }

        // Получаем текущий счётчик для этого вида (или 0, если это первый экземпляр)
        int count = typeCounters.getOrDefault(typeName, 0) + 1;

        // Сохраняем обновлённый счётчик
        typeCounters.put(typeName, count);

        // Формируем уникальный ID в формате "ИмяВида_Номер" (например, "КриптоПро CSP 5.0_1")
        this.instanceId = typeName + "_" + count;

        this.profile = profile;
        this.contour = null;
        this.segment = null;
        this.x = 0;
        this.y = 0;
        initializeConnectionPoints();
    }

    // Геттер для уникального ID экземпляра
    public String getInstanceId() { return instanceId; }

    // Для совместимости (если где-то еще вызывается getId)
    public String getId() { return instanceId; }

    public ComponentProfile getProfile() { return profile; }

    public Contour getContour() { return contour; }
    public void setContour(Contour contour) { this.contour = contour; }

    public Segment getSegment() { return segment; }
    public void setSegment(Segment segment) { this.segment = segment; }

    @Override
    public String toString() {
        // Теперь в выводе будет видно и имя, и уникальный ID экземпляра
        return profile.getName() + " [" + instanceId + "]";
    }

    public double getX() { return x; }
    public void setX(double x) { this.x = x; }

    public double getY() { return y; }
    public void setY(double y) { this.y = y; }

    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public List<ConnectionPoint> getConnectionPoints() { return connectionPoints; }

    private void initializeConnectionPoints() {
        connectionPoints.add(new ConnectionPoint(0, this, -0.3, -0.8));
        connectionPoints.add(new ConnectionPoint(1, this, 0.3, -0.8));
        connectionPoints.add(new ConnectionPoint(2, this, 0.8, -0.3));
        connectionPoints.add(new ConnectionPoint(3, this, 0.8, 0.3));
        connectionPoints.add(new ConnectionPoint(4, this, 0.3, 0.8));
        connectionPoints.add(new ConnectionPoint(5, this, -0.3, 0.8));
        connectionPoints.add(new ConnectionPoint(6, this, -0.8, 0.3));
        connectionPoints.add(new ConnectionPoint(7, this, -0.8, -0.3));
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}