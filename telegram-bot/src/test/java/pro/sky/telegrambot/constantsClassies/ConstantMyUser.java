package pro.sky.telegrambot.constantsClassies;

import com.pengrad.telegrambot.model.User;

public class ConstantMyUser extends User {
    // см комментарий в СonstantsTestBot к константе MY_UPDATE_FOR_TEST
    private String first_name;

    public ConstantMyUser(Long id, String first_name) {
        super(id);
        this.first_name = first_name;
    }

    public String firstName() {
        return first_name;
    }

}
