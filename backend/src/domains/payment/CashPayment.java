package domains.payment;

import interfaces.Payment;

public class CashPayment implements Payment {
    @Override
    public void processPayment(double amount, double amountToPay) {
        if (amount < amountToPay) {
            throw new IllegalArgumentException("Valor insuficiente");
        }
    }
}
