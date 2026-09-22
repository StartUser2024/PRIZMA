package prizma.ui.panel.components;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import prizma.core.model.ComponentInstance;
import prizma.core.model.StandardComponentProfile;
import prizma.ui.panel.InteractionMode;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class ComponentView {
    private static final double COMPONENT_WIDTH = 94;  // Увеличили для комфорта
    private static final double COMPONENT_HEIGHT = 102; // Увеличили для комфорта

    private final Group group;
    private final ComponentInstance instance;
    private final DragContext dragContext = new DragContext();
    private final Rectangle hitArea;

    private final DoubleSupplier zoomProvider;
    private final Supplier<InteractionMode> modeProvider;
    // Изменили типы коллбэков, чтобы передавать Group
    private final BiConsumer<ComponentInstance, Group> onHoverEnter;
    private final Consumer<Group> onHoverExit;
    private final Consumer<ComponentInstance> onComponentMoved;

    // Переименовали параметр 'id' в 'profileId' для ясности
    public ComponentView(double x, double y, String name, String type, String profileId,
                         DoubleSupplier zoomProvider, Supplier<InteractionMode> modeProvider,
                         BiConsumer<ComponentInstance, Group> onHoverEnter,
                         Consumer<Group> onHoverExit, Consumer<ComponentInstance> onComponentMoved) {
        this.zoomProvider = zoomProvider;
        this.modeProvider = modeProvider;
        this.onHoverEnter = onHoverEnter;
        this.onHoverExit = onHoverExit;
        this.onComponentMoved = onComponentMoved;

        var profile = new StandardComponentProfile(profileId, null, null, name, type, "", "Не указан", "");
        this.instance = new ComponentInstance(profile);
        this.instance.setWidth(COMPONENT_WIDTH);
        this.instance.setHeight(COMPONENT_HEIGHT);

        this.group = new Group();
        this.hitArea = new Rectangle(COMPONENT_WIDTH, COMPONENT_HEIGHT);
        hitArea.setFill(Color.TRANSPARENT);
        hitArea.setStroke(Color.TRANSPARENT);
        hitArea.setCursor(Cursor.MOVE);

        // Рассчитываем вертикальное центрирование
        // Примерная высота надписи для 3-4 строк: ~40 пикселей
        double estimatedLabelHeight = 40;
        double totalContentHeight = 48 + 4 + estimatedLabelHeight; // иконка + отступ + надпись
        double startY = (COMPONENT_HEIGHT - totalContentHeight) / 2; // Центрируем по вертикали

        ImageView iconView = getImageViewForType(type);
        if (iconView != null) {
            iconView.setLayoutX((COMPONENT_WIDTH - 48) / 2); // Центрирование по X
            iconView.setLayoutY(startY); // Центрирование по Y
            iconView.setMouseTransparent(true);
        }

        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 10));
        nameLabel.setStyle("-fx-text-fill: #212121; -fx-background-color: transparent; -fx-alignment: center;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(COMPONENT_WIDTH);
        nameLabel.setPrefWidth(COMPONENT_WIDTH); // Фиксируем ширину для центрирования
        nameLabel.setLayoutX(0);
        nameLabel.setLayoutY(startY + 48 + 4); // Под иконкой с отступом 4
        nameLabel.setMouseTransparent(true);

        if (iconView != null) group.getChildren().addAll(hitArea, iconView, nameLabel);
        else group.getChildren().addAll(hitArea, nameLabel);

        group.setLayoutX(x - COMPONENT_WIDTH / 2);
        group.setLayoutY(y - COMPONENT_HEIGHT / 2);
        instance.setPosition(group.getLayoutX(), group.getLayoutY());
        onComponentMoved.accept(instance);

        setupMouseHandlers();
    }

    private void setupMouseHandlers() {
        group.setOnMouseEntered(e -> {
            if (modeProvider.get() == InteractionMode.CREATE_CONNECTION) {
                hitArea.setCursor(Cursor.CROSSHAIR);
                onHoverEnter.accept(instance, group); // Передаём Group!
            } else {
                hitArea.setCursor(Cursor.MOVE);
            }
        });

        group.setOnMouseExited(e -> {
            hitArea.setCursor(Cursor.MOVE);
            if (modeProvider.get() == InteractionMode.CREATE_CONNECTION) {
                onHoverExit.accept(group); // Передаём Group!
            }
        });

        group.setOnMousePressed(e -> {
            if (modeProvider.get() == InteractionMode.NORMAL) {
                dragContext.mouseAnchorX = e.getSceneX();
                dragContext.mouseAnchorY = e.getSceneY();
                dragContext.initialLayoutX = group.getLayoutX();
                dragContext.initialLayoutY = group.getLayoutY();
            }
            e.consume();
        });

        group.setOnMouseDragged(e -> {
            if (modeProvider.get() == InteractionMode.NORMAL) {
                double zoom = zoomProvider.getAsDouble();
                double deltaX = (e.getSceneX() - dragContext.mouseAnchorX) / zoom;
                double deltaY = (e.getSceneY() - dragContext.mouseAnchorY) / zoom;
                group.setLayoutX(dragContext.initialLayoutX + deltaX);
                group.setLayoutY(dragContext.initialLayoutY + deltaY);
                instance.setPosition(group.getLayoutX(), group.getLayoutY());

                // TODO: Здесь нужно вызвать коллбэк для обновления связей
                onComponentMoved.accept(instance);
            }
            e.consume();
        });
    }

    private ImageView getImageViewForType(String type) {
        String imagePath;
        if (type.contains("СЗИ") || type.contains("Межсетевой")) imagePath = "/icons/icon_firewall.png";
        else if (type.contains("Сервер") || type.contains("СУБД")) imagePath = "/icons/icon_server.png";
        else if (type.contains("Сетевое") || type.contains("Маршрутизатор")) imagePath = "/icons/icon_network.png";
        else if (type.contains("АРМ") || type.contains("Ноутбук")) imagePath = "/icons/icon_workstation.png";
        else imagePath = "/icons/icon_default.png";

        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(48);
            imageView.setFitHeight(48);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            return imageView;
        } catch (Exception e) {
            System.err.println("❌ Не удалось загрузить иконку: " + imagePath);
            return null;
        }
    }

    public Group getGroup() { return group; }
    public ComponentInstance getInstance() { return instance; }

    private static class DragContext {
        double mouseAnchorX, mouseAnchorY, initialLayoutX, initialLayoutY;
    }
}