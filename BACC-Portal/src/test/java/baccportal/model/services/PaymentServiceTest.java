package baccportal.model.services;

import baccportal.model.academics.Department;
import baccportal.model.data.PersistencePort;
import baccportal.model.users.StudentUser;
import baccportal.model.utilities.PaymentInfo;
import baccportal.model.utilities.PaymentType;
import baccportal.model.utilities.Receipt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {
    private PaymentService paymentService;
    private StudentUser student;
    private PaymentInfo validPaymentInfo;
    private FakePersistence persistence;

    @BeforeEach
    void setUp() {
        persistence = new FakePersistence();
        paymentService = new PaymentService(persistence);

        student = new StudentUser(
                "student@sjsu.edu",
                "S1001",
                "password123",
                "Test Student",
                false,
                "Computer Science",
                Department.COMPUTER_SCIENCE,
                1000.0
        );

        validPaymentInfo = new PaymentInfo(
                "Test Student",
                "4111111111111111",
                "12/30",
                PaymentType.CREDIT_CARD,
                "123"
        );
    }

    @Test
    void checkStudentBalanceReturnsCurrentBalance() {
        assertEquals(1000.0, paymentService.checkStudentBalance(student), 0.001);
    }

    @Test
    void processEnrollmentFeeIncreasesStudentBalanceAndSaves() {
        paymentService.processEnrollmentFee(student, 250.0);

        assertEquals(1250.0, student.getBalanceOwed(), 0.001);
        assertEquals(1, persistence.saveUsersCalls);
    }

    @Test
    void processRefundReducesStudentBalanceAndSaves() {
        paymentService.processRefund(student, 250.0);

        assertEquals(750.0, student.getBalanceOwed(), 0.001);
        assertEquals(1, persistence.saveUsersCalls);
    }

    @Test
    void processPaymentReducesBalanceAndCreatesReceiptAndSaves() {
        Receipt receipt = paymentService.processPayment(student, validPaymentInfo, 400.0);

        assertEquals(600.0, student.getBalanceOwed(), 0.001);
        assertEquals(400.0, receipt.getTotalPaid(), 0.001);
        assertEquals(600.0, receipt.getRemainingBalance(), 0.001);
        assertEquals(1, student.getTransactionHistory().size());
        assertTrue(student.getTransactionHistory().contains(receipt));
        assertEquals(1, persistence.saveUsersCalls);
    }

    @Test
    void processPaymentRejectsNullPaymentInfo() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.processPayment(student, null, 100.0)
        );

        assertEquals("Invalid Payment Information.", exception.getMessage());
    }

    @Test
    void processPaymentRejectsZeroOrNegativeAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment(student, validPaymentInfo, 0.0));

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.processPayment(student, validPaymentInfo, -25.0));
    }

    @Test
    void processPaymentRejectsAmountGreaterThanBalance() {
        assertThrows(
                IllegalArgumentException.class,
                () -> paymentService.processPayment(student, validPaymentInfo, 1500.0)
        );

        assertEquals(1000.0, student.getBalanceOwed(), 0.001);
        assertTrue(student.getTransactionHistory().isEmpty());
    }

    @Test
    void paymentInfoRejectsInvalidCardFields() {
        assertThrows(IllegalArgumentException.class,
                () -> new PaymentInfo("", "4111111111111111", "12/30", PaymentType.CREDIT_CARD, "123"));

        assertThrows(IllegalArgumentException.class,
                () -> new PaymentInfo("Test Student", "123", "12/30", PaymentType.CREDIT_CARD, "123"));

        assertThrows(IllegalArgumentException.class,
                () -> new PaymentInfo("Test Student", "4111111111111111", "1230", PaymentType.CREDIT_CARD, "123"));

        assertThrows(IllegalArgumentException.class,
                () -> new PaymentInfo("Test Student", "4111111111111111", "12/30", PaymentType.CREDIT_CARD, "12"));
    }

    private static class FakePersistence implements PersistencePort {
        int saveUsersCalls;

        @Override
        public void saveUsers() {
            saveUsersCalls++;
        }

        @Override
        public void saveCourses() {
        }

        @Override
        public void saveSections() {
        }
    }
}
