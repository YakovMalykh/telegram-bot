package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.interfacies.NotificationService;
import pro.sky.telegrambot.model.Notifications;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private NotificationService notificationService;

    //надо вспомнить @PostConstruct аннотацию
    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            // проверяю на соответсвие текста в отправленном юзером сообщении
            // требуемому сообщению
            if (update.message().text().equals("/start")) {
                SendResponse response = greeting(update);
                //проверяю ушел ли ответ и нет ли ошибок
                System.out.println(response.isOk());
                System.out.println(response.errorCode());
            } else {
//                notificationService.saveNotificationToDB(update);
                giveReport(update);
                //  что делаею если это не /start
                // вызываю метод сервиса распарсить сообщение и созадть сущность напомининие
                // и сохраняю в БД
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public int notifies() {
        List<Notifications> notificationsList = notificationService.checkCurrentNotifications();
        if (notificationsList != null) {
            notificationService.makeNotification(notificationsList)
                    .stream().forEach(n->
                            telegramBot.execute(n)
                    );
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private SendResponse greeting(Update update) {
        //получаю id чата, формирую сообщение, которое буду отправлять обратно в этот чат
        // создаю сущность ответа и возвращаю ее
        Long idChat = update.message().chat().id();
        SendMessage message = new SendMessage(idChat, "Hi! I'm ready to start. " +
                "I'm waiting for format like this: DD.MM.YYYY HH:MM TEXT_NOTIFICATION");
        return telegramBot.execute(message);

    }

    private SendResponse giveReport(Update update) {
        Long idChat = update.message().chat().id();
        SendMessage message = new SendMessage(idChat, "Notification added");
        SendMessage negativeMessage = new SendMessage(idChat, "Notification didn't add. Wrong format!");
        if (notificationService.saveNotificationToDB(update) != null) {
            return telegramBot.execute(message);
        } else {
            return telegramBot.execute(negativeMessage);
        }
    }

}
