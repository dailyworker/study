package io.github.brewagebear.domain.order;

public interface OrderReader {
    Order getOrder(String orderToken);
}
