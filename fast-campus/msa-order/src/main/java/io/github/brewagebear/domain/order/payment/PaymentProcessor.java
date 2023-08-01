package io.github.brewagebear.domain.order.payment;

import io.github.brewagebear.domain.order.Order;
import io.github.brewagebear.domain.order.OrderCommand;

public interface PaymentProcessor {
    void pay(Order order, OrderCommand.PaymentRequest request);
}
