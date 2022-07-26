package pro.sky.telegrambot.interfacies;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import pro.sky.telegrambot.model.Notifications;

import java.util.List;

public interface NotificationService {
    public Notifications saveNotificationToDB(Update update);
    public List<Notifications> checkCurrentNotifications();
    public List<SendMessage> makeNotification (List<Notifications> notificationsList);
}
