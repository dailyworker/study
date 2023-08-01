package io.github.brewagebear.infrastructure;

import io.github.brewagebear.domain.notification.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NorificationExecutor implements NotificationService {
    @Override
    public void sendEmail(String email, String title, String description) {
        log.info("sendMail");
    }

    @Override
    public void sendKakao(String phoneNo, String description) {
        log.info("sendKakao");
    }

    @Override
    public void sendSms(String phoneNo, String description) {
        log.info("sendSMS");
    }
}
