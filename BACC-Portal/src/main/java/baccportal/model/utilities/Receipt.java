package baccportal.model.utilities;

public class Receipt {
    private static int nextId = 100000; //have this replaced with data from a file
    private final int receiptId;
    private final double totalPaid;
    private final double remainingBalance; //stores the remaining balance of the the student at the time the receipt was processed

    //constructors
    public Receipt(double totalPaid, double remainingBalance) { //new receipt
        receiptId = nextId++;
        this.totalPaid = totalPaid;
        this.remainingBalance = remainingBalance;
    }

    public Receipt(int receiptId, double totalPaid, double remainingBalance) { //receipt from data
        this.receiptId = receiptId;
        this.totalPaid = totalPaid;
        this.remainingBalance = remainingBalance;
        nextId = Math.max(nextId, this.receiptId + 1);
    }

    //getters
    public int getReceiptId() {
        return receiptId;
    }

    public double getTotalPaid() {
        return totalPaid;
    }

    public double getRemainingBalance() {
        return remainingBalance;
    }

    @Override
    public String toString() {
        return this.receiptId + "-" + this.totalPaid + "-" + this.remainingBalance;
    }

}
