package prizma.core.model;

import javafx.geometry.Point2D;

/**
 * Точка подключения компонента для создания связей.
 * Каждый компонент имеет 8 точек: 2 сверху, 2 снизу, 2 слева, 2 справа.
 */
public class ConnectionPoint {
    private final int index; // Индекс точки (0-7)
    private final ComponentInstance owner; // Владелец (компонент)
    private final double relativeX; // Относительно центра компонента (-1 до 1)
    private final double relativeY;

    public ConnectionPoint(int index, ComponentInstance owner, double relativeX, double relativeY) {
        this.index = index;
        this.owner = owner;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
    }

    public int getIndex() { return index; }
    public ComponentInstance getOwner() { return owner; }

    /**
     * Вычисляет абсолютные координаты точки на холсте.
     * Формула: центр_компонента + (относительная_координата × половина_размера)
     */
    public Point2D getAbsolutePosition() {
        double centerX = owner.getX() + owner.getWidth() / 2;
        double centerY = owner.getY() + owner.getHeight() / 2;
        double absX = centerX + (relativeX * owner.getWidth() / 2);
        double absY = centerY + (relativeY * owner.getHeight() / 2);
        return new Point2D(absX, absY);
    }

    @Override
    public String toString() {
        return "Point[" + index + "] of " + owner.getId();
    }
}