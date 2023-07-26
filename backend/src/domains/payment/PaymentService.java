package domains.payment;

import interfaces.Payment;

public class PaymentService {
    public void processPayment(Payment paymentMethod, double amount, double amountToPay) {
        paymentMethod.processPayment(amount, amountToPay);
    }

    public double applyDiscount(double amount, double discount) {
        return amount - (amount * discount / 100);
    }
}
