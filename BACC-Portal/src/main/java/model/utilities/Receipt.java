package utilities;

import java.time.LocalDateTime;

public class Receipt {
    private static int nextId = 100000;
    private final int receiptId;
    private final double totalPaid;
    private final double remainingBalance; //stores the remaining balance of the the student at the time the receipt was processed
    private final LocalDateTime timestamp;

    public Receipt(double totalPaid, double remainingBalance) {
        receiptId = nextId++;
        this.totalPaid = totalPaid;
        this.remainingBalance = remainingBalance;
        timestamp = LocalDateTime.now();
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

}
