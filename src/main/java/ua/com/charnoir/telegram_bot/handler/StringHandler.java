package ua.com.charnoir.telegram_bot.handler;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import ua.com.charnoir.telegram_bot.persistense.entity.user.User;

import java.io.Serializable;
import java.util.List;

public interface StringHandler extends Handler{
    // основной метод, который будет обрабатывать действия пользователя
    List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message);
}
