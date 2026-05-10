package baccportal.controllers;

import baccportal.App;
import baccportal.model.services.PaymentService;
import baccportal.model.users.StudentUser;
import baccportal.model.utilities.PaymentInfo;
import baccportal.model.utilities.PaymentType;
import baccportal.model.utilities.Receipt;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class StudentPaymentController {

    @FXML private Label studentLabel;
    @FXML private Label balanceLabel;

    @FXML private TextField amountField;
    @FXML private ComboBox<PaymentType> paymentTypeBox;
    @FXML private TextField nameOnCardField;
    @FXML private TextField cardNumberField;

    @FXML private TextField expirationMonthField;
    @FXML private TextField expirationYearField;

    @FXML private PasswordField securityCodeField;
    @FXML private Label statusLabel;

    private StudentUser student;
    private final PaymentService paymentService = App.getAppData().getPaymentService();

    @FXML
    private void initialize() {
        var opt = App.getSession().student();

        if (opt.isPresent()) {
            student = opt.get();
            paymentTypeBox.getItems().setAll(PaymentType.CREDIT_CARD, PaymentType.DEBIT_CARD);
            setupCardNumberFormatting();
            setupExpirationFormatting();
            updateSummary();
        } else {
            statusLabel.setText("No student user is currently logged in.");
        }
    }

    @FXML
    private void handlePayment() {
        if (student == null) {
            statusLabel.setText("No student logged in.");
            return;
        }

        try {
            String amountText = amountField.getText().trim();
            String nameOnCard = nameOnCardField.getText().trim();
            PaymentType paymentType = paymentTypeBox.getValue();

            String cardNumberDisplay = cardNumberField.getText().trim();
            String cardNumber = cardNumberDisplay.replaceAll("\\s", "");

            String expirationMonth = expirationMonthField.getText().trim();
            String expirationYear = expirationYearField.getText().trim();
            String expirationDate = expirationMonth + "/" + expirationYear;

            String securityCode = securityCodeField.getText().trim();

            if (amountText.isBlank()
                    || nameOnCard.isBlank()
                    || paymentType == null
                    || cardNumber.isBlank()
                    || expirationMonth.isBlank()
                    || expirationYear.isBlank()
                    || securityCode.isBlank()) {
                statusLabel.setText("Fill all payment fields.");
                return;
            }

            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                statusLabel.setText("Payment amount must be greater than 0.");
                return;
            }

            PaymentInfo paymentInfo = new PaymentInfo(
                    nameOnCard,
                    cardNumber,
                    expirationDate,
                    paymentType,
                    securityCode
            );

            Receipt receipt = paymentService.processPayment(student, paymentInfo, amount);


            clearForm();
            updateSummary();

            statusLabel.setText(
                    "Payment successful. Receipt #" + receipt.getReceiptId()
                            + " | Paid: $" + String.format("%.2f", receipt.getTotalPaid())
                            + " | Remaining: $" + String.format("%.2f", receipt.getRemainingBalance())
            );

        } catch (NumberFormatException e) {
            statusLabel.setText("Amount must be a number.");
        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
        }
    }

    private void setupCardNumberFormatting() {
        cardNumberField.textProperty().addListener((observable, oldValue, newValue) -> {
            String digitsOnly = newValue.replaceAll("\\D", "");

            if (digitsOnly.length() > 19) {
                digitsOnly = digitsOnly.substring(0, 19);
            }

            StringBuilder formatted = new StringBuilder();

            for (int i = 0; i < digitsOnly.length(); i++) {
                if (i > 0 && i % 4 == 0) {
                    formatted.append(" ");
                }
                formatted.append(digitsOnly.charAt(i));
            }

            String formattedText = formatted.toString();

            if (!formattedText.equals(newValue)) {
                cardNumberField.setText(formattedText);
                cardNumberField.positionCaret(formattedText.length());
            }
        });
    }

    private void setupExpirationFormatting() {
        expirationMonthField.textProperty().addListener((observable, oldValue, newValue) -> {
            String digitsOnly = newValue.replaceAll("\\D", "");

            if (digitsOnly.length() > 2) {
                digitsOnly = digitsOnly.substring(0, 2);
            }

            if (!digitsOnly.equals(newValue)) {
                expirationMonthField.setText(digitsOnly);
                expirationMonthField.positionCaret(digitsOnly.length());
            }
        });

        expirationYearField.textProperty().addListener((observable, oldValue, newValue) -> {
            String digitsOnly = newValue.replaceAll("\\D", "");

            if (digitsOnly.length() > 2) {
                digitsOnly = digitsOnly.substring(0, 2);
            }

            if (!digitsOnly.equals(newValue)) {
                expirationYearField.setText(digitsOnly);
                expirationYearField.positionCaret(digitsOnly.length());
            }
        });
    }

    private void updateSummary() {
        studentLabel.setText("Student: " + student.getFullName() + " (" + student.getUserId() + ")");
        balanceLabel.setText("Current Balance: $" + String.format("%.2f", student.getBalanceOwed()));
    }

    private void clearForm() {
        amountField.clear();
        nameOnCardField.clear();
        cardNumberField.clear();
        expirationMonthField.clear();
        expirationYearField.clear();
        securityCodeField.clear();
        paymentTypeBox.setValue(null);
    }
}