package baccportal.controllers;

import baccportal.model.session.Session;
import baccportal.model.users.StudentUser;
import baccportal.model.utilities.Receipt;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableCell;

public class StudentTransactionsController {

    @FXML private TableView<Receipt> transactionTable;
    @FXML private TableColumn<Receipt, Number> receiptIdColumn;
    @FXML private TableColumn<Receipt, Number> paidColumn;
    @FXML private TableColumn<Receipt, Number> remainingBalanceColumn;
    @FXML private Label statusLabel;

    private StudentUser student;
    private final Session session;

    public StudentTransactionsController(Session session) {
        this.session = session;
    }

    @FXML
    private void initialize() {
        student = session.student();

        if (student != null) {
            setupTable();
            loadTransactions();
        } else {
            statusLabel.setText("No student user is currently logged in.");
        }
    }

    private void setupTable() {
        receiptIdColumn.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getReceiptId())
        );

        paidColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getTotalPaid())
        );

        remainingBalanceColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getRemainingBalance())
        );

        paidColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                } else {
                    setText("$" + String.format("%.2f", value.doubleValue()));
                }
            }
        });

        remainingBalanceColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);

                if (empty || value == null) {
                    setText(null);
                } else {
                    setText("$" + String.format("%.2f", value.doubleValue()));
                }
            }
        });

        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void loadTransactions() {
        transactionTable.getItems().setAll(student.getTransactionHistory());

        if (student.getTransactionHistory().isEmpty()) {
            statusLabel.setText("No transactions yet.");
        } else {
            statusLabel.setText("");
        }
    }
}