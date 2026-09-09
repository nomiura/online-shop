package domain.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
public class EmailService {
    public void sendOrderConfirmation(String accountEmail, Long orderId, BigDecimal totalAmount) {
        //log.info("Отправка email на {}: заказ {}, сумма {}", accountEmail, orderId, totalAmount);


        //TODO ээээ логику реализовать, подключить депенденсис итд
//        <!-- Для отправки email через SMTP -->
//                <dependency>
//                <groupId>org.springframework.boot</groupId>
//                <artifactId>spring-boot-starter-mail</artifactId>
//                </dependency>
//
//                <!-- Для красивых HTML писем (опционально) -->
//                <dependency>
//                <groupId>org.springframework.boot</groupId>
//                <artifactId>spring-boot-starter-thymeleaf</artifactId>
//                </dependency>

//# ============================
//# EMAIL CONFIGURATION
//# ============================
//
//# Для Gmail
//        spring.mail.host=smtp.gmail.com
//        spring.mail.port=587
//        spring.mail.username=your-email@gmail.com      # 👈 ТВОЙ EMAIL
//        spring.mail.password=your-app-password         # 👈 НЕ пароль, а APP PASSWORD!
//                spring.mail.properties.mail.smtp.auth=true
//        spring.mail.properties.mail.smtp.starttls.enable=true
//        spring.mail.properties.mail.smtp.starttls.required=true
//        spring.mail.properties.mail.smtp.connectiontimeout=5000
//        spring.mail.properties.mail.smtp.timeout=5000
//        spring.mail.properties.mail.smtp.writetimeout=5000
//
//# От кого будут приходить письма
//        shop.email.from=noreply@shop.com
        //log.info("Email успешно отправлен");
    }
}
