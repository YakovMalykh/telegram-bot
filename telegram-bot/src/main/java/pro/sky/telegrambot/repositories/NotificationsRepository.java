package pro.sky.telegrambot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.telegrambot.model.Notifications;

import java.util.List;
import java.util.Optional;

public interface NotificationsRepository extends JpaRepository<Notifications,Long> {
}
