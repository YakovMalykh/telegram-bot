package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.interfacies.NotificationService;
import pro.sky.telegrambot.model.Notifications;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

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

            switch (update.message().text()) {
                case "/start":
                    SendMessage message0 = notificationService.greeting(update);
                    SendResponse response = telegramBot.execute(message0);
                    //проверяю ушел ли ответ и нет ли ошибок
                    System.out.println(response.isOk());
                    System.out.println(response.errorCode());
                    break;
                case "/list-all":
                    // вызываю метод для получения полного списка напоминаний пользователя
                    List<Notifications> notificationsList1 = notificationService.getListOfAllNotification(update);
                    notificationService.makeNotificationWithDate(notificationsList1)
                            .forEach(n -> {
                                // передаю SendMessage в .execute() - метод = сообщение отправлено в нужный чат
                                SendResponse response1 = telegramBot.execute(n);
                                System.out.println(response1.isOk());
                                System.out.println(response1.errorCode());
                            });
                    break;
                case "/list-today":
                    List<Notifications> notificationsList2 = notificationService.getListNotificationForToday(update);
                    // создаю из Напоминианий сущности SendMessage и собираю в список, для последующей отправки пользователю
                    notificationService.makeNotificationWithDate(notificationsList2)
                            .forEach(n -> {
                                // передаю SendMessage в .execute() - метод = сообщение отправлено в нужный чат
                                SendResponse response2 = telegramBot.execute(n);
                                System.out.println(response2.isOk());
                                System.out.println(response2.errorCode());
                            });
                    break;
                case "/list-cleare":
                    // очищаю список напоминаний (реально удалятся при ежедневной очистке БД)
                    SendMessage message = notificationService.cleareMyListNotifications(update);
                    telegramBot.execute(message);
                    logger.info("выполнился метод cleareMyListNotifications");
                    break;
                default:
                    telegramBot.execute(notificationService.giveReport(update));
                    // вызываю метод сервиса, парсю сообщение, созадаю сущность "напомининие"
                    // и сохраняю в БД
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    // раз в минуту выполнятеся
    public int notifies() {
        // сначала проеряет имеются ли в БД напоминания на эту минуту - вернет список напоминаний,
        // если такие есть
        List<Notifications> notificationsList = notificationService.checkCurrentNotifications();
        // если список непустой - вызывается метод makeNotification, который возвращает List<SendMessage>
        // который несет id чата (кому это сообщение нужно отправить) и текст сообщения,
        // который нужно отправить
        if (!notificationsList.isEmpty()) {
            notificationService.makeNotification(notificationsList)
                    .forEach(n -> {
                        // передаю SendMessage в .execute() - метод = сообщение отправлено в нужный чат
                        SendResponse response = telegramBot.execute(n);
                        System.out.println(response.isOk());
                        System.out.println(response.errorCode());
                    });
            logger.info("выполнился метод notifies");
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 43 17 * * *")
    // метод удаляет устаревшие напоминания из БД
    public void deleteOldNotifications() {
        notificationService.deleteOldNotification();
        logger.info("выполнился метод deleteOldNotifications - устаревшие напоминания удалены");
    }

}
