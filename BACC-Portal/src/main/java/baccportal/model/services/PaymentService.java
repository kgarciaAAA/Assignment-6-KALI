package baccportal.model.services;

import baccportal.model.data.PersistencePort;
import baccportal.model.users.StudentUser;
import baccportal.model.utilities.PaymentInfo;
import baccportal.model.utilities.Receipt;

public class PaymentService {

    private final PersistencePort persistence;

    public PaymentService(PersistencePort persistence) {
        this.persistence = persistence;
    }

    public double checkStudentBalance(StudentUser user) {
        return user.getBalanceOwed();
    }

    // Validates and applies payment toward a student's outstanding balance. Throws an exception if the payment is invalid.
    public Receipt processPayment(StudentUser user, PaymentInfo paymentInfo, double amount) throws IllegalArgumentException {
        if (paymentInfo == null || amount <= 0 || user.getBalanceOwed() < amount)
            throw new IllegalArgumentException("Invalid Payment Information.");

        user.decrementBalanceOwed(amount);
        Receipt newReceipt = new Receipt(amount, user.getBalanceOwed());
        user.addTransaction(newReceipt);
        persistence.saveUsers();
        return newReceipt;
    }

    public void processRefund(StudentUser user, double amount) {
        user.decrementBalanceOwed(amount);
        persistence.saveUsers();    
    }

    public void processEnrollmentFee(StudentUser user, double amount) {
        user.incrementBalanceOwed(amount);
        persistence.saveUsers();
    }
}
