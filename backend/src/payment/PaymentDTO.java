package payment;

import enums.PaymentMethod;

public record PaymentDTO(
        PaymentMethod paymentMethod,
        double amount
) {
}
