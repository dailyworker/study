package io.github.brewagebear.infrastructure.item.optiongroup;

import io.github.brewagebear.domain.item.optiongroup.ItemOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemOptionGroupRepository extends JpaRepository<ItemOptionGroup, Long> {
}
