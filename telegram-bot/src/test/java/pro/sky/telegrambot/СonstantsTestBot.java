package pro.sky.telegrambot;

import com.pengrad.telegrambot.request.SendMessage;
import pro.sky.telegrambot.constantsClassies.ConstantMyChat;
import pro.sky.telegrambot.constantsClassies.ConstantMyMessage;
import pro.sky.telegrambot.constantsClassies.ConstantMyUpdate;
import pro.sky.telegrambot.constantsClassies.ConstantMyUser;
import pro.sky.telegrambot.model.Notifications;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class СonstantsTestBot {
    public static final Notifications NOTIFICATION_1_OUTDATED = new Notifications(1L, 1L, "notification 1"
            , LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).minusDays(3), "User 1");
    public static final Notifications NOTIFICATION_2_CURRENT = new Notifications(2L, 2L, "notification 2"
            , LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusMinutes(30L), "User 2");
    public static final Notifications NOTIFICATION_3_OUTDATED = new Notifications(3L, 2L, "notification 3"
            , LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).minusDays(3), "User 2");
    public static final Notifications NOTIFICATION_4_FUTURE = new Notifications(4L, 2L, "Проверка"
            , LocalDateTime.of(2199,12,12,12,00), "User 2");
    public static final Notifications NOTIFICATION_5_NOW = new Notifications(5L, 1L, "notification 5"
            , LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "User 1");

    public static final List<Notifications> NOTIFICATIONS_LIST = new ArrayList<>(List.of(NOTIFICATION_1_OUTDATED,
            NOTIFICATION_2_CURRENT, NOTIFICATION_3_OUTDATED, NOTIFICATION_4_FUTURE, NOTIFICATION_5_NOW));
    public static final List<Notifications> CURRENT_NOTIFICATIONS_LIST = new ArrayList<>(List.of(NOTIFICATION_5_NOW));
    public static final SendMessage CURRENT_SEND_MESSAGE = new SendMessage(1L,"notification 5");
    public static final SendMessage CURRENT_SEND_MESSAGE_WITH_DATE = new SendMessage(1L,"notification 5"+ " в "
            + LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));


    public static final ConstantMyChat MY_CHAT_FOR_TEST = new ConstantMyChat(2L);
    public static final ConstantMyUser MY_USER_FOR_TEST = new ConstantMyUser(1L,"User 2");
    public static final ConstantMyMessage MY_MESSAGE_FOR_TEST = new ConstantMyMessage(
            4,MY_USER_FOR_TEST,MY_CHAT_FOR_TEST,"12.12.2199 12:00 Проверка");

    public static final ConstantMyUpdate MY_UPDATE_FOR_TEST = new ConstantMyUpdate(MY_MESSAGE_FOR_TEST);
    // создал класс несколько сових классов (ConstantMyUser, ConstantMyChat, ConstantMyMessage и ConstantMyUpdate), которые
    // наследуются от User, Char, Message и Update соответственно, чтобы создать сущность с
    // необходимыми полями для тестов, т.к. Update замокать не удалось

    public static SendMessage SEND_MESSAGE_FOR_TEST = new SendMessage(2L, "Hi, " + "User 2" + "!\nI'm ready to start.\n" +
            "I'm waiting for format like this:\nDD.MM.YYYY HH:MM TEXT_NOTIFICATION\n" +
            "\nYou can use following commands:" +
            "\n/list-all - to get a list of all reminders" +
            "\n/list-today - to get a list of reminders for today" +
            "\n/list-cleare - clear all your reminders");
}
