package io.github.brewagebear.interfaces.partner;

import io.github.brewagebear.domain.partner.Partner;
import io.github.brewagebear.domain.partner.PartnerCommand;
import io.github.brewagebear.domain.partner.PartnerInfo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public class PartnerDto {
    public record RegisterRequest(
        @NotEmpty(message = "partnerName 는 필수값입니다") String partnerName,
        @NotEmpty(message = "businessNo 는 필수값입니다") String businessNo,
        @Email(message = "email 형식에 맞아야 합니다") @NotEmpty(message = "email 는 필수값입니다") String email
    ) {
        public PartnerCommand toCommand() {
            return PartnerCommand.builder()
                    .partnerName(partnerName)
                    .businessNo(businessNo)
                    .email(email)
                    .build();
        }
    }

    public record RegisterResponse(
        String partnerToken,
        String partnerName,
        String businessNo,
        String email,
        Partner.Status status
    ) {
        public RegisterResponse(PartnerInfo partnerInfo) {
            this(
                    partnerInfo.partnerToken(),
                    partnerInfo.partnerName(),
                    partnerInfo.businessNo(),
                    partnerInfo.email(),
                    partnerInfo.status()
            );
        }
    }
}
