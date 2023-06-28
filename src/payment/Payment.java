package payment;

import enums.PaymentMethod;

public class Payment {
    private PaymentMethod paymentMethod;
    private double totalPrice;

    public Payment(PaymentMethod paymentMethod, double totalPrice) {
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
