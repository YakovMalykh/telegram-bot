package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.interfacies.NotificationService;
import pro.sky.telegrambot.model.Notifications;
import pro.sky.telegrambot.repositories.NotificationsRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationsRepository notificationsRepository;

    public NotificationServiceImpl(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    @Override
    public Notifications saveNotificationToDB(Update update) {
        Notifications notification = parsMessage(update);
        if (notification != null) {
            return notificationsRepository.save(notification);
        } else {
            return null;
        }
    }

    @Override
    public List<Notifications> checkCurrentNotifications() {
        LocalDateTime currentMoment = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        List<Notifications> notificationsList = notificationsRepository.findAll()
                .stream()
                .filter(n -> n.getDateAndTime().equals(currentMoment))
                .collect(Collectors.toList());
        return notificationsList;
    }

    @Override
    public List<SendMessage> makeNotification(List<Notifications> notificationsList) {
        List<SendMessage> sendMessageList = notificationsList
                .stream()
                .map(n -> {
                    SendMessage message = new SendMessage(n.getChatId(), n.getNotificationText());
                    return message;
                })
                .collect(Collectors.toList());
        return sendMessageList;
    }


    private Notifications parsMessage(Update update) {
        Notifications notification = new Notifications();
        String messageText = update.message().text();
        Long messageId = update.message().messageId().longValue();

        Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
        Matcher matcher = pattern.matcher(messageText);

        if (matcher.matches()) {
            String date = matcher.group(1);
            notification.setDateAndTime(
                    LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            String textNotification = matcher.group(3);
            notification.setNotificationText(textNotification);
            notification.setChatId(update.message().chat().id());
            notification.setId(messageId);
            return notification;
        }
        return null;
    }


}
