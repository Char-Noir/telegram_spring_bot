package ua.com.charnoir.telegram_bot.handler.impl;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.com.charnoir.telegram_bot.handler.CallBackHandler;
import ua.com.charnoir.telegram_bot.persistense.entity.user.User;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.BotStateType;
import ua.com.charnoir.telegram_bot.persistense.repository.UserRepository;
import ua.com.charnoir.telegram_bot.util.BundleUtil;
import ua.com.charnoir.telegram_bot.util.TelegramUtil;

import java.io.Serializable;
import java.util.List;

public class BackHandler implements CallBackHandler {

    public static final String CALLBACK = "back";
    private final UserRepository userRepository;

    public BackHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(CALLBACK);
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, Update update) {
        user.setBotState(BotStateType.NONE);
        userRepository.save(user);
        SendMessage sendMessage = TelegramUtil.createMessageTemplate(user.getChatId().toString());
        sendMessage.setText(String.format(BundleUtil.getString(user.getLanguage(), "none")));
        return List.of(sendMessage);
    }
}
