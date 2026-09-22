package prizma;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import prizma.controller.MainController;
import prizma.ui.MainWindow;
import prizma.ui.panel.ArchitecturePanel;
import prizma.ui.panel.widgets.ComponentCatalogPanel;
import prizma.ui.panel.widgets.ValidationPanel;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // === ПРИВЕТСТВЕННОЕ ОКНО ===
        Label welcomeLabel = new Label("Добро пожаловать в ПРИЗМУ!");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label subtitleLabel = new Label("Платформа рационального инжиниринга защищенных моделей архитектуры");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        subtitleLabel.setWrapText(true);
        subtitleLabel.setMaxWidth(Double.MAX_VALUE);
        subtitleLabel.setAlignment(Pos.CENTER);
        subtitleLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button startButton = new Button("▶ Начать проектирование");
        startButton.setStyle("-fx-font-size: 14px; -fx-padding: 10 20; -fx-cursor: hand; " +
                "-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");

        startButton.setOnAction(e -> {
            // === ГЛАВНОЕ ОКНО ===
            Stage mainStage = new Stage();

            // 1. Создаём панели (View-компоненты)
            ArchitecturePanel archPanel = new ArchitecturePanel();
            ComponentCatalogPanel catalogPanel = new ComponentCatalogPanel();
            ValidationPanel validationPanel = new ValidationPanel();

            // 2. Создаём контроллер (Controller), передавая ему ссылки на панели
            MainController controller = new MainController(archPanel, validationPanel);

            // 3. Создаём главное окно (View), передавая ему панели
            MainWindow mainWindow = new MainWindow(mainStage, archPanel, catalogPanel);

            // 4. Показываем окно и закрываем приветственное
            mainWindow.show();
            primaryStage.close();
        });

        VBox root = new VBox(20, welcomeLabel, subtitleLabel, startButton);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 40; -fx-background-color: #ecf0f1;");

        Scene scene = new Scene(root, 500, 300);
        primaryStage.setTitle("ПРИЗМА - Приветствие");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}