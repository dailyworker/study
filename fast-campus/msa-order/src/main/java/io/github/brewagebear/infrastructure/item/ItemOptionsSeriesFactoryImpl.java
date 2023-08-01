package io.github.brewagebear.infrastructure.item;

import io.github.brewagebear.domain.item.Item;
import io.github.brewagebear.domain.item.ItemCommand;
import io.github.brewagebear.domain.item.ItemOptionsSeriesFactory;
import io.github.brewagebear.domain.item.option.ItemOptionStore;
import io.github.brewagebear.domain.item.optiongroup.ItemOptionGroup;
import io.github.brewagebear.domain.item.optiongroup.ItemOptionGroupStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemOptionsSeriesFactoryImpl implements ItemOptionsSeriesFactory {
    private final ItemOptionGroupStore itemOptionGroupStore;
    private final ItemOptionStore itemOptionStore;

    @Override
    public List<ItemOptionGroup> store(ItemCommand.RegisterItemRequest command, Item item) {
        var itemOptionGroupRequests = command.itemOptionGroupRequestList();
        if(CollectionUtils.isEmpty(itemOptionGroupRequests)) return Collections.emptyList();

        return itemOptionGroupRequests.stream()
                .map(requestItemOptionGroup -> {
                    var initItemOptionGroup = requestItemOptionGroup.toEntity(item);
                    var itemOptionGroup = itemOptionGroupStore.store(initItemOptionGroup);

                    requestItemOptionGroup.itemOptionRequestList()
                            .forEach(requestItemOption -> {
                                var initItemOption = requestItemOption.toEntity(itemOptionGroup);
                                itemOptionStore.store(initItemOption);
                            });
                    return itemOptionGroup;
                })
                .collect(Collectors.toList());
    }
}
