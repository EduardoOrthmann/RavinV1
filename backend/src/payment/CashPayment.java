package payment;

import interfaces.Payment;

public class CashPayment implements Payment {
    @Override
    public void processPayment(double amount) {
        PaymentService.printReceipt("Processando pagamento em dinheiro de R$ " + amount);
    }
}
