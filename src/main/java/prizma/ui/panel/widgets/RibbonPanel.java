package prizma.ui.panel.widgets;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import prizma.ui.panel.ArchitecturePanel;
import prizma.ui.panel.InteractionMode;

import java.util.LinkedHashMap;
import java.util.Map;

public class RibbonPanel extends VBox {
    private ArchitecturePanel architecturePanel;

    private final Map<String, Label> tabLabels = new LinkedHashMap<>();
    private final Map<String, VBox> tabContents = new LinkedHashMap<>();
    private final StackPane contentArea = new StackPane();

    private String activeTab = "Главная";

    // Цветовая палитра
    private static final String COLOR_RIBBON_BG = "#ce93d8";
    private static final String COLOR_TAB_ACTIVE = "#ab47bc";
    private static final String COLOR_TAB_INACTIVE = "#ce93d8";
    private static final String COLOR_CONTENT_BG = "#f3e5f5";
    private static final String COLOR_TEXT = "#4a148c";

    public RibbonPanel() {
        this.setPrefHeight(120);
        this.setMinHeight(120);
        this.setMaxHeight(120);
        this.setStyle("-fx-background-color: " + COLOR_RIBBON_BG + ";");
        this.setSpacing(0);

        buildTabs();
        buildTabContents();

        // Верхняя часть с вкладками
        HBox tabBar = createTabBar();

        // Нижняя часть с контентом
        contentArea.setStyle("-fx-background-color: " + COLOR_CONTENT_BG + ";");
        contentArea.setPadding(new Insets(5));

        this.getChildren().addAll(tabBar, contentArea);

        // Активируем вкладку по умолчанию
        selectTab(activeTab);
    }

    private void buildTabs() {
        // Порядок вкладок важен!
        tabLabels.put("Файл", null);
        tabLabels.put("Главная", null);
        tabLabels.put("Связи", null);
        tabLabels.put("Контуры", null);
        tabLabels.put("Сегменты", null);
        tabLabels.put("Проверка", null);
        tabLabels.put("Оценка", null);
    }

    private void buildTabContents() {
        tabContents.put("Файл", createFileTabContent());
        tabContents.put("Главная", createHomeTabContent());
        tabContents.put("Связи", createConnectionsTabContent());
        tabContents.put("Контуры", createContoursTabContent());
        tabContents.put("Сегменты", createSegmentsTabContent());
        tabContents.put("Проверка", createValidationTabContent());
        tabContents.put("Оценка", createEvaluationTabContent());
    }

    private HBox createTabBar() {
        HBox tabBar = new HBox(0);
        tabBar.setStyle("-fx-background-color: " + COLOR_RIBBON_BG + ";");

        for (String tabName : tabLabels.keySet()) {
            Label tabLabel = new Label(tabName);
            tabLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
            tabLabel.setPadding(new Insets(8, 15, 8, 15));
            tabLabel.setStyle("-fx-text-fill: white; -fx-cursor: hand;");
            tabLabel.setOnMouseClicked(e -> selectTab(tabName));

            tabLabels.put(tabName, tabLabel);
            tabBar.getChildren().add(tabLabel);
        }

        // Заполнитель справа
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        tabBar.getChildren().add(spacer);

        return tabBar;
    }

    private void selectTab(String tabName) {
        activeTab = tabName;

        // Сброс стилей всех вкладок
        for (Label lbl : tabLabels.values()) {
            lbl.setStyle("-fx-text-fill: white; -fx-cursor: hand; -fx-background-color: " + COLOR_TAB_INACTIVE + ";");
        }

        // Подсветка активной вкладки
        Label activeLabel = tabLabels.get(tabName);
        if (activeLabel != null) {
            activeLabel.setStyle("-fx-text-fill: white; -fx-cursor: hand; -fx-background-color: " + COLOR_TAB_ACTIVE + "; -fx-font-weight: bold;");
        }

        // Смена контента
        VBox content = tabContents.get(tabName);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(content);
    }

    // === Контент вкладок (заглушки) ===

    private VBox createFileTabContent() {
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("📁 Файл");
        title.setFont(Font.font("System", FontWeight.BOLD, 13));
        title.setStyle("-fx-text-fill: " + COLOR_TEXT + ";");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_LEFT);

        buttons.getChildren().addAll(
                createRibbonButton("📄 Новый"),
                createRibbonButton("📂 Открыть"),
                createRibbonButton("💾 Сохранить"),
                createRibbonButton("📤 Экспорт")
        );

        content.getChildren().addAll(title, buttons);
        return content;
    }

    private VBox createHomeTabContent() {
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🏠 Главная");
        title.setFont(Font.font("System", FontWeight.BOLD, 13));
        title.setStyle("-fx-text-fill: " + COLOR_TEXT + ";");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_LEFT);

        buttons.getChildren().addAll(
                createRibbonButton("↶ Отменить"),
                createRibbonButton("↷ Повторить"),
                createRibbonButton("🗑 Удалить"),
                createRibbonButton("📋 Копировать")
        );

        content.getChildren().addAll(title, buttons);
        return content;
    }

    private VBox createConnectionsTabContent() {
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("🔗 Связи");
        title.setFont(Font.font("System", FontWeight.BOLD, 13));
        title.setStyle("-fx-text-fill: " + COLOR_TEXT + ";");
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Label createConnectionBtn = createRibbonButton("➕ Создать связь");
        createConnectionBtn.setOnMouseClicked(e -> {
            if (architecturePanel != null) {
                architecturePanel.setInteractionMode(InteractionMode.CREATE_CONNECTION);
                System.out.println("✅ Режим создания связи активирован");
            } else {
                System.err.println("❌ ArchitecturePanel не установлен!");
            }
        });

        buttons.getChildren().addAll(
                createConnectionBtn,
                createRibbonButton("✏️ Редактировать"),
                createRibbonButton("🔒 Шифрование"),
                createRibbonButton("📊 Протокол")
        );

        content.getChildren().addAll(title, buttons);
        return content;
    }

    private VBox createContoursTabContent() {
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🎯 Контуры");
        title.setFont(Font.font("System", FontWeight.BOLD, 13));
        title.setStyle("-fx-text-fill: " + COLOR_TEXT + ";");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_LEFT);

        // Кнопки для добавления контуров
        Label addL1 = createRibbonButton("Добавить L1");
        addL1.setOnMouseClicked(e -> {
            // Получаем ссылку на ArchitecturePanel через MainWindow
            // Пока используем заглушку — добавляем в центр холста
            System.out.println("Добавление контура L1");
        });

        buttons.getChildren().addAll(
                addL1,
                createRibbonButton("Добавить L2"),
                createRibbonButton("Добавить L3"),
                createRibbonButton("Добавить L4"),
                createRibbonButton("Добавить L5")
        );

        content.getChildren().addAll(title, buttons);
        return content;
    }

    private VBox createSegmentsTabContent() {
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("📋 Сегменты");
        title.setFont(Font.font("System", FontWeight.BOLD, 13));
        title.setStyle("-fx-text-fill: " + COLOR_TEXT + ";");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_LEFT);

        buttons.getChildren().addAll(
                createRibbonButton("👤 ПДн"),
                createRibbonButton("🏛 ГИС"),
                createRibbonButton("🌐 Открытый"),
                createRibbonButton("⚙️ Админ.")
        );

        content.getChildren().addAll(title, buttons);
        return content;
    }

    private VBox createValidationTabContent() {
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("✅ Проверка");
        title.setFont(Font.font("System", FontWeight.BOLD, 13));
        title.setStyle("-fx-text-fill: " + COLOR_TEXT + ";");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_LEFT);

        buttons.getChildren().addAll(
                createRibbonButton("🔍 Проверить модель"),
                createRibbonButton("📝 Отчёт"),
                createRibbonButton("🤖 LLM-анализ")
        );

        content.getChildren().addAll(title, buttons);
        return content;
    }

    private VBox createEvaluationTabContent() {
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("📊 Оценка");
        title.setFont(Font.font("System", FontWeight.BOLD, 13));
        title.setStyle("-fx-text-fill: " + COLOR_TEXT + ";");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_LEFT);

        buttons.getChildren().addAll(
                createRibbonButton("🎯 Совместимость"),
                createRibbonButton("🛡 Безопасность"),
                createRibbonButton("💰 Экономика")
        );

        content.getChildren().addAll(title, buttons);
        return content;
    }

    private Label createRibbonButton(String text) {
        Label button = new Label(text);
        button.setFont(Font.font("System", 11));
        button.setPadding(new Insets(6, 12, 6, 12));
        button.setStyle("-fx-background-color: white; -fx-border-color: #ab47bc; -fx-border-radius: 4; " +
                "-fx-background-radius: 4; -fx-cursor: hand; -fx-text-fill: " + COLOR_TEXT + ";");

        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: #e1bee7; -fx-border-color: #8e24aa; -fx-border-radius: 4; " +
                        "-fx-background-radius: 4; -fx-cursor: hand; -fx-text-fill: " + COLOR_TEXT + ";"));
        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: white; -fx-border-color: #ab47bc; -fx-border-radius: 4; " +
                        "-fx-background-radius: 4; -fx-cursor: hand; -fx-text-fill: " + COLOR_TEXT + ";"));

        return button;
    }

    public void setArchitecturePanel(ArchitecturePanel panel) {
        this.architecturePanel = panel;
    }
}