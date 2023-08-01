package io.github.brewagebear.domain.order.payment.validator;

import io.github.brewagebear.common.exception.InvalidParamException;
import io.github.brewagebear.domain.order.OrderCommand;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(3)
@Component
public class PayStatusValidator implements PaymentValidator {
    @Override
    public void validate(io.github.brewagebear.domain.order.Order order, OrderCommand.PaymentRequest paymentRequest) {
        if (order.isAlreadyPaymentComplete()) throw new InvalidParamException("이미 결제완료된 주문입니다");
    }
}
