package io.github.brewagebear.domain.partner;

public record PartnerInfo(
        Long id,
        String partnerToken,
        String partnerName,
        String businessNo,
        String email,
        Partner.Status status
) {
    public PartnerInfo(Partner partner) {
        this(partner.getId(),
                partner.getPartnerToken(),
                partner.getPartnerName(),
                partner.getBusinessNo(),
                partner.getEmail(),
                partner.getStatus());
    }
}
