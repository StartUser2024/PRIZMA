package prizma.ui.panel.connections;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import prizma.core.model.ComponentInstance;
import prizma.core.model.Connection;
import prizma.core.model.ConnectionPoint;

import java.util.List;

public class ConnectionView {
    private final Connection model;
    private final ComponentInstance sourceComponent;
    private final ComponentInstance targetComponent;
    private final int sourcePointIndex;
    private final int targetPointIndex;

    private final Polyline visibleLine; // Тонкая видимая линия
    private final Polyline hitboxLine;  // Толстая невидимая линия для перехвата кликов
    private final Group visualGroup;
    private final ContextMenu contextMenu;
    private boolean isHighlighted = false;

    public ConnectionView(Connection connection,
                          ComponentInstance sourceComponent,
                          ComponentInstance targetComponent,
                          int sourcePointIndex,
                          int targetPointIndex,
                          Runnable onDelete,
                          Runnable onConfigure) {
        this.model = connection;
        this.sourceComponent = sourceComponent;
        this.targetComponent = targetComponent;
        this.sourcePointIndex = sourcePointIndex;
        this.targetPointIndex = targetPointIndex;

        // 1. Видимая линия
        this.visibleLine = new Polyline();
        visibleLine.setStroke(Color.web("#7b1fa2"));
        visibleLine.setStrokeWidth(2.0);
        visibleLine.setFill(null);

        // 2. Hitbox (добавляется в Group ПОСЛЕ видимой линии, чтобы быть сверху, но прозрачный)
        this.hitboxLine = new Polyline();
        hitboxLine.setStroke(Color.TRANSPARENT);
        hitboxLine.setStrokeWidth(14.0); // Широкая зона для легкого попадания
        hitboxLine.setFill(null);

        // 3. Контекстное меню
        this.contextMenu = new ContextMenu();
        MenuItem configureItem = new MenuItem("⚙️ Настроить связь...");
        configureItem.setOnAction(e -> {
            if (onConfigure != null) onConfigure.run();
            contextMenu.hide();
        });
        MenuItem deleteItem = new MenuItem("🗑️ Удалить связь");
        deleteItem.setStyle("-fx-text-fill: red;");
        deleteItem.setOnAction(e -> {
            if (onDelete != null) onDelete.run();
            contextMenu.hide();
        });
        contextMenu.getItems().addAll(configureItem, new SeparatorMenuItem(), deleteItem);

        // 4. Привязка событий (Вешаем на ОБЕ линии, чтобы срабатывало при идеальном и неточном попадании)
        setupMouseEvents(visibleLine);
        setupMouseEvents(hitboxLine);

        // hitboxLine добавлен последним, он визуально "сверху", но прозрачен
        this.visualGroup = new Group(visibleLine, hitboxLine);
        update();
    }

    private void setupMouseEvents(Polyline line) {
        line.setOnContextMenuRequested(e -> {
            contextMenu.show(line, e.getScreenX(), e.getScreenY());
            highlight(true);
            e.consume();
        });
        line.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                contextMenu.hide();
                highlight(false);
            }
        });
        line.setOnMouseEntered(e -> highlight(true));
        line.setOnMouseExited(e -> {
            if (!contextMenu.isShowing()) highlight(false);
        });
    }

    public void update() {
        List<ConnectionPoint> sourcePoints = sourceComponent.getConnectionPoints();
        List<ConnectionPoint> targetPoints = targetComponent.getConnectionPoints();
        if (sourcePointIndex >= sourcePoints.size() || targetPointIndex >= targetPoints.size()) return;

        Point2D sourcePos = sourcePoints.get(sourcePointIndex).getAbsolutePosition();
        Point2D targetPos = targetPoints.get(targetPointIndex).getAbsolutePosition();

        // Простой ортогональный маршрут (Г-образный)
        List<Double> points = List.of(
                sourcePos.getX(), sourcePos.getY(),
                targetPos.getX(), sourcePos.getY(),
                targetPos.getX(), targetPos.getY()
        );

        // Обновляем ОБЕ линии синхронно
        visibleLine.getPoints().setAll(points);
        hitboxLine.getPoints().setAll(points);
    }

    private void highlight(boolean isActive) {
        this.isHighlighted = isActive;
        if (isActive) {
            visibleLine.setStroke(Color.web("#ab47bc")); // Ярче при наведении
            visibleLine.setStrokeWidth(3.0);
        } else {
            visibleLine.setStroke(Color.web("#7b1fa2")); // Стандартный цвет
            visibleLine.setStrokeWidth(2.0);
        }
    }

    public Group getVisualGroup() { return visualGroup; }
    public Connection getModel() { return model; }
}