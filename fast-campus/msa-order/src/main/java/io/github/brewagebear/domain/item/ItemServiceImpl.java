package io.github.brewagebear.domain.item;

import io.github.brewagebear.domain.partner.PartnerReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final PartnerReader partnerReader;
    private final ItemStore itemStore;
    private final ItemReader itemReader;
    private final ItemOptionsSeriesFactory itemOptionsSeriesFactory;

    @Override
    @Transactional
    public String registerItem(ItemCommand.RegisterItemRequest command, String partnerToken) {
        var partner = partnerReader.getPartner(partnerToken);
        var initItem = command.toEntity(partner.getId());
        var item = itemStore.store(initItem);
        itemOptionsSeriesFactory.store(command, item);
        return item.getItemToken();
    }

    @Override
    @Transactional
    public void changeOnSale(String itemToken) {
        var item = itemReader.getItemBy(itemToken);
        item.changeOnSale();
    }

    @Override
    @Transactional
    public void changeEndOfSale(String itemToken) {
        var item = itemReader.getItemBy(itemToken);
        item.changeEndOfSale();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemInfo.Main retrieveItemInfo(String itemToken) {
        return null;
    }
}
