package prizma.ui.panel.connections;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import prizma.core.model.ComponentInstance;
import prizma.core.model.ConnectionPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ConnectionPointManager {
    private final List<ConnectionPointView> views = new ArrayList<>();
    private ComponentInstance hoveredComponent = null;
    private Group currentComponentGroup = null; // Запоминаем группу, чтобы потом очистить

    public void showPoints(ComponentInstance instance, Group componentGroup, Consumer<ConnectionPoint> onPointClicked) {
        // === ВОТ ЭТО СПАСЁТ ОТ МЕРЦАНИЯ ===
        if (this.hoveredComponent == instance) {
            return; // Точки уже показаны для этого компонента, не пересоздаём их!
        }

        hidePoints(); // Сначала скрываем предыдущие
        hoveredComponent = instance;
        currentComponentGroup = componentGroup;

        double groupX = componentGroup.getLayoutX();
        double groupY = componentGroup.getLayoutY();

        for (ConnectionPoint point : instance.getConnectionPoints()) {
            ConnectionPointView view = new ConnectionPointView(point);

            // Пересчитываем координаты относительно компонента, а не всего холста
            Point2D absPos = point.getAbsolutePosition();
            view.getCircle().setCenterX(absPos.getX() - groupX);
            view.getCircle().setCenterY(absPos.getY() - groupY);

            view.getCircle().setOnMouseEntered(e -> {
                view.highlight(true);
                e.consume();
            });
            view.getCircle().setOnMouseExited(e -> {
                view.highlight(false);
                e.consume();
            });
            view.getCircle().setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY) {
                    onPointClicked.accept(point);
                }
                e.consume();
            });

            // ВАЖНО: Добавляем ВНУТРЬ componentGroup, а не на canvas!
            componentGroup.getChildren().add(view.getCircle());
            views.add(view);
        }
    }

    public void hidePoints() {
        if (currentComponentGroup != null) {
            for (ConnectionPointView view : views) {
                currentComponentGroup.getChildren().remove(view.getCircle());
            }
            views.clear();
            currentComponentGroup = null;
            hoveredComponent = null;
        }
    }

    public void updatePointsPosition() {
        // Если точки сейчас отображаются, обновляем их позиции
        if (currentComponentGroup != null && hoveredComponent != null) {
            double groupX = currentComponentGroup.getLayoutX();
            double groupY = currentComponentGroup.getLayoutY();
            for (ConnectionPointView view : views) {
                Point2D absPos = view.getModel().getAbsolutePosition();
                view.getCircle().setCenterX(absPos.getX() - groupX);
                view.getCircle().setCenterY(absPos.getY() - groupY);
            }
        }
    }

    public ComponentInstance getHoveredComponent() {
        return hoveredComponent;
    }
}