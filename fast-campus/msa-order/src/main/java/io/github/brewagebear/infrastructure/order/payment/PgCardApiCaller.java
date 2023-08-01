package io.github.brewagebear.infrastructure.order.payment;

import io.github.brewagebear.domain.order.OrderCommand;
import io.github.brewagebear.domain.order.payment.PayMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PgCardApiCaller implements PaymentApiCaller {
    @Override
    public boolean support(PayMethod payMethod) {
        return PayMethod.카드 == payMethod;
    }

    @Override
    public void pay(OrderCommand.PaymentRequest request) {
        //TODO
    }
}
