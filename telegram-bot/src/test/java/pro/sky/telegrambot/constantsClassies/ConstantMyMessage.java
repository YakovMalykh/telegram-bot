package pro.sky.telegrambot.constantsClassies;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;

public class ConstantMyMessage extends Message {
    // см комментарий в СonstantsTestBot к константе MY_UPDATE_FOR_TEST
    private Integer message_id;
    private ConstantMyUser from;
    private ConstantMyChat chat;
    private String text;

    public ConstantMyMessage(Integer message_id, ConstantMyUser from, ConstantMyChat chat, String text) {
        this.message_id = message_id;
        this.from = from;
        this.chat = chat;
        this.text = text;
    }

    public String text() {
        return text;
    }

    public Integer messageId() {
        return message_id;
    }

    public ConstantMyUser from() {
        return from;
    }

    public Chat chat() {
        return chat;
    }
}
