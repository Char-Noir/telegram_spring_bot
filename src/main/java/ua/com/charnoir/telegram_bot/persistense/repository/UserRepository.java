package ua.com.charnoir.telegram_bot.persistense.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.com.charnoir.telegram_bot.persistense.entity.user.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> getByChatId(long chatId);
}
