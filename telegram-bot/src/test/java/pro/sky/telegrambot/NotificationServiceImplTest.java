package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import pro.sky.telegrambot.model.Notifications;
import pro.sky.telegrambot.repositories.NotificationsRepository;
import pro.sky.telegrambot.service.NotificationServiceImpl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static pro.sky.telegrambot.СonstantsTestBot.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
    @Mock
    private NotificationsRepository notificationsRepositoryMock;

    @Mock
    private TelegramBot telegramBot;
    @InjectMocks
    private NotificationServiceImpl out;


    @Test
    public void shouldReturnGreeting() {
        // т.к. в SendMessage не переопределен метод Equals и сравнение полученных
        // экземпляров возвращает False достаю параметры SendMessage в виде Map и уже их сравниваю
        Map<String, Object> resultGreeting = out.greeting(MY_UPDATE_FOR_TEST).getParameters();
        Map<String, Object> expectedResalt = SEND_MESSAGE_FOR_TEST.getParameters();
        assertEquals(expectedResalt, resultGreeting);
    }

    @Test
    public void shouldReturnNameOfUser() {
        String result = out.getNameOfUser(MY_UPDATE_FOR_TEST);
        assertEquals("User 2", result);
    }

    @Test // тестирую сохранение напоминания в БД
    public void shouldReturnSavedNotification() {
        when(notificationsRepositoryMock.save(any(Notifications.class))).thenReturn(NOTIFICATION_4_FUTURE);
        Notifications result = out.saveNotificationToDB(MY_UPDATE_FOR_TEST);
        assertEquals(NOTIFICATION_4_FUTURE, result);
    }

    @Test // тестирую проверку на уникальность сообщения
    public void testNotificationIsUnique() {
        when(notificationsRepositoryMock.findAll()).thenReturn(NOTIFICATIONS_LIST);
        assertFalse(out.notificationIsUnique(NOTIFICATION_5_NOW));
    }


    @Test // тестирую проверку напоминаний на текущую минуту
    public void shouldReturnListCurrentNotifications() {
        when(notificationsRepositoryMock.findAll()).thenReturn(NOTIFICATIONS_LIST);
        List<Notifications> result = out.checkCurrentNotifications();
        assertIterableEquals(CURRENT_NOTIFICATIONS_LIST, result);
    }

    @Test // тестирую получение списка всех напоминаний конкретного
    // пользователя (getListOfAllNotification)
    public void shouldReturnListAllNotificationsOfThisUser() {
        when(notificationsRepositoryMock.findAll()).thenReturn(NOTIFICATIONS_LIST);
        List<Notifications> resultList = out.getListOfAllNotification(MY_UPDATE_FOR_TEST);
        assertEquals(3, resultList.size());
        assertTrue(resultList.contains(NOTIFICATION_2_CURRENT) &&
                resultList.contains(NOTIFICATION_3_OUTDATED) &&
                resultList.contains(NOTIFICATION_4_FUTURE));
    }


    @Test// тестирую getListNotificationForToday - получение списка напоминаний пользователя на сегодня
    public void shouldReturnListNotificationsForToday() {
        when(notificationsRepositoryMock.findAll()).thenReturn(NOTIFICATIONS_LIST);
        List<Notifications> result = out.getListNotificationForToday(MY_UPDATE_FOR_TEST);
        assertTrue(result.contains(NOTIFICATION_2_CURRENT));
        assertEquals(1, result.size());
    }


    @Test
    public void shouldReturnListOfSendMessage() {// провекряю makeNotification
        // т.к. в SendMessage не переопределен метод Equals и сравнение полученных экземпляров возвращает
        // False, проверяю, что возвращается требуемый класс. Достаю его у первого элемента
        List<SendMessage> resultSendMessageList = out.makeNotification(CURRENT_NOTIFICATIONS_LIST);
        Class<? extends SendMessage> resultClass = resultSendMessageList.get(0).getClass();
        assertEquals(SendMessage.class, resultClass);
        // зедсь достаю параметры SendMessage в виде Map и уже их сравниваю
        Map<String, Object> resultParameters = resultSendMessageList.get(0).getParameters();
        Map<String, Object> expectedParameters = CURRENT_SEND_MESSAGE.getParameters();
        assertEquals(expectedParameters, resultParameters);
    }

    @Test// проверка аналогична предыдущему методу (shouldReturnListOfSendMessage), но добавляется дата
    public void testMakeNotificationWithDate() {
        List<SendMessage> resultSendMessageList = out.makeNotificationWithDate(CURRENT_NOTIFICATIONS_LIST);
        Class<? extends SendMessage> resultClass = resultSendMessageList.get(0).getClass();
        assertEquals(SendMessage.class, resultClass);

        Map<String, Object> resultParameters = resultSendMessageList.get(0).getParameters();
        Map<String, Object> expectedParameters = CURRENT_SEND_MESSAGE_WITH_DATE.getParameters();
        assertEquals(expectedParameters, resultParameters);
    }

    @Test // тестирую метод giveReport
    public void shouldReturnSendMessageAfterCallingSaveToDb() {
        when(notificationsRepositoryMock.save(any(Notifications.class))).thenReturn(NOTIFICATION_4_FUTURE);
        Map<String, Object> resultParameters = out.giveReport(MY_UPDATE_FOR_TEST).getParameters();
        SendMessage expectedMessage = new SendMessage(2L, "Notification added");
        Map<String, Object> expectedParameters = expectedMessage.getParameters();
        assertEquals(expectedParameters,resultParameters);

    }

    @Test // тестирую парсинг
    public void shouldParseMessage() {
        Notifications result = out.parsMessage(MY_UPDATE_FOR_TEST);
        assertEquals(NOTIFICATION_4_FUTURE, result);
    }

    @Test //тестирую cleareMyListNotifications, он должен вернуть сообщение и установить у всех напомнианий пользователя
    // утсаревшую дату, что и буду проверять
    public void shouldMarkToDeleteListOfAllNotificationsOfUser() {
        LocalDateTime before = NOTIFICATION_2_CURRENT.getDateTime();
        when(notificationsRepositoryMock.findAll()).thenReturn(NOTIFICATIONS_LIST);
        Map<String, Object> resultParametersSendMessage =
                out.cleareMyListNotifications(MY_UPDATE_FOR_TEST).getParameters();
        SendMessage expectedMessage = new SendMessage(
                2L, "Список напоминаний помечен на удаление");
        Map<String, Object> expectedParametersSendMessage = expectedMessage.getParameters();
        //снова достаю параметры класса  SendMessage, иначе не могу сравнить через Equals
        assertEquals(expectedParametersSendMessage, resultParametersSendMessage);
        LocalDateTime after = NOTIFICATION_2_CURRENT.getDateTime();
        assertNotEquals(after, before);
        // теперь устанавливаю прежние даты, чтобы не повлиять на другие тесты
        NOTIFICATION_2_CURRENT.setDateTime(
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(30L));
        NOTIFICATION_3_OUTDATED.setDateTime(
                LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).minusDays(3));
        NOTIFICATION_4_FUTURE.setDateTime(LocalDateTime.of(2199, 12, 12, 12, 00));
    }


}
