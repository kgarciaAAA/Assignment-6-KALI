package utilities;

public class PaymentInfo {
    private final String nameOnCard;
    private final String cardNumber;
    private final String expirationDate;
    private final PaymentType paymentType;
    private final String securityCode;

    //constructors
    public PaymentInfo(String nameOnCard, String cardNumber, String expirationDate, PaymentType paymentType, String securityCode) {
        if (nameOnCard == null || nameOnCard.isEmpty()) {
            throw new IllegalArgumentException("Name On Card is Empty.");
        } 

        if (!cardNumber.matches("\\d{13,19}")) { //checks for digits 0-9 for lengths 13-19
            throw new IllegalArgumentException ("Invalid Card Length");
        }

        if (!expirationDate.matches("\\d{2}/\\d{2}")) { //checks for digits 0-9 and verifies format MM/YY
            throw new IllegalArgumentException("Invalid Expiration Date");
        }

        if (!securityCode.matches("\\d{3,4}")) { //checks for digits 0-9 and lengths 3-4
            throw new IllegalArgumentException("Invalid Security Code");
        }

        this.nameOnCard = nameOnCard;
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.paymentType = paymentType;
        this.securityCode = securityCode;
    }

    //getters
    public String getNameOnCard() {
        return nameOnCard;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public String getSecurityCode() {
        return securityCode;
    }


}
