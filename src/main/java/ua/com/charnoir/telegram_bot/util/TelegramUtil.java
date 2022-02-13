package ua.com.charnoir.telegram_bot.util;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.com.charnoir.telegram_bot.persistense.entity.user.User;

import java.io.Serializable;
import java.util.List;

import static ua.com.charnoir.telegram_bot.util.UpdateUtil.*;

public class TelegramUtil {
    public static SendMessage createMessageTemplate(User user) {
        return createMessageTemplate(String.valueOf(user.getChatId()));
    }

    // Создаем шаблон SendMessage с включенным Markdown
    public static SendMessage createMessageTemplate(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableMarkdown(true);
        return message;
    }

    public static SendMessage createMessageTemplate(String chatId, String message, InlineKeyboardButton... buttons) {
        SendMessage sendMessage = createMessageTemplate(chatId);
        sendMessage.setText(message);
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(List.of(buttons)));
        sendMessage.setReplyMarkup(markup);
        return sendMessage;
    }

    // Создаем кнопку
    public static InlineKeyboardButton createInlineKeyboardButton(String text, String command) {
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(command);
        return inlineKeyboardButton;
    }


    public static SendMessage error(User user) {
        SendMessage message = new SendMessage();
        message.setChatId(user.getChatId().toString());
        message.enableMarkdown(true);
        message.setText(String.format(BundleUtil.getString(user.getLanguage(), "error"), ValueUtil.getCreator()));
        return message;
    }

    public static List<PartialBotApiMethod<? extends Serializable>> error(User user, Exception e, Update update) {
        SendMessage userMessage = error(user);
        SendMessage adminMessage = createMessageTemplate(ValueUtil.getCreator());
        adminMessage.setText("Catch error while working with " + getMessageType(update) + " from " + getChatId(update) + " aka " + getUserName(update) + "with string" + getMessage(update) + "and error" + e.getMessage());
        return List.of(userMessage, adminMessage);
    }

    public static SendMessage createMessageTemplateMarkDownV2(String chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.enableMarkdownV2(true);
        return message;
    }

    public static void escapeChars(SendMessage sendMessage) {
        String str = sendMessage.getText();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char character = str.charAt(i);
            if ((int) character >= 1 && (int) character <= 126) {
                stringBuilder.append("\\");
            }
            stringBuilder.append(character);
        }
        sendMessage.setText(stringBuilder.toString());
    }
}