package ua.com.charnoir.telegram_bot.handler;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.com.charnoir.telegram_bot.persistense.entity.user.User;

import java.io.Serializable;
import java.util.List;

public interface CallBackHandler extends Handler {

    // метод, который позволяет узнать, какие команды CallBackQuery мы можем обработать в этом классе
    List<String> operatedCallBackQuery();

    // основной метод, который будет обрабатывать действия пользователя
    List<PartialBotApiMethod<? extends Serializable>> handle(User user, Update update);
}
