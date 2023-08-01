package io.github.brewagebear.domain.order.payment.validator;

import io.github.brewagebear.domain.order.Order;
import io.github.brewagebear.domain.order.OrderCommand;

public interface PaymentValidator {
    void validate(Order order, OrderCommand.PaymentRequest paymentRequest);
}
