package io.github.brewagebear.application.partner;

import io.github.brewagebear.domain.notification.NotificationService;
import io.github.brewagebear.domain.partner.PartnerCommand;
import io.github.brewagebear.domain.partner.PartnerInfo;
import io.github.brewagebear.domain.partner.PartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerFacade {
    private final PartnerService partnerService;
    private final NotificationService notificationService;

    public PartnerInfo registerPartner(PartnerCommand command) {
        var partnerInfo = partnerService.registerPartner(command);
        notificationService.sendEmail(partnerInfo.email(), "title", "description");
        return partnerInfo;
    }
}
