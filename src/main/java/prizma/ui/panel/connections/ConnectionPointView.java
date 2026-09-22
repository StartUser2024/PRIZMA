package prizma.ui.panel.connections;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import prizma.core.model.ConnectionPoint;

/**
 * Визуальное представление точки подключения (маленький кружок).
 * Появляется при наведении мыши на компонент.
 */
public class ConnectionPointView {
    private final Circle circle;
    private final ConnectionPoint model; // Ссылка на модель точки

    public ConnectionPointView(ConnectionPoint model) {
        this.model = model;

        // Создаём кружок радиусом 5 пикселей
        this.circle = new Circle(5);
        this.circle.setFill(Color.WHITE);
        this.circle.setStroke(Color.web("#ab47bc")); // Фиолетовая рамка
        this.circle.setStrokeWidth(1.5);
        this.circle.setCursor(Cursor.CROSSHAIR);

        // Обновляем позицию кружка
        updatePosition();
    }

    /**
     * Обновляет позицию кружка на основе абсолютных координат точки.
     * Вызывается при перемещении компонента.
     */
    public void updatePosition() {
        Point2D pos = model.getAbsolutePosition();
        circle.setCenterX(pos.getX());
        circle.setCenterY(pos.getY());
    }

    public Circle getCircle() {
        return circle;
    }

    public ConnectionPoint getModel() {
        return model;
    }

    /**
     * Подсветка точки при наведении мыши.
     */
    public void highlight(boolean highlighted) {
        if (highlighted) {
            circle.setFill(Color.web("#e1bee7")); // Светло-фиолетовый
            circle.setRadius(6); // Чуть больше
        } else {
            circle.setFill(Color.WHITE);
            circle.setRadius(5);
        }
    }
}