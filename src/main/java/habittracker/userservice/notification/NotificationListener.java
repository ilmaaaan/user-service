package habittracker.userservice.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Компонент для обработки уведомлений из Kafka.
 */
@Component
public class NotificationListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationListener.class);

    /**
     * Метод для обработки сообщений из Kafka.
     *
     * @param message сообщение из Kafka
     */
    @KafkaListener(topics = "notificationTopic", groupId = "notification_group")
    public void listen(String message) {
        LOGGER.info("Received message: {}", message);

        // Обработка сообщения
        try {
            processMessage(message);
        } catch (Exception e) {
            LOGGER.error("Error processing message: {}", message, e);
        }
    }

    /**
     * Метод для обработки сообщения.
     *
     * @param message сообщение для обработки
     */
    private void processMessage(String message) {
        // Логика обработки сообщения
        LOGGER.debug("Processing message: {}", message);
        // Example: бизнес-логика
    }
}