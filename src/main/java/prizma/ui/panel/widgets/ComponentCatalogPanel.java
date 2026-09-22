package prizma.ui.panel.widgets;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import prizma.core.model.ComponentProfile;
import prizma.infrastructure.loader.ComponentsRegistryLoader;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ComponentCatalogPanel extends VBox {

    private final Map<String, List<ComponentProfile>> allCategories = new LinkedHashMap<>();
    private final Map<String, Label> sectionLabelsMap = new LinkedHashMap<>();

    private final List<String> mainTypes = List.of("СЗИ от НСД", "СКЗИ", "Антивирус", "Межсетевой экран", "СУБД");
    private String currentSelectedCategory = "СЗИ от НСД";

    private VBox componentsDisplayArea;
    private TextField searchField;

    // Цветовая палитра
    private static final String COLOR_BG_PANEL = "#f4f4f4";
    private static final String COLOR_HEADER = "#6a1b9a";
    private static final String COLOR_TEXT_MAIN = "#212121";
    private static final String COLOR_TEXT_HINT = "#9e9e9e";
    private static final String COLOR_ACTIVE_BG = "#e1bee7";
    private static final String COLOR_ACTIVE_TEXT = "#4a148c";
    private static final String COLOR_CARD_BG = "#ffffff";
    private static final String COLOR_CARD_BORDER = "#bdbdbd";
    private static final String COLOR_CARD_HOVER_BG = "#d1c4e9";
    private static final String COLOR_CARD_HOVER_BORDER = "#8e24aa";

    public ComponentCatalogPanel() {
        this.setPrefWidth(260);
        this.setMinWidth(260);
        this.setStyle("-fx-background-color: " + COLOR_BG_PANEL + "; -fx-border-color: #9e9e9e; -fx-border-width: 0 1 0 0;");
        this.setSpacing(8);
        this.setPadding(new Insets(12));

        ComponentsRegistryLoader loader = new ComponentsRegistryLoader();
        List<ComponentProfile> profiles = loader.loadComponents();
        groupByType(profiles);

        buildUI();
        selectCategory(currentSelectedCategory);
    }

    private void groupByType(List<ComponentProfile> profiles) {
        for (ComponentProfile profile : profiles) {
            String type = profile.getType();
            allCategories.computeIfAbsent(type, k -> new ArrayList<>()).add(profile);
        }
    }

    private void buildUI() {
        // 1. Заголовок
        Label header = new Label("Компоненты");
        header.setFont(Font.font("System", FontWeight.BOLD, 15));
        header.setStyle("-fx-text-fill: " + COLOR_HEADER + ";");

        // 2. Поле поиска
        searchField = new TextField();
        searchField.setPromptText("🔍 Поиск компонента...");
        searchField.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; " +
                "-fx-border-color: " + COLOR_CARD_BORDER + "; " +
                "-fx-border-radius: 4; -fx-background-radius: 4; " +
                "-fx-text-fill: " + COLOR_TEXT_MAIN + "; " +
                "-fx-prompt-text-fill: " + COLOR_TEXT_HINT + "; " +
                "-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                // Если поиск пустой — возвращаемся к выбранной категории
                selectCategory(currentSelectedCategory);
            } else {
                // Если есть текст — показываем результаты из ВСЕХ категорий
                showSearchResults(newVal);
            }
        });

        // 3. Список разделов
        VBox sectionsList = new VBox(2);

        sectionsList.getChildren().add(createSectionLabel("★ Избранное"));
        for (String type : mainTypes) {
            if (allCategories.containsKey(type)) {
                sectionsList.getChildren().add(createSectionLabel(type));
            }
        }

        // Дополнительно (с ContextMenu)
        Label extraLabel = new Label("▾ Дополнительно");
        extraLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        extraLabel.setPadding(new Insets(3, 7, 3, 7)); // Уменьшено в 1.5 раза
        extraLabel.setStyle("-fx-cursor: hand; -fx-text-fill: " + COLOR_TEXT_MAIN + "; -fx-background-radius: 4;");

        ContextMenu extraMenu = new ContextMenu();
        for (String type : allCategories.keySet()) {
            if (!mainTypes.contains(type) && !type.equals("★ Избранное")) {
                MenuItem item = new MenuItem(type);
                item.setStyle("-fx-font-size: 12px;");
                item.setOnAction(e -> selectCategory(type));
                extraMenu.getItems().add(item);
            }
        }
        extraLabel.setOnMouseClicked(e -> extraMenu.show(extraLabel, Side.BOTTOM, 0, 0));
        sectionsList.getChildren().add(extraLabel);
        sectionLabelsMap.put("Дополнительно", extraLabel);

        // 4. Разделительная черта
        Region separator = new Region();
        separator.setMinHeight(1);
        separator.setMaxHeight(1);
        separator.setStyle("-fx-background-color: #9e9e9e;");
        separator.setPadding(new Insets(8, 0, 8, 0));

        // 5. Область отображения компонентов (убираем синий фокус)
        componentsDisplayArea = new VBox(4);
        componentsDisplayArea.setPadding(new Insets(0, 0, 10, 0));
        componentsDisplayArea.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");

        ScrollPane scrollPane = new ScrollPane(componentsDisplayArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-border-color: transparent; " +
                "-fx-focus-color: transparent; -fx-faint-focus-color: transparent;");
        VBox.setVgrow(scrollPane, javafx.scene.layout.Priority.ALWAYS);

        this.getChildren().addAll(header, searchField, sectionsList, separator, scrollPane);
    }

    private Label createSectionLabel(String typeName) {
        Label label = new Label(typeName);
        label.setFont(Font.font("System", FontWeight.SEMI_BOLD, 12));
        label.setPadding(new Insets(3, 7, 3, 7)); // Уменьшено в 1.5 раза (было 5, 10, 5, 10)
        label.setStyle("-fx-cursor: hand; -fx-text-fill: " + COLOR_TEXT_MAIN + "; -fx-background-radius: 4;");
        label.setOnMouseClicked(e -> selectCategory(typeName));

        sectionLabelsMap.put(typeName, label);
        return label;
    }

    private void selectCategory(String typeName) {
        currentSelectedCategory = typeName;

        // Сброс стилей всех разделов
        for (Label lbl : sectionLabelsMap.values()) {
            lbl.setStyle("-fx-cursor: hand; -fx-text-fill: " + COLOR_TEXT_MAIN + "; -fx-background-radius: 4; -fx-background-color: transparent;");
        }

        // Подсветка активного раздела
        Label activeLabel = sectionLabelsMap.get(typeName);
        if (activeLabel != null) {
            activeLabel.setStyle("-fx-cursor: hand; -fx-text-fill: " + COLOR_ACTIVE_TEXT + "; -fx-background-radius: 4; -fx-background-color: " + COLOR_ACTIVE_BG + "; -fx-font-weight: bold;");
        }

        // Очистка и заполнение области компонентов
        componentsDisplayArea.getChildren().clear();
        List<ComponentProfile> profiles = allCategories.get(typeName);

        if (profiles != null && !profiles.isEmpty()) {
            for (ComponentProfile profile : profiles) {
                componentsDisplayArea.getChildren().add(createComponentItem(profile));
            }
        } else {
            Label empty = new Label("Компоненты не найдены");
            empty.setFont(Font.font("System", 11));
            empty.setStyle("-fx-text-fill: " + COLOR_TEXT_HINT + "; -fx-padding: 10;");
            componentsDisplayArea.getChildren().add(empty);
        }
    }

    private void showSearchResults(String searchText) {
        componentsDisplayArea.getChildren().clear();

        // Сброс подсветки активного раздела (при поиске ничего не выбрано)
        for (Label lbl : sectionLabelsMap.values()) {
            lbl.setStyle("-fx-cursor: hand; -fx-text-fill: " + COLOR_TEXT_MAIN + "; -fx-background-radius: 4; -fx-background-color: transparent;");
        }

        String lowerSearch = searchText.toLowerCase();
        boolean found = false;

        // Показываем компоненты из ВСЕХ категорий
        for (Map.Entry<String, List<ComponentProfile>> entry : allCategories.entrySet()) {
            String category = entry.getKey();
            List<ComponentProfile> profiles = entry.getValue();

            List<ComponentProfile> filtered = new ArrayList<>();
            for (ComponentProfile profile : profiles) {
                if (profile.getName().toLowerCase().contains(lowerSearch) ||
                        profile.getType().toLowerCase().contains(lowerSearch)) {
                    filtered.add(profile);
                }
            }

            if (!filtered.isEmpty()) {
                found = true;
                // Заголовок категории
                Label categoryHeader = new Label(category);
                categoryHeader.setFont(Font.font("System", FontWeight.BOLD, 11));
                categoryHeader.setStyle("-fx-text-fill: " + COLOR_HEADER + "; -fx-padding: 8 0 4 0;");
                componentsDisplayArea.getChildren().add(categoryHeader);

                // Компоненты этой категории
                for (ComponentProfile profile : filtered) {
                    componentsDisplayArea.getChildren().add(createComponentItem(profile));
                }
            }
        }

        if (!found) {
            Label empty = new Label("Компоненты не найдены");
            empty.setFont(Font.font("System", 11));
            empty.setStyle("-fx-text-fill: " + COLOR_TEXT_HINT + "; -fx-padding: 10;");
            componentsDisplayArea.getChildren().add(empty);
        }
    }

    private HBox createComponentItem(ComponentProfile profile) {
        HBox item = new HBox(10);
        item.setPadding(new Insets(5, 7, 5, 7));
        item.setAlignment(Pos.CENTER_LEFT);
        item.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 4; " +
                "-fx-border-color: " + COLOR_CARD_BORDER + "; -fx-border-radius: 4; -fx-cursor: hand;");

        Label iconPlaceholder = new Label("📦");
        iconPlaceholder.setStyle("-fx-font-size: 14px;");

        Label nameLabel = new Label(profile.getName());
        nameLabel.setFont(Font.font("System", FontWeight.SEMI_BOLD, 11));
        nameLabel.setStyle("-fx-text-fill: " + COLOR_TEXT_MAIN + ";");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(nameLabel, javafx.scene.layout.Priority.ALWAYS);

        item.getChildren().addAll(iconPlaceholder, nameLabel);

        // Эффекты наведения
        item.setOnMouseEntered(e -> item.setStyle("-fx-background-color: " + COLOR_CARD_HOVER_BG + "; -fx-background-radius: 4; " +
                "-fx-border-color: " + COLOR_CARD_HOVER_BORDER + "; -fx-border-radius: 4; -fx-cursor: hand;"));
        item.setOnMouseExited(e -> item.setStyle("-fx-background-color: " + COLOR_CARD_BG + "; -fx-background-radius: 4; " +
                "-fx-border-color: " + COLOR_CARD_BORDER + "; -fx-border-radius: 4; -fx-cursor: hand;"));

        // === НОВАЯ ЛОГИКА: Начало перетаскивания ===
        item.setOnDragDetected(event -> {
            // Разрешаем копирование
            Dragboard db = item.startDragAndDrop(TransferMode.COPY);

            // Формируем строку данных: id|name|type
            String data = profile.getCertificateId() + "|" + profile.getName() + "|" + profile.getType();

            ClipboardContent content = new ClipboardContent();
            content.putString(data);
            db.setContent(content);

            event.consume();
        });

        return item;
    }
}