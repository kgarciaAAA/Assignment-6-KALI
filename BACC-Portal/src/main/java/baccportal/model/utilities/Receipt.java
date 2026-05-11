package baccportal.model.utilities;

public class Receipt {
    // Seeded to 100000 so new receipt ids stay six digits. The data-loading
    // constructor below pushes this past any id read from disk on startup.
    private static int nextId = 100000;
    private final int receiptId;
    private final double totalPaid;
    // Remaining balance of the student at the time the receipt was processed.
    private final double remainingBalance;

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
