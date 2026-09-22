package prizma.ui.panel.shapes;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import java.util.function.DoubleSupplier;

public class ContourShape {
    private final Group group;
    private final Rectangle rect;
    private final Color baseColor;
    private boolean isActive = false;
    private final DragContext dragContext = new DragContext();
    private final DoubleSupplier zoomProvider;

    public ContourShape(double x, double y, double width, double height, String name, Color color, DoubleSupplier zoomProvider) {
        this.baseColor = color;
        this.zoomProvider = zoomProvider;

        rect = new Rectangle(0, 0, width, height);
        rect.setFill(color.deriveColor(0, 1, 1, 0.3));
        rect.setStroke(color);
        rect.setStrokeWidth(2);
        rect.setCursor(Cursor.HAND);

        Label label = new Label(name);
        label.setLayoutX(5);
        label.setLayoutY(5);
        label.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " +
                toHexString(color.darker()) + "; -fx-background-color: transparent;");

        group = new Group(rect, label);
        group.setLayoutX(x);
        group.setLayoutY(y);

        rect.setOnMouseClicked(e -> {
            e.consume();
            setActive(true);
        });

        group.setOnMousePressed(e -> {
            dragContext.mouseAnchorX = e.getSceneX();
            dragContext.mouseAnchorY = e.getSceneY();
            dragContext.initialLayoutX = group.getLayoutX();
            dragContext.initialLayoutY = group.getLayoutY();
            e.consume();
        });

        group.setOnMouseDragged(e -> {
            double zoom = zoomProvider.getAsDouble();
            double deltaX = (e.getSceneX() - dragContext.mouseAnchorX) / zoom;
            double deltaY = (e.getSceneY() - dragContext.mouseAnchorY) / zoom;
            group.setLayoutX(dragContext.initialLayoutX + deltaX);
            group.setLayoutY(dragContext.initialLayoutY + deltaY);
            e.consume();
        });
    }

    public Group getGroup() { return group; }
    public void setActive(boolean active) {
        this.isActive = active;
        rect.setStrokeWidth(active ? 3 : 2);
        rect.setStroke(active ? baseColor.darker() : baseColor);
    }
    public boolean isActive() { return isActive; }

    private static String toHexString(Color color) {
        return String.format("#%02X%02X%02X", (int)(color.getRed()*255), (int)(color.getGreen()*255), (int)(color.getBlue()*255));
    }

    private static class DragContext { double mouseAnchorX, mouseAnchorY, initialLayoutX, initialLayoutY; }
}