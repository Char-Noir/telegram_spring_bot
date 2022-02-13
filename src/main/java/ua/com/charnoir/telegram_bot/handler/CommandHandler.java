package ua.com.charnoir.telegram_bot.handler;

import java.util.List;

public interface CommandHandler extends StringHandler {
    // метод, который позволяет узнать, какие команды CallBackQuery мы можем обработать в этом классе
    List<String> operatedCommand();
}
