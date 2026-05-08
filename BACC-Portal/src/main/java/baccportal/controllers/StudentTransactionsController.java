package baccportal.controllers;

import baccportal.App;
import baccportal.model.users.StudentUser;
import baccportal.model.users.User;
import baccportal.model.utilities.Receipt;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class StudentTransactionsController {

    @FXML private ListView<Receipt> transactionListView;
    @FXML private Label statusLabel;

    private StudentUser student;

    @FXML
    private void initialize() {
        User user = App.getCurrentUser();

        if (user instanceof StudentUser) {
            student = (StudentUser) user;
            loadTransactions();
        } else {
            statusLabel.setText("No student user is currently logged in.");
        }
    }

    private void loadTransactions() {
        transactionListView.getItems().setAll(student.getTransactionHistory());

        if (student.getTransactionHistory().isEmpty()) {
            statusLabel.setText("No transactions yet.");
        }
    }
}