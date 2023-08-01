package io.github.brewagebear.domain.item;

import io.github.brewagebear.domain.item.optiongroup.ItemOptionGroup;
import io.github.brewagebear.domain.item.option.ItemOption;

import java.util.List;

public class ItemCommand {

    public record RegisterItemRequest(
            String itemName,
            Long itemPrice,
            List<RegisterItemOptionGroupRequest> itemOptionGroupRequestList
    ) {
        public Item toEntity(Long partnerId) {
            return Item.builder()
                    .partnerId(partnerId)
                    .itemName(itemName)
                    .itemPrice(itemPrice)
                    .build();
        }
    }

    public record RegisterItemOptionGroupRequest (
            Integer ordering,
            String itemOptionGroupName,
            List<RegisterItemOptionRequest> itemOptionRequestList
    ) {
        public ItemOptionGroup toEntity(Item item) {
            return ItemOptionGroup.builder()
                    .item(item)
                    .ordering(ordering)
                    .itemOptionGroupName(itemOptionGroupName)
                    .build();
        }
    }

    public record RegisterItemOptionRequest(
            Integer ordering,
            String itemOptionName,
            Long itemOptionPrice
    ) {
        public ItemOption toEntity(ItemOptionGroup itemOptionGroup) {
            return ItemOption.builder()
                    .itemOptionGroup(itemOptionGroup)
                    .ordering(ordering)
                    .itemOptionName(itemOptionName)
                    .itemOptionPrice(itemOptionPrice)
                    .build();
        }
    }
}
