package ua.com.charnoir.telegram_bot.handler;

import ua.com.charnoir.telegram_bot.persistense.entity.user.type.BotStateType;

public interface TextHandler extends Handler {
    // метод, который позволяет узнать, можем ли мы обработать текущий State у пользователя
    BotStateType operatedBotState();
}
