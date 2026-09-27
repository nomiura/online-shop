package domain.service;

import domain.entity.OrderStatus;
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

    // Метод-шаблон
    private void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true,  "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            String html = templateEngine.process(templateName, context);

            helper.setText(html, true);
            mailSender.send(message);
            log.info("Email отправлен на {}", to);

        } catch (Exception e) {
            log.error("Ошибка отправки на {}: ошибка {}", to, e.getMessage());
            throw new RuntimeException("Не удалось отправить email", e);
        }
    }

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

    public void sendOrderConfirmationHtml(String accountEmail, Long orderId, BigDecimal totalAmount) {

            //генерим html из шаблона
            Context context = new Context();
            context.setVariable("orderId", orderId);
            context.setVariable("totalAmount", totalAmount);
            context.setVariable("orderDate", LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
            ));

        sendHtmlEmail(
                accountEmail,
                "Подтверждение заказа #" + orderId,
                "email/order-created-confirmation",
                context
        );

    }

    public void sendOrderStatusChangedConfirmationHtml(String accountEmail, Long orderId, OrderStatus status) {

        String message = switch (status) {
            case CREATED -> throw new IllegalArgumentException("CREATED обрабатывается отдельным событием");
            case APPROVED -> "Ваш заказ подтвержден";
            case PAID -> "Ваш заказ оплачен";
            case REJECTED -> "Ваш заказ отменен магазином";
            case CANCELLED -> "Вы отменили заказ";
            case COMPLETED -> "Ваш заказ собран и готов к доставке";
            case DELEVERED -> throw new IllegalArgumentException("DELEVERED обрабатывается отдельным событием");
         };

        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("statusMessage",message);

        sendHtmlEmail(
                accountEmail,
                "Статус заказа #" + orderId,
                "email/order-status-confirmation",
                context
        );
    }
}
