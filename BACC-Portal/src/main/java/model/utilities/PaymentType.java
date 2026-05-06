package utilities;

public enum PaymentType {
    CREDIT_CARD, DEBIT_CARD;

    public static PaymentType stringToPaymentType(String paymentType) {
        switch (paymentType) {
            case "CREDIT_CARD" -> {
                return CREDIT_CARD;
            } case "DEBIT_CARD" -> {
                return DEBIT_CARD;
            }
            default -> {
                throw new IllegalArgumentException("Cannot convert string paymentType to enum PaymentType");
            }
            
        }
    }
}
