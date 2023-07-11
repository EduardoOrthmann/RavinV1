package payment;

import interfaces.Payment;

public class DebitCardPayment implements Payment {
    @Override
    public void processPayment(double amount) {
        PaymentService.printReceipt("Processando pagamento por cartão de débito de R$ " + amount);
    }
}
