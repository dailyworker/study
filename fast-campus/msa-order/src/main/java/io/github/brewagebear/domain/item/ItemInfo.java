package io.github.brewagebear.domain.item;

import io.github.brewagebear.domain.item.option.ItemOption;
import io.github.brewagebear.domain.item.optiongroup.ItemOptionGroup;

import java.util.List;

public class ItemInfo {
    public record Main(
            String itemToken,
            Long partnerId,
            String itemName,
            Long itemPrice,
            Item.Status status,
            List<ItemOptionGroupInfo> itemOptionGroupInfos
    ) {
        public Main(Item item, List<ItemOptionGroupInfo> itemOptionGroupInfoList) {
            this(item.getItemToken(),
                    item.getPartnerId(),
                    item.getItemName(),
                    item.getItemPrice(),
                    item.getStatus(),
                    itemOptionGroupInfoList);
        }
    }

    public record ItemOptionGroupInfo(
            Integer ordering,
            String itemOptionGroupName,
            List<ItemOptionInfo> itemOptionInfos
    ) {
        public ItemOptionGroupInfo(ItemOptionGroup itemOptionGroup, List<ItemOptionInfo> itemOptionInfos) {
            this(itemOptionGroup.getOrdering(), itemOptionGroup.getItemOptionGroupName(), itemOptionInfos);
        }
    }

    public record ItemOptionInfo(
            Integer ordering,
            String itemOptionName,
            Long itemOptionPrice
    ) {
        public ItemOptionInfo(ItemOption itemOption) {
            this(itemOption.getOrdering(), itemOption.getItemOptionName(), itemOption.getItemOptionPrice());
        }
    }
}
