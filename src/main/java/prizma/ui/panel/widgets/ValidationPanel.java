package prizma.ui.panel.widgets;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Панель результатов валидации архитектуры (аналог окна "Problems" в IDE).
 * Располагается внизу экрана, имеет изменяемую высоту.
 */
public class ValidationPanel extends VBox {

    private final ListView<String> issuesList;

    public ValidationPanel() {
        this.setPrefHeight(150); // Начальная высота панели
        this.setMinHeight(30);   // Минимальная высота (когда свернута)
        this.setStyle("-fx-background-color: #f5f5f5; -fx-border-color: #bdbdbd; -fx-border-width: 1 0 0 0;");

        // Заголовок панели
        Label header = new Label("⚠️ Результаты проверки: 0 проблем");
        header.setFont(Font.font("System", FontWeight.BOLD, 12));
        header.setPadding(new Insets(5, 10, 5, 10));
        header.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #bdbdbd; -fx-border-width: 0 0 1 0;");
        header.setMaxWidth(Double.MAX_VALUE); // Растягиваем на всю ширину

        // Список проблем
        issuesList = new ListView<>();
        issuesList.setPlaceholder(new Label("Ошибок не найдено. Архитектура корректна."));

        VBox.setVgrow(issuesList, javafx.scene.layout.Priority.ALWAYS); // Список занимает всё оставшееся место

        this.getChildren().addAll(header, issuesList);
    }

    public ListView<String> getIssuesList() {
        return issuesList;
    }
}