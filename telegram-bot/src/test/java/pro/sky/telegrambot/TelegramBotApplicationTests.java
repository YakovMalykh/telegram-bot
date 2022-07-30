package pro.sky.telegrambot;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.repositories.NotificationsRepository;

import static org.junit.jupiter.api.Assertions.*;
import static pro.sky.telegrambot.СonstantsTestBot.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TelegramBotApplicationTests {

    @LocalServerPort
    private int port;
    @Autowired
    private NotificationsRepository notificationsRepository;
    @Autowired
    private TelegramBotUpdatesListener botUpdatesListener;

    // здесь удается протестировать только один метод
    @Test
    void contextLoads() {
        Assertions.assertThat(botUpdatesListener).isNotNull();
    }

    @BeforeEach//предварительно заполняю БД необходимыми элементами
    void fillDBBeforeTest() {
        notificationsRepository.save(NOTIFICATION_1_OUTDATED);
        notificationsRepository.save(NOTIFICATION_2_CURRENT);
        notificationsRepository.save(NOTIFICATION_3_OUTDATED);
        notificationsRepository.save(NOTIFICATION_4_FUTURE);
        notificationsRepository.save(NOTIFICATION_5_NOW);
    }

    @AfterEach//после выполнения метода чищу БД от помещенных в нее элементов для тестов,
        // т.к. работаю с действующей БД и в ней не должно остаться тестовых данных
    void cleareDBAfterMethod() {
        notificationsRepository.delete(NOTIFICATION_1_OUTDATED);
        notificationsRepository.delete(NOTIFICATION_2_CURRENT);
        notificationsRepository.delete(NOTIFICATION_3_OUTDATED);
        notificationsRepository.delete(NOTIFICATION_4_FUTURE);
        notificationsRepository.delete(NOTIFICATION_5_NOW);
    }

    @Test// тестирую очистку БД от устаревших сообщений
    void testDeleteOldNotifications() {
        long countBefore = notificationsRepository.count();
        botUpdatesListener.deleteOldNotifications();
        long countAfter = notificationsRepository.count();
        assertEquals(2, countBefore - countAfter);
        assertTrue(countBefore > countAfter);

    }


}
