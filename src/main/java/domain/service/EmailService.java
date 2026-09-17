package domain.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine; //для HTML писем

    @Value("${shop.email.from}")
    private String fromEmail;

    public void sendOrderConfirmation(String accountEmail, Long orderId, BigDecimal totalAmount) {
        log.info("Отправка email на {}: заказ {}, сумма {}", accountEmail, orderId, totalAmount);

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(accountEmail);
            message.setSubject("Подтверждение заказа #" + orderId);
            message.setText(String.format(
                    "Здравствуйте!\n\n" +
                            "Ваш заказ #%d успешно оформлен.\n" +
                            "Сумма заказа: %.2f руб.\n\n" +
                            "Спасибо за покупку!",
                    orderId, totalAmount
            ));

            mailSender.send(message);
            log.info("Email успешно отправлен на {}", accountEmail);
        } catch (Exception e) {
            log.error("Ошибка отправки email: ", e);
            throw new RuntimeException("Не удалось отправить email: ", e);
        }
    }

    public void sentOrderConfirmationHtml(String accountEmail, Long orderId, BigDecimal totalAmount) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(accountEmail);
            helper.setSubject("Подтверждение заказа #" + orderId);

            //генерим html из шаблона
            Context context = new Context();
            context.setVariable("orderId", orderId);
            context.setVariable("totalAmount", totalAmount);
            context.setVariable("orderDate", LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            ));


            String htmlContent = templateEngine.process("email/order-confirmation", context);
            helper.setText(htmlContent, true);
            mailSender.send(message);

            log.info("Email отправлен на {}", accountEmail);
        } catch (MessagingException e) {
            log.error("Ошибка отправки на email: {}, ошибка: {}", accountEmail, e.getMessage());
            throw new RuntimeException("Не удалось отправить email", e);
        }
    }
}
