package domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {

    public void sendSms(String phone, String message) {
        // заглушка: вместо реальной отправки просто логируем,
        //т.к. для этого требуется реальный клиент, SDK, и API
        log.info("SMS на {}: {}", phone, message);
    }
}
