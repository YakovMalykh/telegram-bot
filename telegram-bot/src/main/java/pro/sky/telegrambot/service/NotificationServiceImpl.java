package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.interfacies.NotificationService;
import pro.sky.telegrambot.model.Notifications;
import pro.sky.telegrambot.repositories.NotificationsRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private TelegramBot telegramBot;
    private final NotificationsRepository notificationsRepository;

    private Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    public NotificationServiceImpl(NotificationsRepository notificationsRepository) {
        this.notificationsRepository = notificationsRepository;
    }

    @Override
    public SendMessage greeting(Update update) {
        //получаю id чата, формирую сообщение, которое буду отправлять обратно в этот чат
        // создаю сущность ответа и возвращаю ее
        Long idChat = update.message().chat().id();
        String name = getNameOfUser(update);
        SendMessage message = new SendMessage(idChat, "Hi, " + name + "!\nI'm ready to start.\n" +
                "I'm waiting for format like this:\nDD.MM.YYYY HH:MM TEXT_NOTIFICATION\n" +
                "\nYou can use following commands:" +
                "\n/list-all - to get a list of all reminders" +
                "\n/list-today - to get a list of reminders for today" +
                "\n/list-cleare - clear all your reminders");
        logger.info("Выполнился метод greeting");
        return message;
    }
    // получаю имя пользователя, выбирая из firstName и username,
    // указанного пользователем при регистрации в telegram
    public String getNameOfUser(Update update) {
        String firstName = update.message().from().firstName();
        String userName = update.message().from().username();
        if (!(firstName.isEmpty() && firstName.isBlank())) {
            logger.info("выполнился метод getNameOfUser, получили firstName");
            return firstName;
        } else if (!(userName.isEmpty() && userName.isBlank())) {
            logger.info("выполнился метод getNameOfUser, получили userName");
            return userName;
        }
        logger.info("выполнился метод getNameOfUser, не получили ни firstName, ни userName");
        return "Stranger";
    }

    @Override
    // сохраняю напоминание в БД
    public Notifications saveNotificationToDB(Update update) {
        Notifications notification = parsMessage(update);
        LocalDateTime current = LocalDateTime.now();
        // проверяю, что вернулся не null
        if (notification != null) {
            // так проверяю задана ли дата корректно = не указано ли прошедшее время.
            boolean timeIsCorrect = notification.getDateTime().isAfter(current);
            //  и уникальное напоминание
            if (notificationIsUnique(notification) && timeIsCorrect) {
                logger.info("выполнился метод saveNotificationToDB, сохранили {}", notification);
                return notificationsRepository.save(notification);
            } else {
                return null;
            }
        }
        return null;
    }

    // проверяет на наличие точно такого же напоминания в БД
    public Boolean notificationIsUnique(Notifications notification) {
        if (notificationsRepository.findAll().contains(notification)) {
            return false;
        }
        return true;
    }

    @Override
    // проверяет есть ли напоминимня на данную минуту
    public List<Notifications> checkCurrentNotifications() {
        // вычисляю текущую минуту. Точность до минут!
        LocalDateTime currentMoment = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        // иду в репозиторий и достаю все напоминания и фильтрую их по дате и времени,
        // собираю подходящие напоминания в список
        return notificationsRepository.findAll()
                .stream()
                .filter(n -> n.getDateTime().equals(currentMoment))
                .collect(Collectors.toList());
    }

    @Override
    // метод возвращает весь список напоминаний пользователя
    public List<Notifications> getListOfAllNotification(Update update) {
        Long idChat = update.message().chat().id();
        logger.info("выполняется метод getListOfAllNotification");
        return notificationsRepository.findAll().stream()
                .filter(n -> n.getChatId().equals(idChat))
                .collect(Collectors.toList());
    }
    @Override
    // @Transactional помечаю, что в метод может вносить правки в БД, иначе дату нужную не мог вставить
    @Transactional
    // при вызове этого метода вызываю getListOfAllNotification и получаю список всех напоминаний,
    // затем меняю дату в напомнианиях на позавчерашний день = теперь при ежедневной чистке базы данных
    // метод deleteOldNotification удалит эти напоминания
    public SendMessage cleareMyListNotifications (Update update) {
        LocalDateTime yesterday = LocalDateTime.now().minusDays(2);
        List<Notifications> notificationsList = getListOfAllNotification(update);
        Long chatId = update.message().chat().id();
        SendMessage message = new SendMessage(chatId, "Список напоминаний помечен на удаление");
        SendMessage messageNegative = new SendMessage(chatId, "у Вас нет напомнинаний");

        if (!notificationsList.isEmpty()) {
            notificationsList.forEach(n->n.setDateTime(yesterday));
            System.out.println(yesterday);
            return message;
        } else {
            return messageNegative;
        }
    }

    @Override
    // метод возвращает список напоминаний пользователя на сегодня
    public List<Notifications> getListNotificationForToday(Update update) {
        LocalDateTime today = LocalDateTime.now().truncatedTo(ChronoUnit.DAYS);
        logger.info("выполняется метод getListNotificationForToday");
        // использую метод getListOfAllNotification для получения полного списка напоминаний
        return getListOfAllNotification(update).stream()
                .filter(n -> n.getDateTime().truncatedTo(ChronoUnit.DAYS).equals(today))
                .collect(Collectors.toList());
    }

    @Override
    // из списка напоминаний достает ID чата и сообщение, формирую сущность SendMessage,
    // и собирает их в список
    public List<SendMessage> makeNotification(List<Notifications> notificationsList) {
        List<SendMessage> sendMessageList = notificationsList
                .stream()
                .map(n ->
                        new SendMessage(n.getChatId(), n.getNotificationText())
                )
                .collect(Collectors.toList());
        logger.info("выполнился метод makeNotification");
        return sendMessageList;
    }

    @Override
    // метод аналогичен makeNotification, но в добавок в сообщении выводит еще и дату, использую
    // в методах по получению списка запланированных напоминаний, чтобы пользователь видел не только
    // текст, но и запланированное время
    public List<SendMessage> makeNotificationWithDate(List<Notifications> notificationsList) {
        List<SendMessage> sendMessageList = notificationsList.stream().map(n -> new SendMessage(
                        n.getChatId(), n.getNotificationText() + " в " + n.getDateTime()
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))))
                .collect(Collectors.toList());
        logger.info("выполнился метод makeNotificationWithDate");
        return sendMessageList;
    }

    @Override
    // отправляет в чат отчет добавлено напоминание или нет
    public SendMessage giveReport(Update update) {
        Long idChat = update.message().chat().id();
        SendMessage message = new SendMessage(idChat, "Notification added");
        SendMessage negativeMessage = new SendMessage(idChat, "Notification didn't add. \nWrong format or \nNotification already exists or" +
                "\nPast time");
        if (saveNotificationToDB(update) != null) {
            logger.info("метод giveReport выполняется успешно");
            return message;
        } else {
            logger.info("метод giveReport завершается провалом");
            return negativeMessage;
        }
    }

    @Override
    // метод удаляет устаревшие напоминания, своего рода сборщик мусора
    public void deleteOldNotification() {
        // определяю дату и время, ранее которых напоминания считаю устаревшими.
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        // создаю временный список напоминаний, куда копирую только те напоминания,
        // дата и время которых позже yesterday
        List<Notifications> temporaryListNotifications = notificationsRepository.findAll().stream()
                .filter(n -> n.getDateTime().isAfter(yesterday))
                .collect(Collectors.toList());
        // удаляю все элементы из БД
        notificationsRepository.deleteAll();
        // сохраняю в БД все эелементы из временного списка
        notificationsRepository.saveAll(temporaryListNotifications);
    }

    // парсю текс полученного сообещния, вычленяю дату, время и текст напоминания
    public Notifications parsMessage(Update update) {
        Notifications notification = new Notifications();
        String messageText = update.message().text();
        //использую ID сообщения из чата для сохранения в БД
        Long messageId = update.message().messageId().longValue();

        Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([a-zA-Z0-9\\W+]+)");
        Matcher matcher = pattern.matcher(messageText);
        // если формат верный, вставляю полученные значения в поля нового Напоминиая созданного выше
        if (matcher.matches()) {
            String date = matcher.group(1);
            notification.setDateTime(
                    LocalDateTime.parse(date, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
            String textNotification = matcher.group(3);
            notification.setNotificationText(textNotification);
            notification.setChatId(update.message().chat().id());
            notification.setId(messageId);
            notification.setNameOfUser(getNameOfUser(update));
            logger.info("выполнился метод parsMessage");
            return notification;
        }
        logger.info("выполнился метод parsMessage, вернул null");
        return null;
    }


}
