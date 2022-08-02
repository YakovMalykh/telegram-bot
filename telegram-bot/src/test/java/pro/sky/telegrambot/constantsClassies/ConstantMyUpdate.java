package pro.sky.telegrambot.constantsClassies;

import com.pengrad.telegrambot.model.Update;
import pro.sky.telegrambot.constantsClassies.ConstantMyMessage;

public class ConstantMyUpdate extends Update {
    // см комментарий в СonstantsTestBot к константе MY_UPDATE_FOR_TEST

    private ConstantMyMessage message;

    public ConstantMyUpdate(ConstantMyMessage message) {
        this.message = message;
    }

    public ConstantMyMessage message() {
        return message;
    }
}
