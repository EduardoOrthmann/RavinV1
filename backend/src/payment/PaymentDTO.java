package payment;

import enums.PaymentMethodFactory;

public record PaymentDTO(
        PaymentMethodFactory paymentMethodFactory,
        double amount
) {
}
