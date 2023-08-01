package io.github.brewagebear.infrastructure.order.payment;

import io.github.brewagebear.domain.order.OrderCommand;
import io.github.brewagebear.domain.order.payment.PayMethod;

public interface PaymentApiCaller {
    boolean support(PayMethod payMethod);
    void pay(OrderCommand.PaymentRequest request);
}
