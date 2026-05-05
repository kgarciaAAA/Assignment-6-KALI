package services;

import users.StudentUser;
import utilities.PaymentInfo;
import utilities.Receipt;

public class PaymentService {

    public double checkStudentBalance(StudentUser user) {
        return user.getBalanceOwed();
    }

    /**
     * adds charge to students account after enrolling into a class.
     * "THIS METHOD SHOULD BE CALLED IN CONTROLLER, USE AN IF STATEMENT TO VERIFY THE STUDENT 
     * CORRECTLY ENROLLED THEN CALL CHARGEFORCOURSE()"
     * @param user reference to a student user child
     * @param amount a double containing price for a course
    
     */
    public void addCharge(StudentUser user, double amount) { 
        user.adjustBalanceOwed(amount);
    }

    public Receipt processPayment(StudentUser user, PaymentInfo paymentInfo, double amount) throws IllegalArgumentException{
        if (paymentInfo == null) {
            throw new IllegalArgumentException("Invalid Payment Information."); //can do some kind of system to validate the paymentInfo
        }

        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid Payment Amount.");
        }

        if (user.getBalanceOwed() < amount) {
            throw new IllegalArgumentException("Requested Payment is greater than Current Balance");
        }

        user.adjustBalanceOwed(-amount);
        Receipt newReceipt = new Receipt(amount, user.getBalanceOwed());
        user.addTransaction(newReceipt);
        return newReceipt; //can return a generated receipt ID, or can make receipt object
    }
}
