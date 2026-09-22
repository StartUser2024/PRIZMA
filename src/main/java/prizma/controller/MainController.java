package prizma.controller;

import prizma.ui.panel.ArchitecturePanel;
import prizma.ui.panel.widgets.ValidationPanel;

public class MainController {

    private final ArchitecturePanel architecturePanel;
    private final ValidationPanel validationPanel;

    public MainController(ArchitecturePanel architecturePanel, ValidationPanel validationPanel) {
        this.architecturePanel = architecturePanel;
        this.validationPanel = validationPanel;

        System.out.println("✅ MainController успешно инициализирован и связан с панелями.");
    }

    private void handleValidation() {
        System.out.println("Запрос на валидацию архитектуры получен.");
    }
}