package io.github.brewagebear.domain.partner;

import lombok.Builder;

@Builder
public record PartnerCommand(
        String partnerName,
        String businessNo,
        String email
) {
    public Partner toEntity() {
        return Partner.builder()
                .partnerName(partnerName)
                .businessNo(businessNo)
                .email(email)
                .build();
    }
}
