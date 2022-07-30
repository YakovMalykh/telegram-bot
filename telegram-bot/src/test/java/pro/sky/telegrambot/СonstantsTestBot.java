package pro.sky.telegrambot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
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
    public static final Notifications NOTIFICATION_4_FUTURE = new Notifications(4L, 2L, "notification 4"
            , LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).plusDays(1), "User 2");
    public static final Notifications NOTIFICATION_5_NOW = new Notifications(5L, 1L, "CORRECT_TEXT_NOTIFICATION"
            , LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES), "User 1");

    public static final List<Notifications> NOTIFICATIONS_LIST = new ArrayList<>(List.of(NOTIFICATION_1_OUTDATED,
            NOTIFICATION_2_CURRENT, NOTIFICATION_3_OUTDATED, NOTIFICATION_4_FUTURE, NOTIFICATION_5_NOW));
    public static final List<Notifications> CURRENT_NOTIFICATIONS_LIST = new ArrayList<>(List.of(NOTIFICATION_5_NOW));
    public static final SendMessage CURRENT_SEND_MESSAGE = new SendMessage(1L,"CORRECT_TEXT_NOTIFICATION");
    public static final SendMessage CURRENT_SEND_MESSAGE_WITH_DATE = new SendMessage(1L,"CORRECT_TEXT_NOTIFICATION"+ " в "
            + LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES).format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));

    // пытаюсь создать константу класса Update, чтобы прописать тесты всех оставшихся методов

    // взял из логов строку Update, хочу преобразовать обратно в JSON и уже из этого JSON воссоздать экземпляр класса Update

//    public static final String str = "Update{update_id=900654397, message=Message{message_id=357, from=User{id=584199371, is_bot=false, first_name='Yakov', last_name='Malykh', username='null'" +
//            ", language_code='ru', can_join_groups=null, can_read_all_group_messages=null, supports_inline_queries=null}, sender_chat=null, date=1659176078," +
//            " chat=Chat{id=584199371, type=Private, first_name='Yakov', last_name='Malykh', username='null', title='null', photo=null, bio='null', " +
//            "has_private_forwards=null, description='null', invite_link='null', pinned_message=null, permissions=null, slow_mode_delay=null, message_auto_delete_time=null," +
//            " has_protected_content=null, sticker_set_name='null', can_set_sticker_set=null, linked_chat_id=null, location=null}, forward_from=null, forward_from_chat=null" +
//            ", forward_from_message_id=null, forward_signature='null', forward_sender_name='null', forward_date=null, is_automatic_forward=null, reply_to_message=null," +
//            " via_bot=null, edit_date=null, has_protected_content=null, media_group_id='null', author_signature='null', text='30.07.2022 17:23 снова про JSON', " +
//            "entities=null, caption_entities=null, audio=null, document=null, animation=null, game=null, photo=null, sticker=null, video=null, voice=null, video_note=null" +
//            ", caption='null', contact=null, location=null, venue=null, poll=null, dice=null, new_chat_members=null, left_chat_member=null, new_chat_title='null', " +
//            "new_chat_photo=null, delete_chat_photo=null, group_chat_created=null, supergroup_chat_created=null, channel_chat_created=null, message_auto_delete_timer_changed=null," +
//            " migrate_to_chat_id=null, migrate_from_chat_id=null, pinned_message=null, invoice=null, successful_payment=null, connected_website='null', passport_data=null" +
//            ", proximity_alert_triggered=null, voice_chat_started=null, voice_chat_ended=null, voice_chat_participants_invited=null, voice_chat_scheduled=null, " +
//            "reply_markup=null}, edited_message=null, channel_post=null, edited_channel_post=null, inline_query=null, chosen_inline_result=null, callback_query=null, " +
//            "shipping_query=null, pre_checkout_query=null, poll=null, poll_answer=null, my_chat_member=null, chat_member=null, chat_join_request=null}";
//
//    public static final JsonObject jsonObject = new JsonObject().getAsJsonObject(str);
//    public static final Update update = new Gson().fromJson(jsonObject,Update.class);
}
