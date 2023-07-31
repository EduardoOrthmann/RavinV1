package domains.payment;

import enums.PaymentMethodFactory;

public record PaymentDTO(
        PaymentMethodFactory paymentMethod,
        double amount
) {
}
