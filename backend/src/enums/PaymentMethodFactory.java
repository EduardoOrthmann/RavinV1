package enums;

import domains.payment.CashPayment;
import domains.payment.CreditCardPayment;
import domains.payment.DebitCardPayment;
import interfaces.Payment;

public enum PaymentMethodFactory {
    CASH {
        @Override
        public Payment createPaymentMethod() {
            return new CashPayment();
        }
    },
    CREDIT_CARD {
        @Override
        public Payment createPaymentMethod() {
            return new CreditCardPayment();
        }
    },
    DEBIT_CARD {
        @Override
        public Payment createPaymentMethod() {
            return new DebitCardPayment();
        }
    };

    public abstract Payment createPaymentMethod();
}
