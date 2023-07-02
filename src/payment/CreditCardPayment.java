package payment;

import interfaces.Payment;

public class CreditCardPayment implements Payment {
    @Override
    public void processPayment(double amount) {
        PaymentService.printReceipt("Processando pagamento por cartão de crédito de R$ " + amount);
    }
}
