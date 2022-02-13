package ua.com.charnoir.telegram_bot.util;

import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateUtil {

    public static String getMessageType(Update update) {
        if (update.hasMessage()) {
            return "message";
        } else if (update.hasCallbackQuery()) {
            return "callback";
        } else if (update.hasChannelPost()) {
            return update.getChannelPost().getText();
        }
        throw new IllegalArgumentException();
    }

    public static String getMessage(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getText();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }
        throw new IllegalArgumentException();
    }

    public static long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }
        throw new IllegalArgumentException();
    }

    public static String getUserName(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChat().getUserName();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getUserName();
        }
        throw new IllegalArgumentException();
    }
}
