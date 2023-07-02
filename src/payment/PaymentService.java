package payment;

import enums.PaymentMethod;

public class PaymentService {
    public void processPayment(PaymentMethod paymentMethod, double amount) {
        switch (paymentMethod) {
            case CASH -> processCashPayment(amount);
            case CREDIT_CARD -> processCreditCardPayment(amount);
            case DEBIT_CARD -> processDebitCardPayment(amount);
            default -> System.out.println("Tipo de pagamento inválido");
        }
    }

    private void processCashPayment(double amount) {
        printReceipt("Processando pagamento em dinheiro de R$ " + amount);
    }

    private void processCreditCardPayment(double amount) {
        printReceipt("Processando pagamento por cartão de crédito de R$ " + amount);
    }

    private void processDebitCardPayment(double amount) {
        printReceipt("Processando pagamento por cartão de débito de R$ " + amount);
    }

    public double applyDiscount(double amount, double discount) {
        return amount - (amount * discount / 100);
    }

    private void printReceipt(String message) {
        System.out.println("===== NOTA FISCAL =====");
        System.out.println("-----------------------");
        System.out.println(message);
        System.out.println("-----------------------");
        System.out.println("=======================");
    }
}
