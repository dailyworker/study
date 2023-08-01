package io.github.brewagebear.infrastructure.item;

import io.github.brewagebear.common.exception.EntityNotFoundException;
import io.github.brewagebear.domain.item.Item;
import io.github.brewagebear.domain.item.ItemInfo;
import io.github.brewagebear.domain.item.ItemReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemReaderImpl implements ItemReader {
    private final ItemRepository itemRepository;

    @Override
    public Item getItemBy(String itemToken) {
        return itemRepository.findByItemToken(itemToken)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Override
    public List<ItemInfo.ItemOptionGroupInfo> getItemOptionSeries(Item item) {
        var itemOptionGroups = item.getItemOptionGroups();
        return itemOptionGroups.stream()
                .map(itemOptionGroup -> {
                    var itemOptionList = itemOptionGroup.getItemOptionList();
                    var itemOptionInfos = itemOptionList.stream()
                            .map(ItemInfo.ItemOptionInfo::new)
                            .collect(Collectors.toList());

                    return new ItemInfo.ItemOptionGroupInfo(itemOptionGroup, itemOptionInfos);
                }).collect(Collectors.toList());
    }
}
