package ua.com.charnoir.telegram_bot.handler;

import java.util.List;

public interface CallBackHandler extends Handler {

    // метод, который позволяет узнать, какие команды CallBackQuery мы можем обработать в этом классе
    List<String> operatedCallBackQuery();
}
