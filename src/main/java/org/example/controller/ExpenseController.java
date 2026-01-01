package org.example.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.model.Expense;
import org.example.repository.ExpenseRepository;
import org.example.util.DatabaseUtil;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExpenseController {
    @FXML
    private TextField descriptionField;
    @FXML
    private ComboBox<String> categoryCombo;
    @FXML
    private TextField amountField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TableView<Expense> expenseTable;

    @FXML
    private TableColumn<Expense, String> descriptionColumn;
    @FXML
    private TableColumn<Expense, String> categoryColumn;
    @FXML
    private TableColumn<Expense, Double> amountColumn;
    @FXML
    private TableColumn<Expense, LocalDate> dateColumn;

    @FXML
    private Label lbTotal;

    private final ExpenseRepository repository = new ExpenseRepository();
    private final ObservableList<Expense> expenses = FXCollections.observableArrayList(DatabaseUtil.loadExpensesFromDB());

    @FXML
    public void initialize() {

        updateTotal();
        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>("description")
        );
        categoryColumn.setCellValueFactory(
                new PropertyValueFactory<>("category")
        );
        amountColumn.setCellValueFactory(
                new PropertyValueFactory<>("amount")
        );
        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("date")
        );
        expenseTable.setItems(expenses);
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    private void handleAddExpense() {
        try {
            Expense expense = buildExpenseFromForm();
            repository.save(expense);

            expenses.add(expense);
            updateTotal();
            clearForm();

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private Expense buildExpenseFromForm() {
        String description = descriptionField.getText();
        String category = categoryCombo.getValue();
        String amountText = amountField.getText();
        LocalDate date = datePicker.getValue();

        if (description.isBlank() || category == null || amountText.isBlank() || date == null) {
            throw new IllegalArgumentException("Preenche todos os campos.");
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Valor inválido.");
        }

        return new Expense(description, category, amount, date);
    }

    private void clearForm() {
        descriptionField.clear();
        amountField.clear();
        categoryCombo.setValue(null);
        datePicker.setValue(LocalDate.now());
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateTotal() {
        double total = expenses.stream()
                .mapToDouble(Expense::getAmount) // assume que getAmount() retorna double
                .sum();
        lbTotal.setText(String.format("€ %.2f", total));
    }

    @FXML
    private void handleExportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Despesas" + datePicker.getValue());
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Cabeçalho
                writer.write("Descrição,Categoria,Valor,Data");
                writer.newLine();

                // Conteúdo
                for (Expense exp : expenses) {
                    writer.write(String.format("%s,%s,%.2f,%s",
                            exp.getDescription(),
                            exp.getCategory(),
                            exp.getAmount(),
                            exp.getDate())); // se usar LocalDate, toString funciona
                    writer.newLine();
                }

                // Feedback ao usuário
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exportação concluída");
                alert.setHeaderText(null);
                alert.setContentText("Despesas exportadas com sucesso!");
                alert.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Erro ao exportar CSV!");
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleExportExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salvar Despesas como Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Despesas");

                // Cabeçalho
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Descrição");
                header.createCell(1).setCellValue("Categoria");
                header.createCell(2).setCellValue("Valor");
                header.createCell(3).setCellValue("Data");

                // Conteúdo
                int rowNum = 1;
                Row row = sheet.createRow(0);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                for (Expense exp : expenses) {
                    row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(exp.getDescription());
                    row.createCell(1).setCellValue(exp.getCategory());
                    row.createCell(2).setCellValue(exp.getAmount());
                    row.createCell(3).setCellValue(exp.getDate().format(formatter));
                }

                CellStyle style = workbook.createCellStyle();
                Font font = workbook.createFont();
                font.setBold(true);          // deixa negrito
                style.setFont(font);

                rowNum++;
                row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue("Total");
                row.getCell(0).setCellStyle(style);
                row.createCell(1).setCellValue(lbTotal.getText());
                // Ajustar largura das colunas
                for (int i = 0; i < 4; i++) {
                    sheet.autoSizeColumn(i);
                }


                // Salvar arquivo
                try (FileOutputStream fileOut = new FileOutputStream(file)) {
                    workbook.write(fileOut);
                }

                // Feedback
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exportação concluída");
                alert.setHeaderText(null);
                alert.setContentText("Despesas exportadas com sucesso para Excel!");
                alert.showAndWait();

            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(null);
                alert.setContentText("Erro ao exportar Excel!");
                alert.showAndWait();
            }
        }
    }
}
