package io.github.brewagebear.infrastructure.order;

import io.github.brewagebear.domain.order.item.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
