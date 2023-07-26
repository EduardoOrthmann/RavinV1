package domains.payment;

import interfaces.Payment;

public class DebitCardPayment implements Payment {
    @Override
    public void processPayment(double amount, double amountToPay) {
        if (amount < amountToPay) {
            throw new IllegalArgumentException("Valor insuficiente");
        }
    }
}
