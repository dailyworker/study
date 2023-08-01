package io.github.brewagebear.domain.order.payment.validator;


import io.github.brewagebear.common.exception.InvalidParamException;
import io.github.brewagebear.domain.order.OrderCommand;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class PayAmountValidator implements PaymentValidator {
    @Override
    public void validate(io.github.brewagebear.domain.order.Order order, OrderCommand.PaymentRequest paymentRequest) {
        if(!order.calculateTotalAmount().equals(paymentRequest.amount())) {
            throw new InvalidParamException("주문가격이 불일치합니다.");
        }
    }
}
