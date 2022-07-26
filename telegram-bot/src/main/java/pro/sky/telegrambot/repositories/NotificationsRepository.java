package pro.sky.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.model.Notifications;

public interface NotificationsRepository extends JpaRepository<Notifications,Long> {
}
