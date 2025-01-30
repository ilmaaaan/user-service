package habittracker.userservice.unit.test.service;

import habittracker.userservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NotificationServiceUnitTest {

    @Test
    void sendNotification() {
        KafkaTemplate<String, String> kafkaTemplate = mock(KafkaTemplate.class);
        NotificationService notificationService = new NotificationService(kafkaTemplate);
        String testMessage = "Test Notification";

        notificationService.sendNotification(testMessage);

        verify(kafkaTemplate).send("notificationTopic", testMessage);
    }
}
