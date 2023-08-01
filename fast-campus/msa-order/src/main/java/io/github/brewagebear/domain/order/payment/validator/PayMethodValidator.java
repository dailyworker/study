package io.github.brewagebear.domain.order.payment.validator;


import io.github.brewagebear.common.exception.InvalidParamException;
import io.github.brewagebear.domain.order.Order;
import io.github.brewagebear.domain.order.OrderCommand;
import org.springframework.stereotype.Component;

@Component
@org.springframework.core.annotation.Order(2)
public class PayMethodValidator implements PaymentValidator {
    @Override
    public void validate(Order order, OrderCommand.PaymentRequest paymentRequest) {
        if(!order.getPayMethod().equals(paymentRequest.payMethod().name())) {
            throw new InvalidParamException("주문 과정에서의 결제수단이 다릅니다.");
        }
    }
}
