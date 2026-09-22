package prizma.ui.panel.widgets;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class StatusBar extends HBox {

    private final Label zoomLabel;
    private final Slider zoomSlider;
    private final Label statusLabel;

    // Дискретные значения масштаба
    private static final List<Double> DISCRETE_VALUES = Arrays.asList(
            20.0, 25.0, 30.0, 40.0, 50.0, 60.0, 75.0, 90.0, 100.0,
            110.0, 125.0, 150.0, 175.0, 200.0, 250.0, 300.0
    );

    public StatusBar() {
        this.setPrefHeight(30);
        this.setMinHeight(30);
        this.setMaxHeight(30);
        this.setPadding(new Insets(0, 10, 0, 10));
        this.setAlignment(Pos.CENTER_LEFT);
        this.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #e0e0e0; -fx-border-width: 1 0 0 0;");
        this.setSpacing(10);

        statusLabel = new Label("Готово");
        statusLabel.setFont(Font.font("System", 11));
        statusLabel.setStyle("-fx-text-fill: #666666;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label scaleLabel = new Label("Масштаб:");
        scaleLabel.setFont(Font.font("System", 11));
        scaleLabel.setStyle("-fx-text-fill: #666666;");

        // Slider с диапазоном от минимального до максимального значения
        zoomSlider = new Slider(20, 300, 100);
        zoomSlider.setPrefWidth(150);
        zoomSlider.setShowTickLabels(false);
        zoomSlider.setShowTickMarks(false);
        zoomSlider.setSnapToTicks(false); // Отключаем стандартную привязку

        zoomLabel = new Label("100%");
        zoomLabel.setFont(Font.font("System", 11));
        zoomLabel.setStyle("-fx-text-fill: #333333; -fx-font-weight: bold;");
        zoomLabel.setMinWidth(40);

        // Привязка к дискретным значениям
        zoomSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double value = newVal.doubleValue();

            // Находим ближайшее дискретное значение
            double closest = DISCRETE_VALUES.stream()
                    .min(Comparator.comparingDouble(v -> Math.abs(v - value)))
                    .orElse(100.0);

            // Устанавливаем ближайшее значение
            if (Math.abs(closest - value) > 0.1) {
                zoomSlider.setValue(closest);
            }

            zoomLabel.setText((int) closest + "%");
        });

        this.getChildren().addAll(statusLabel, spacer, scaleLabel, zoomSlider, zoomLabel);
    }

    public Slider getZoomSlider() {
        return zoomSlider;
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }
}