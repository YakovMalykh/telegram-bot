package pro.sky.telegrambot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import pro.sky.telegrambot.model.Notifications;
import pro.sky.telegrambot.repositories.NotificationsRepository;
import pro.sky.telegrambot.service.NotificationServiceImpl;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static pro.sky.telegrambot.СonstantsTestBot.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {
    @Mock
    private NotificationsRepository notificationsRepositoryMock;

    @Autowired
    private TelegramBot telegramBot;
    @InjectMocks
    private NotificationServiceImpl out;

    // эти методы допишу, если удастся разобраться с вопросом по коснтанте Update -
    // см комметарий в кл СonstantsTestBot

//    @Test// НЕ ЗАВЕРШЕН, нужно дописать логику
//    public void shouldReturnGreeting() {
//        when(updateMock.message().chat().id()).thenReturn(1L);
//        when(out.getNameOfUser(updateMock)).thenReturn("User 1");
//        SendResponse resultGreeting = out.greeting(updateMock);
//        assertNotNull(resultGreeting);
//    }

    //        @Test
//    public void shouldReturnNameOfUser() {
//        when(updateMock.message().from().firstName()).thenReturn("User 1");
//        when(updateMock.message().from().username()).thenReturn("User 1.1");
//        String result = out.getNameOfUser(updateMock);
//        assertEquals("User 1",result);
//    }
//    @Test
//    public void shouldReturnSavedNotification() {
//        when(out.parsMessage(any(Update.class))).thenReturn(NOTIFICATION_4_FUTURE);
//        when(out.notificationIsUnique(NOTIFICATION_4_FUTURE)).thenReturn(true);
//        Notifications result = out.saveNotificationToDB(updateMock);
//        assertEquals(NOTIFICATION_4_FUTURE,result);
//    }

//    @Test
//    public void shouldParseMessage() {
//        when(updateMock.message()).thenReturn(MESSAGE);
//        when(MESSAGE.text()).thenReturn(CORRECT_MESSAGE);
//        when(MESSAGE.messageId().longValue()).thenReturn(5L);
//        when(MESSAGE.chat().id()).thenReturn(1L);
//        when(out.getNameOfUser(updateMock)).thenReturn("User 1");
//        when(MESSAGE.from().firstName()).thenReturn("User 1");
//        Notifications result = out.parsMessage(updateMock);
//        assertEquals(NOTIFICATION_5_NOW, result);
//    }

    @Test
    public void shouldReturnListCurrentNotifications() {
        when(notificationsRepositoryMock.findAll()).thenReturn(NOTIFICATIONS_LIST);
        List<Notifications> result = out.checkCurrentNotifications();
        assertIterableEquals(CURRENT_NOTIFICATIONS_LIST, result);
    }

    @Test
    public void shouldReturnListOfSendMessage() {
        // т.к. в SendMessage не переопределен метод Equals и сравнение полученных экземпляров возвращает
        // False, проверяю, что возвращается требуемый класс. Достаю его у первого элемента
        List<SendMessage> resultSendMessageList = out.makeNotification(CURRENT_NOTIFICATIONS_LIST);
        Class<? extends SendMessage> resultClass = resultSendMessageList.get(0).getClass();
        assertEquals(SendMessage.class, resultClass);
        // зедсь достаю параметры SendMessage в виде Map и уже их сравниваю
        Map<String, Object> resultParameters = resultSendMessageList.get(0).getParameters();
        Map<String, Object> expectedParameters = CURRENT_SEND_MESSAGE.getParameters();
        assertEquals(expectedParameters,resultParameters);
    }

    @Test// проверка аналогична предыдущему методу, но добавляется дата
    public void testmakeNotificationWithDate() {
        List<SendMessage> resultSendMessageList = out.makeNotificationWithDate(CURRENT_NOTIFICATIONS_LIST);
        Class<? extends SendMessage> resultClass = resultSendMessageList.get(0).getClass();
        assertEquals(SendMessage.class, resultClass);

        Map<String, Object> resultParameters = resultSendMessageList.get(0).getParameters();
        Map<String, Object> expectedParameters = CURRENT_SEND_MESSAGE_WITH_DATE.getParameters();
        assertEquals(expectedParameters,resultParameters);
    }


}
