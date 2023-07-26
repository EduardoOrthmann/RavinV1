package enums;

import interfaces.Payment;
import payment.CashPayment;
import payment.CreditCardPayment;
import payment.DebitCardPayment;

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
