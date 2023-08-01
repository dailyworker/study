package io.github.brewagebear.infrastructure.order;

import io.github.brewagebear.domain.order.item.OrderItemOption;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemOptionRepository extends JpaRepository<OrderItemOption, Long> {
}
