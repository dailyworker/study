package io.github.brewagebear.infrastructure.order;

import io.github.brewagebear.domain.order.item.OrderItemOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemOptionGroupRepository extends JpaRepository<OrderItemOptionGroup, Long> {
}
