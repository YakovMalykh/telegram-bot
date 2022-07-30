package pro.sky.telegrambot.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Notifications {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "chat_id")
    private Long chatId;
    @JoinColumn(name = "notification_text")
    private String notificationText;
    @JoinColumn(name = "date_time")
    private LocalDateTime dateTime;
    @JoinColumn(name = "name")
    private String nameOfUser;

    public Notifications() {
    }

    public Notifications(Long id, Long chatId, String notificationText, LocalDateTime dateTime, String nameOfUser) {
        this.id = id;
        this.chatId = chatId;
        this.notificationText = notificationText;
        this.dateTime = dateTime;
        this.nameOfUser = nameOfUser;
    }

    public String getNameOfUser() {
        return nameOfUser;
    }

    public void setNameOfUser(String nameOfUser) {
        this.nameOfUser = nameOfUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;

    }
    // создаю геттеры и сеттеры

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    //перепорпеделяю equals и hashCode по всем полям кроме id, т.к. потребуется при проверке
    // напоминания на уникальность
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notifications that = (Notifications) o;
        return Objects.equals(chatId, that.chatId) && Objects.equals(notificationText, that.notificationText) && Objects.equals(dateTime, that.dateTime) && Objects.equals(nameOfUser, that.nameOfUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, notificationText, dateTime, nameOfUser);
    }

    @Override
    public String toString() {
        return "Notifications{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", notificationText='" + notificationText + '\'' +
                ", dateTime=" + dateTime +
                ", nameOfUser='" + nameOfUser + '\'' +
                '}';
    }
}
