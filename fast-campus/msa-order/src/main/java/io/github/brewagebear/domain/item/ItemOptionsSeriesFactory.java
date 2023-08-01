package io.github.brewagebear.domain.item;

import io.github.brewagebear.domain.item.optiongroup.ItemOptionGroup;

import java.util.List;

public interface ItemOptionsSeriesFactory {
    List<ItemOptionGroup> store(ItemCommand.RegisterItemRequest request, Item item);
}
