package pro.sky.telegrambot.interfacies;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import pro.sky.telegrambot.model.Notifications;

import java.util.List;

public interface NotificationService {
    SendMessage greeting(Update update);

    Notifications saveNotificationToDB(Update update);
    List<Notifications> checkCurrentNotifications();


    List<Notifications> getListOfAllNotification(Update update);

    SendMessage cleareMyListNotifications(Update update);

    List<Notifications> getListNotificationForToday(Update update);

    public List<SendMessage> makeNotification (List<Notifications> notificationsList);

    List<SendMessage> makeNotificationWithDate(List<Notifications> notificationsList);

    SendMessage giveReport(Update update);

    void deleteOldNotification();
}
