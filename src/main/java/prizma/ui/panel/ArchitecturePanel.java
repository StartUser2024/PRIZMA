package prizma.ui.panel;

import javafx.scene.control.ScrollPane;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.transform.Scale;
import prizma.core.model.ConnectionPoint;
import prizma.core.model.LogicalModel;
import prizma.ui.panel.components.ComponentView;
import prizma.ui.panel.connections.ConnectionPointManager;
import prizma.ui.panel.connections.ConnectionView;
import prizma.ui.panel.shapes.ContourShape;
import prizma.ui.panel.shapes.SegmentShape;
import java.util.ArrayList;
import java.util.List;

public class ArchitecturePanel extends ScrollPane {

    private static final double CANVAS_BASE_WIDTH = 2000;
    private static final double CANVAS_BASE_HEIGHT = 1500;

    private final Pane canvas;
    private final Pane zoomGroup;
    private final Scale scaleTransform;
    private double currentZoom = 1.0;

    private final LogicalModel logicalModel = new LogicalModel();
    private final List<ComponentView> componentViews = new ArrayList<>();
    private final List<ConnectionView> connectionViews = new ArrayList<>();
    private Polyline rubberBandLine = null;
    private final List<ContourShape> contours = new ArrayList<>();
    private final List<SegmentShape> segments = new ArrayList<>();

    private final ConnectionPointManager connectionPointManager = new ConnectionPointManager();
    private InteractionMode currentMode = InteractionMode.NORMAL;
    private ConnectionPoint pendingConnectionSource = null;

    private static final Color[] CONTOUR_COLORS = { Color.web("#e3f2fd"), Color.web("#fff3e0"), Color.web("#e8f5e9"), Color.web("#fce4ec"), Color.web("#f3e5f5") };
    private static final Color[] SEGMENT_COLORS = { Color.web("#bbdefb"), Color.web("#c8e6c9"), Color.web("#ffecb3"), Color.web("#d1c4e9") };

    public ArchitecturePanel() {
        this.setStyle("-fx-background-color: #fafafa;");

        canvas = new Pane();
        canvas.setPrefSize(CANVAS_BASE_WIDTH, CANVAS_BASE_HEIGHT);
        canvas.setMinSize(CANVAS_BASE_WIDTH, CANVAS_BASE_HEIGHT);
        canvas.setMaxSize(CANVAS_BASE_WIDTH, CANVAS_BASE_HEIGHT);
        canvas.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 1;");

        zoomGroup = new Pane(canvas);
        zoomGroup.setPrefSize(CANVAS_BASE_WIDTH, CANVAS_BASE_HEIGHT);
        zoomGroup.setMinSize(CANVAS_BASE_WIDTH, CANVAS_BASE_HEIGHT);
        zoomGroup.setMaxSize(CANVAS_BASE_WIDTH, CANVAS_BASE_HEIGHT);

        scaleTransform = new Scale(1.0, 1.0, 0, 0);
        zoomGroup.getTransforms().add(scaleTransform);

        setupCanvasHandlers();
        setupDragAndDropHandlers();

        this.setContent(zoomGroup);
        this.setFitToWidth(false);
        this.setFitToHeight(false);
        this.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
    }

    private void setupCanvasHandlers() {
        canvas.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                clearActiveSelection();
            } else if (e.getButton() == MouseButton.SECONDARY) {
                if (currentMode == InteractionMode.CREATE_CONNECTION) {
                    setInteractionMode(InteractionMode.NORMAL);
                    pendingConnectionSource = null;
                    System.out.println("✅ Режим создания связи отменён (ПКМ)");
                }
            }
        });
    }

    public void setZoom(double zoomPercent) {
        currentZoom = zoomPercent / 100.0;
        scaleTransform.setX(currentZoom);
        scaleTransform.setY(currentZoom);

        double newWidth = CANVAS_BASE_WIDTH * currentZoom;
        double newHeight = CANVAS_BASE_HEIGHT * currentZoom;
        zoomGroup.setPrefSize(newWidth, newHeight);
        zoomGroup.setMinSize(newWidth, newHeight);
        zoomGroup.setMaxSize(newWidth, newHeight);
        this.requestLayout();
    }

    public void setInteractionMode(InteractionMode mode) {
        this.currentMode = mode;
        if (mode == InteractionMode.NORMAL) {
            connectionPointManager.hidePoints();
            canvas.setCursor(javafx.scene.Cursor.DEFAULT);
            pendingConnectionSource = null;
            cleanupRubberBand();
        } else if (mode == InteractionMode.CREATE_CONNECTION) {
            canvas.setCursor(javafx.scene.Cursor.CROSSHAIR);
        }
    }

    public void addContour(double x, double y, double width, double height, String name) {
        ContourShape contour = new ContourShape(x, y, width, height, name, CONTOUR_COLORS[contours.size() % CONTOUR_COLORS.length], this::getZoom);
        contours.add(contour);
        canvas.getChildren().add(contour.getGroup());
    }

    public void addSegment(double x, double y, double width, double height, String name) {
        SegmentShape segment = new SegmentShape(x, y, width, height, name, SEGMENT_COLORS[segments.size() % SEGMENT_COLORS.length], this::getZoom);
        segments.add(segment);
        canvas.getChildren().add(segment.getGroup());
    }

    private void clearActiveSelection() {
        contours.forEach(c -> c.setActive(false));
        segments.forEach(s -> s.setActive(false));
    }

    private void setupDragAndDropHandlers() {
        canvas.setOnDragOver(event -> {
            if (event.getDragboard().hasString()) event.acceptTransferModes(TransferMode.COPY);
            event.consume();
        });

        canvas.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                String[] parts = db.getString().split("\\|");
                if (parts.length == 3) {
                    createComponent(event.getX(), event.getY(), parts[1], parts[2], parts[0]);
                    event.setDropCompleted(true);
                }
            }
            event.consume();
        });
    }

    private void createComponent(double x, double y, String name, String type, String id) {
        ComponentView view = new ComponentView(
                x, y, name, type, id,
                this::getZoom,
                () -> this.currentMode,
                this::onComponentHoverEnter,
                this::onComponentHoverExit,
                instance -> updateConnectionsForComponent(instance) // <-- ТЕПЕРЬ ОШИБКИ НЕТ!
        );

        componentViews.add(view);
        logicalModel.addComponent(view.getInstance());
        canvas.getChildren().add(view.getGroup());
        System.out.println("✅ Компонент: " + view.getInstance().getInstanceId());
    }

    // ТЕПЕРЬ ПРИНИМАЮТ Group
    private void onComponentHoverEnter(prizma.core.model.ComponentInstance instance, javafx.scene.Group componentGroup) {
        connectionPointManager.showPoints(instance, componentGroup, this::handleConnectionPointClick);
    }

    private void onComponentHoverExit(javafx.scene.Group componentGroup) {
        connectionPointManager.hidePoints();
    }

    public double getZoom() {
        return currentZoom;
    }

    public LogicalModel getLogicalModel() {
        return logicalModel;
    }

    /**
     * Обрабатывает клик по точке подключения и управляет "резиновой" линией.
     */
    private void handleConnectionPointClick(prizma.core.model.ConnectionPoint clickedPoint) {
        if (pendingConnectionSource == null) {
            // ПЕРВЫЙ КЛИК: Запоминаем точку и рисуем резиновую линию
            pendingConnectionSource = clickedPoint;
            System.out.println("🔗 Начало связи: точка " + clickedPoint.getIndex() +
                    " компонента " + clickedPoint.getOwner().getInstanceId());

            rubberBandLine = new javafx.scene.shape.Polyline();
            // ДЕЛАЕМ ЛИНИЮ ТАКОЙ ЖЕ, КАК ФИНАЛЬНАЯ СВЯЗЬ:
            rubberBandLine.setStroke(javafx.scene.paint.Color.web("#7b1fa2")); // Фиолетовый
            rubberBandLine.setStrokeWidth(2.0);
            rubberBandLine.setFill(null);
            rubberBandLine.setMouseTransparent(true); // КРИТИЧЕСКИ ВАЖНО: линия не перехватывает мышь!
            rubberBandLine.toFront(); // Рисуем поверх всего

            canvas.getChildren().add(rubberBandLine);

            // Используем ФИЛЬТР событий на всей панели, чтобы ловить движение мыши
            // даже когда курсор находится НАД компонентами (а не только на пустом canvas)
            this.addEventFilter(javafx.scene.input.MouseEvent.MOUSE_MOVED, this::handleRubberBandMouseMove);

        } else {
            // ВТОРОЙ КЛИК: Создаем связь
            if (pendingConnectionSource != clickedPoint) {
                createConnection(pendingConnectionSource, clickedPoint);
            } else {
                System.out.println("⚠️ Нельзя соединить точку саму с собой");
            }

            // Очищаем резиновую линию и обработчики
            cleanupRubberBand();

            pendingConnectionSource = null;
            setInteractionMode(InteractionMode.NORMAL);
            System.out.println("✅ Связь создана, возврат в обычный режим");
        }
    }

    /**
     * Обрабатывает движение мыши для отрисовки "резиновой" линии.
     * Вынесен в отдельный метод, чтобы избежать проблем с инициализацией полей.
     */
    private void handleRubberBandMouseMove(javafx.scene.input.MouseEvent e) {
        if (rubberBandLine != null && pendingConnectionSource != null) {
            // Получаем координаты мыши относительно canvas
            javafx.geometry.Point2D mouseInCanvas = canvas.screenToLocal(e.getScreenX(), e.getScreenY());
            double mouseX = mouseInCanvas.getX();
            double mouseY = mouseInCanvas.getY();

            double startX = pendingConnectionSource.getAbsolutePosition().getX();
            double startY = pendingConnectionSource.getAbsolutePosition().getY();

            // ОРТОГОНАЛЬНАЯ (Г-образная) ломаная линия, как в финальной связи!
            rubberBandLine.getPoints().setAll(
                    startX, startY,
                    mouseX, startY,
                    mouseX, mouseY
            );
        }
    }

    private void cleanupRubberBand() {
        if (rubberBandLine != null) {
            canvas.getChildren().remove(rubberBandLine);
            rubberBandLine = null;
        }
        // Удаляем слушатель, чтобы не было утечек памяти
        this.removeEventFilter(javafx.scene.input.MouseEvent.MOUSE_MOVED, this::handleRubberBandMouseMove);
    }

    /**
     * Создаёт объект связи и сохраняет его в модели.
     */
    /**
     * Создаёт объект связи, сохраняет его в модели и рисует на холсте.
     */
    private void createConnection(prizma.core.model.ConnectionPoint source, prizma.core.model.ConnectionPoint target) {
        String connectionId = "conn_" + System.currentTimeMillis();

        // 1. Создаём модель связи
        prizma.core.model.Connection connection = new prizma.core.model.Connection(
                connectionId,
                source.getOwner().getInstanceId(),
                target.getOwner().getInstanceId()
        );

        // Сохраняем индексы точек подключения
        connection.setSourcePointIndex(source.getIndex());
        connection.setTargetPointIndex(target.getIndex());

        logicalModel.addConnection(connection);

        // 2. Создаём визуальное представление связи
        ConnectionView connectionView = new ConnectionView(
                connection,
                source.getOwner(),
                target.getOwner(),
                source.getIndex(),
                target.getIndex(),
                () -> deleteConnection(connection),           // Действие "Удалить"
                () -> openConnectionConfigDialog(connection)  // Действие "Настроить"
        );

        connectionViews.add(connectionView);
        canvas.getChildren().add(connectionView.getVisualGroup());

        System.out.println("🔗 Создана связь: " + source.getOwner().getInstanceId() +
                " → " + target.getOwner().getInstanceId() +
                " (" + connection.getProtocol() + ")");
    }

    /**
     * Обновляет все связи, связанные с указанным компонентом.
     */
    public void updateConnectionsForComponent(prizma.core.model.ComponentInstance component) {
        for (ConnectionView view : connectionViews) {
            if (view.getModel().getSourceId().equals(component.getInstanceId()) ||
                    view.getModel().getTargetId().equals(component.getInstanceId())) {
                view.update();
            }
        }
    }

    /**
     * Удаляет связь из модели и с холста.
     */
    private void deleteConnection(prizma.core.model.Connection connection) {
        // Удаляем из модели
        logicalModel.removeConnection(connection.getId());

        // Удаляем визуальное представление с холста
        connectionViews.removeIf(view -> {
            if (view.getModel().equals(connection)) {
                canvas.getChildren().remove(view.getVisualGroup());
                return true;
            }
            return false;
        });

        System.out.println("🗑️ Связь удалена: " + connection.getId());
    }

    /**
     * Открывает диалог настройки связи (пока заглушка).
     */
    private void openConnectionConfigDialog(prizma.core.model.Connection connection) {
        // TODO: Здесь позже добавим модальное окно с настройками
        // (ComboBox для протокола, CheckBox для шифрования и т.д.)
        System.out.println("⚙️ Открыта настройка для связи: " + connection.getId());
        System.out.println("   Протокол: " + connection.getProtocol());
        System.out.println("   Зашифрована: " + connection.isEncrypted());
    }
}