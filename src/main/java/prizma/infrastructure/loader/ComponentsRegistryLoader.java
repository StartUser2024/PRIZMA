package prizma.infrastructure.loader;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import prizma.core.model.ComponentProfile;
import prizma.core.model.StandardComponentProfile;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ComponentsRegistryLoader {

    private static final String FILE_PATH = "/data/Components.xlsx";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<ComponentProfile> loadComponents() {
        List<ComponentProfile> profiles = new ArrayList<>();

        try (InputStream inputStream = getClass().getResourceAsStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Пропускаем заголовок (строка 0), начинаем со строки 1
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    ComponentProfile profile = parseRow(row);
                    profiles.add(profile);
                } catch (Exception e) {
                    System.err.println("Ошибка парсинга строки " + i + ": " + e.getMessage());
                }
            }

            System.out.println("✅ Загружено компонентов: " + profiles.size());

        } catch (Exception e) {
            System.err.println("❌ Ошибка загрузки файла компонентов: " + e.getMessage());
            e.printStackTrace();
        }

        return profiles;
    }

    private ComponentProfile parseRow(Row row) {
        String certificateId = getCellValue(row, 0);
        LocalDate registrationDate = parseDate(getCellValue(row, 1));
        LocalDate expirationDate = parseDate(getCellValue(row, 2));
        String name = getCellValue(row, 3);
        String type = getCellValue(row, 4);
        String requirements = getCellValue(row, 5);
        String vendor = getCellValue(row, 6);
        String operationalFeatures = getCellValue(row, 7);

        return new StandardComponentProfile(
                certificateId,
                registrationDate,
                expirationDate,
                name,
                type,
                requirements,
                vendor,
                operationalFeatures
        );
    }

    private String getCellValue(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    private LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            System.err.println("Не удалось распарсить дату: " + dateStr);
            return null;
        }
    }
}