package com.iit.tutorials.tax_cw;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.util.List;

public class HomeController {

    @FXML private TextField filePathField;
    @FXML private TableView<TaxDataRecord> tableView;
    @FXML private TableColumn<TaxDataRecord, Integer> billNoColumn;
    @FXML private TableColumn<TaxDataRecord, String> itemCodeColumn;
    @FXML private TableColumn<TaxDataRecord, Double> internalPriceColumn;
    @FXML private TableColumn<TaxDataRecord, Double> salesPriceColumn;
    @FXML private TableColumn<TaxDataRecord, Double> discountColumn;
    @FXML private TableColumn<TaxDataRecord, Integer> qtyColumn;
    @FXML private TableColumn<TaxDataRecord, String> validColumn;
    @FXML private Label statusLabel;
    @FXML private Button deleteButton;
    @FXML private Label totalProfitLabel;
    @FXML private TextField taxInput;
    @FXML private Label taxLabel;
    @FXML private Label totalRecLabel;
    @FXML private Label totalValidLabel;
    @FXML private Label totalInvalidLabel;



    @FXML
    private void onDeleteButtonClick() {
        TaxDataRecord selectedRecord = tableView.getSelectionModel().getSelectedItem();
        if (selectedRecord != null) {
            if ("False".equalsIgnoreCase(selectedRecord.getValid())) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Delete Confirmation");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Are you sure you want to delete the selected invalid record?");

                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        tableView.getItems().remove(selectedRecord);
                        updateTotalProfit();
                        statusLabel.setText("Invalid record deleted successfully.");
                    }
                });
            } else {
                showAlert("Cannot Delete", "Only invalid records can be deleted.");
            }
        } else {
            showAlert("No Selection", "Please select a row to delete.");
        }
    }


    private void updateTotalProfit() {
        double totalProfit = 0.0;
        for (TaxDataRecord record : tableView.getItems()) {
            totalProfit += record.getProfit();
        }
        totalProfitLabel.setText("Total Profit: " + String.format("%.2f", totalProfit));
    }

    @FXML
    private void generateTax() {
        try {
            double taxRate = Double.parseDouble(taxInput.getText());
            String profitText = totalProfitLabel.getText().replace("Total Profit: ", "");
            double totalProfit = Double.parseDouble(profitText);

            double taxAmount = totalProfit * (taxRate / 100);
            taxLabel.setText(String.format("%.2f", taxAmount));
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid tax rate.");
        }
    }


    @FXML
    private void initialize() {
        setupTable();
    }

    public void setUsertype(String usertype) {
        if (usertype.equals("user")) {
            deleteButton.setVisible(false);
            billNoColumn.setEditable(false);
            itemCodeColumn.setEditable(false);
            internalPriceColumn.setEditable(false);
            salesPriceColumn.setEditable(false);
            discountColumn.setEditable(false);
            qtyColumn.setEditable(false);
            validColumn.setEditable(false);
            tableView.setEditable(false);
        }
    }

    private void setupTable() {
        tableView.setEditable(true);

        billNoColumn.setCellValueFactory(new PropertyValueFactory<>("billNo"));
        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        internalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("internalPrice"));
        salesPriceColumn.setCellValueFactory(new PropertyValueFactory<>("salesPrice"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("discount"));
        qtyColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        validColumn.setCellValueFactory(new PropertyValueFactory<>("valid"));

        itemCodeColumn.setCellFactory(column -> {
            TextFieldTableCell<TaxDataRecord, String> cell = new TextFieldTableCell<>(new javafx.util.converter.DefaultStringConverter()) {
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if (!item.matches("^[a-zA-Z0-9]+$")) {
                            setStyle("-fx-background-color: lightcoral; -fx-text-fill: white;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
            return cell;
        });

        itemCodeColumn.setOnEditCommit(event -> handleEditCommit(event, "itemCode"));

        internalPriceColumn.setCellFactory(column -> {
            TextFieldTableCell<TaxDataRecord, Double> cell = new TextFieldTableCell<>(new DoubleStringConverter()) {
                @Override
                public void startEdit() {
                    super.startEdit();
                    if (getGraphic() instanceof TextField textField) {
                        textField.setTextFormatter(new TextFormatter<>(change -> {
                            String newText = change.getControlNewText();
                            if (newText.matches("-?\\d*(\\.\\d*)?")) {
                                return change;
                            }
                            return null;
                        }));
                    }
                }

                @Override
                public void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.valueOf(item));
                        if (item < 0) {
                            setStyle("-fx-background-color: lightcoral; -fx-text-fill: white;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
            return cell;
        });

        internalPriceColumn.setOnEditCommit(event -> handleEditCommit(event, "internalPrice"));

        salesPriceColumn.setCellFactory(column -> {
            TextFieldTableCell<TaxDataRecord, Double> cell = new TextFieldTableCell<>(new DoubleStringConverter()) {
                @Override
                public void startEdit() {
                    super.startEdit();
                    if (getGraphic() instanceof TextField textField) {
                        textField.setTextFormatter(new TextFormatter<>(change -> {
                            String newText = change.getControlNewText();
                            if (newText.matches("-?\\d*(\\.\\d*)?")) {
                                return change;
                            }
                            return null;
                        }));
                    }
                }

                @Override
                public void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.valueOf(item));
                        if (item < 0) {
                            setStyle("-fx-background-color: lightcoral; -fx-text-fill: white;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
            return cell;
        });

        salesPriceColumn.setOnEditCommit(event -> handleEditCommit(event, "salesPrice"));
        discountColumn.setCellFactory(column -> {
            TextFieldTableCell<TaxDataRecord, Double> cell = new TextFieldTableCell<>(new DoubleStringConverter()) {
                @Override
                public void startEdit() {
                    super.startEdit();
                    if (getGraphic() instanceof TextField textField) {
                        textField.setTextFormatter(new TextFormatter<>(change -> {
                            String newText = change.getControlNewText();
                            if (newText.matches("-?\\d*(\\.\\d*)?")) {
                                return change;
                            }
                            return null;
                        }));
                    }
                }

                @Override
                public void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.valueOf(item));
                        if (item < 0) {
                            setStyle("-fx-background-color: lightcoral; -fx-text-fill: white;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
            return cell;
        });

        discountColumn.setOnEditCommit(event -> handleEditCommit(event, "discount"));
        qtyColumn.setCellFactory(column -> {
            TextFieldTableCell<TaxDataRecord, Integer> cell = new TextFieldTableCell<>(new IntegerStringConverter()) {
                @Override
                public void startEdit() {
                    super.startEdit();
                    if (getGraphic() instanceof TextField textField) {
                        textField.setTextFormatter(new TextFormatter<>(change -> {
                            String newText = change.getControlNewText();
                            if (newText.matches("\\d*")) {
                                return change;
                            }
                            return null;
                        }));
                    }
                }

                @Override
                public void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.valueOf(item));
                        if (item < 0) {
                            setStyle("-fx-background-color: lightcoral; -fx-text-fill: white;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
            return cell;
        });

        qtyColumn.setOnEditCommit(event -> handleEditCommit(event, "quantity"));

        updateTotalProfit();
    }

    private void handleEditCommit(TableColumn.CellEditEvent<TaxDataRecord, ?> event, String fieldName) {
        TaxDataRecord record = event.getRowValue();

        if ("False".equalsIgnoreCase(record.getValid())) {
            switch (fieldName) {
                case "itemCode":
                    record.setItemCode((String) event.getNewValue());
                    break;
                case "discount":
                    record.setDiscount((Double) event.getNewValue());
                    break;
                case "salesPrice":
                    record.setSalesPrice((Double) event.getNewValue());
                    break;
                case "internalPrice":
                    record.setInternalPrice((Double) event.getNewValue());
                    break;
                case "quantity":
                    record.setQuantity((Integer) event.getNewValue());
                    break;
                default:
                    System.out.println("Unknown field: " + fieldName);
            }

            record.recalculateProfit();
            record.validateChecksum(tableView.getItems());
            updateTotalProfit();
            updateSummaryLabels();
            tableView.refresh();
        } else {
            tableView.refresh();
            showAlert("Editing Not Allowed", "Only invalid records can be edited.");
        }
    }

    @FXML
    private void onOpenFileClick() {
        String filePath = filePathField.getText();

        if (filePath == null || filePath.trim().isEmpty()) {
            statusLabel.setText("Please enter a file path.");
            return;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            statusLabel.setText("File not found!");
            return;
        }

        try {
            List<TaxDataRecord> records = TransactionFileReader.readTransactionFile(file);
            if (records.isEmpty()) {
                statusLabel.setText("No data found in file.");
                clearSummaryLabels();
            } else {
                statusLabel.setText("File read successfully: " + filePath);
                ObservableList<TaxDataRecord> data = FXCollections.observableArrayList(records);
                tableView.setItems(data);
                updateTotalProfit();
                updateSummaryLabels();
            }
        } catch (Exception e) {
            statusLabel.setText("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void updateSummaryLabels() {
        ObservableList<TaxDataRecord> records = tableView.getItems();
        int totalRecords = records.size();
        long validRecords = records.stream().filter(r -> "True".equalsIgnoreCase(r.getValid())).count();
        long invalidRecords = totalRecords - validRecords;

        totalRecLabel.setText("Total Records: " + totalRecords);
        totalValidLabel.setText("Valid Records: " + validRecords);
        totalInvalidLabel.setText("Invalid Records: " + invalidRecords);
    }


    private void clearSummaryLabels() {
        totalRecLabel.setText("Total Records: 0");
        totalValidLabel.setText("Valid Records: 0");
        totalInvalidLabel.setText("Invalid Records: 0");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
