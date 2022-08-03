package pro.sky.telegrambot.constantsClassies;

import com.pengrad.telegrambot.model.Chat;

public class ConstantMyChat extends Chat {
    // см комментарий в СonstantsTestBot к константе MY_UPDATE_FOR_TEST
    private Long id;

    public ConstantMyChat(Long id) {
        this.id = id;
    }

    public Long id() {
        return id;
    }
}
