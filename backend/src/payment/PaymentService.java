package payment;

import interfaces.Payment;

public class PaymentService {
    public void processPayment(Payment paymentMethod, double amount) {
        paymentMethod.processPayment(amount);
    }

    public double applyDiscount(double amount, double discount) {
        return amount - (amount * discount / 100);
    }

    public static void printReceipt(String message) {
        System.out.println("===== NOTA FISCAL =====");
        System.out.println("-----------------------");
        System.out.println(message);
        System.out.println("-----------------------");
        System.out.println("=======================");
    }
}
