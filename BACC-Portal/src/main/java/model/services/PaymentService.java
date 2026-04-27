package services;

import users.StudentUser;
import utilities.PaymentInfo;

public class PaymentService { //potentially add a receipt system?
    
    public double checkStudentBalance(StudentUser user) {
        return user.getBalance();
    }

    public void addStudentBalance(StudentUser user, double amount) {
        user.adjustBalance(amount);
    }

    public boolean processPayment(StudentUser user, PaymentInfo paymentInfo, double amount) {
        if (paymentInfo == null) {
            throw new IllegalArgumentException("Invalid Payment Information."); //can do some kind of system to validate the paymentInfo
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid Payment Amount.");
        }

        if (user.getBalance() < amount) {
            throw new IllegalArgumentException("Requested Payment is greater than Current Balance");
        }

        user.adjustBalance(-amount);

        return true; //can return a generated receipt ID, or can make receipt object
    }
}
