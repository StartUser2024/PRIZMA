package prizma.ui;

import javafx.scene.Scene;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import prizma.ui.panel.*;
import prizma.ui.panel.widgets.ComponentCatalogPanel;
import prizma.ui.panel.widgets.RibbonPanel;
import prizma.ui.panel.widgets.StatusBar;
import prizma.ui.panel.widgets.ValidationPanel;

import java.util.Arrays;
import java.util.List;

public class MainWindow {
    private final Stage stage;
    private final ArchitecturePanel archPanel;
    private final StatusBar statusBar;

    // Дискретные значения масштаба
    private static final List<Double> DISCRETE_VALUES = Arrays.asList(
            20.0, 25.0, 30.0, 40.0, 50.0, 60.0, 75.0, 90.0, 100.0,
            110.0, 125.0, 150.0, 175.0, 200.0, 250.0, 300.0
    );

    public MainWindow(Stage stage,
                      ArchitecturePanel archPanel,
                      ComponentCatalogPanel catalogPanel) {
        this.stage = stage;
        this.archPanel = archPanel;

        // 1. Лента (Ribbon)
        RibbonPanel ribbon = new RibbonPanel();
        ribbon.setArchitecturePanel(archPanel);

        // 2. Основная область: каталог слева + холст по центру
        HBox workspace = new HBox();
        HBox.setHgrow(archPanel, Priority.ALWAYS);
        workspace.getChildren().addAll(catalogPanel, archPanel);

        // 3. Нижняя панель статуса
        statusBar = new StatusBar();

        // Связываем ползунок масштаба с холстом
        statusBar.getZoomSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            archPanel.setZoom(newVal.doubleValue());
        });

        // 4. ПРИОРИТЕТНЫЙ обработчик CTRL + колёсико (Используем Event Filter!)
        setupZoomOnScroll();

        // 5. Панель результатов проверки (будет снизу)
        ValidationPanel validationPanel = new ValidationPanel();

        // 6. Компоновка с использованием SplitPane для изменения высоты панелей
        // Создаем вертикальный SplitPane (разделитель будет горизонтальным)
        javafx.scene.control.SplitPane mainSplitPane = new javafx.scene.control.SplitPane();
        mainSplitPane.setOrientation(javafx.geometry.Orientation.VERTICAL);
        mainSplitPane.setStyle("-fx-background-color: #fafafa;"); // Цвет фона между панелями

        // Добавляем рабочую область (каталог + холст) и панель проверки
        mainSplitPane.getItems().addAll(workspace, validationPanel);

        // Настраиваем позиции разделителей (0.8 = 80% на рабочую область, 20% на проверки)
        mainSplitPane.setDividerPositions(0.80);

        // Итоговая сборка главного окна
        BorderPane root = new BorderPane();
        root.setTop(ribbon);
        root.setCenter(mainSplitPane); // В центр кладём SplitPane, а не просто workspace
        root.setBottom(statusBar);

        Scene scene = new Scene(root, 1280, 720);
        stage.setTitle("ПРИЗМА — Проектирование архитектуры ИС");
        stage.setScene(scene);

        // Тестовые данные
        archPanel.addContour(100, 100, 400, 300, "Контур L1");
        archPanel.addContour(600, 100, 400, 300, "Контур L2");
        archPanel.addSegment(100, 500, 400, 200, "Сегмент ПДн");
        archPanel.addSegment(600, 500, 400, 200, "Сегмент ГИС");
    }

    private void setupZoomOnScroll() {
        // ВАЖНО: addEventFilter перехватывает событие ДО того, как его обработает ScrollPane
        archPanel.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                // "Съедаем" событие. ScrollPane НЕ будет прокручивать холст!
                event.consume();

                double currentZoom = statusBar.getZoomSlider().getValue();
                double deltaY = event.getDeltaY();

                int currentIndex = findClosestIndex(currentZoom);
                int newIndex = currentIndex;

                if (deltaY > 0 && currentIndex < DISCRETE_VALUES.size() - 1) {
                    newIndex = currentIndex + 1; // Увеличение
                } else if (deltaY < 0 && currentIndex > 0) {
                    newIndex = currentIndex - 1; // Уменьшение
                }

                double newZoom = DISCRETE_VALUES.get(newIndex);
                statusBar.getZoomSlider().setValue(newZoom);
            }
        });
    }

    private int findClosestIndex(double value) {
        int closestIndex = 0;
        double minDiff = Math.abs(DISCRETE_VALUES.get(0) - value);

        for (int i = 1; i < DISCRETE_VALUES.size(); i++) {
            double diff = Math.abs(DISCRETE_VALUES.get(i) - value);
            if (diff < minDiff) {
                minDiff = diff;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    public void show() {
        stage.show();
    }
}