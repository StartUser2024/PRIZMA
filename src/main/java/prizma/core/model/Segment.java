package prizma.core.model;

public class Segment {
    private final String id;
    private final String name;
    private final String description;
    private final String regulatoryFramework;

    public Segment(String id, String name, String description, String regulatoryFramework) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.regulatoryFramework = regulatoryFramework;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getRegulatoryFramework() { return regulatoryFramework; }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Segment segment = (Segment) o;
        return id.equals(segment.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    // === СТАТИЧЕСКИЕ КОНСТАНТЫ (замена enum) ===
    public static final Segment PDN = new Segment("PDN", "Персональные данные",
            "Сегмент обработки персональных данных", "Приказ ФСТЭК №21");
    public static final Segment GIS = new Segment("GIS", "Государственная ИС",
            "Сегмент государственной информационной системы", "Приказ ФСТЭК №17");
    public static final Segment PUBLIC = new Segment("PUBLIC", "Открытый доступ",
            "Сегмент с открытым доступом", "Базовые требования");
    public static final Segment ADMIN = new Segment("ADMIN", "Административный",
            "Административный сегмент", "Внутренние политики");
    public static final Segment UNDEFINED = new Segment("UNDEFINED", "Не определён",
            "Сегмент не назначен", "—");
}