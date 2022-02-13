package ua.com.charnoir.telegram_bot.handler.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.com.charnoir.telegram_bot.handler.CallBackHandler;
import ua.com.charnoir.telegram_bot.handler.CommandHandler;
import ua.com.charnoir.telegram_bot.persistense.entity.user.User;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.BotStateType;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.Status;
import ua.com.charnoir.telegram_bot.persistense.repository.UserRepository;
import ua.com.charnoir.telegram_bot.util.BundleUtil;

import java.io.Serializable;
import java.util.List;

import static ua.com.charnoir.telegram_bot.util.TelegramUtil.createInlineKeyboardButton;
import static ua.com.charnoir.telegram_bot.util.TelegramUtil.createMessageTemplate;

@Component
public class AddCategoryStartHandler implements CommandHandler, CallBackHandler {

    public static final String COMMAND = "/add_category";
    public static final String CALLBACK = "addcategory";
    private final UserRepository userRepository;

    public AddCategoryStartHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<String> operatedCommand() {
        return List.of(COMMAND);
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (user.getStatus() == Status.ADMIN && (user.getBotState() == BotStateType.NONE || user.getBotState() == BotStateType.ADD_CATEGORY)) {
            user.setBotState(BotStateType.ADD_CATEGORY);
            userRepository.save(user);
            SendMessage sendMessage = createMessageTemplate(user.getChatId().toString(), String.format(BundleUtil.getString(user.getLanguage(), "add_category")), createInlineKeyboardButton("Back", "back"));
            return List.of(sendMessage);
        }
        SendMessage sendMessage = createMessageTemplate(user.getChatId().toString(), String.format(BundleUtil.getString(user.getLanguage(), "unavailable")), createInlineKeyboardButton("Back", "back"));
        return List.of(sendMessage);
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(CALLBACK);
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, Update update) {
        return handle(user, update.getCallbackQuery().getData());
    }
}
