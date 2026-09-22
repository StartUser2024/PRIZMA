package prizma.core.model;

public class Contour {
    private final String id;
    private final String name;
    private final String description;
    private final int level;

    public Contour(String id, String name, String description, int level) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.level = level;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getLevel() { return level; }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contour contour = (Contour) o;
        return id.equals(contour.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    // === СТАТИЧЕСКИЕ КОНСТАНТЫ (замена enum) ===
    public static final Contour L1 = new Contour("L1", "Рабочий контур",
            "Контур пользовательских рабочих станций", 1);
    public static final Contour L2 = new Contour("L2", "Магистральный контур",
            "Контур сетевого оборудования и магистральных каналов", 2);
    public static final Contour L3 = new Contour("L3", "Серверный контур",
            "Контур серверов приложений и баз данных", 3);
    public static final Contour L4 = new Contour("L4", "Граничный контур",
            "Контур межсетевых экранов и DMZ", 4);
    public static final Contour L5 = new Contour("L5", "Административный контур",
            "Контур серверов управления и мониторинга", 5);
}