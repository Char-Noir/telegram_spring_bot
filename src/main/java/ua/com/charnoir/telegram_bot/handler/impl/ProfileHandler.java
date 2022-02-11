package ua.com.charnoir.telegram_bot.handler.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.com.charnoir.telegram_bot.handler.CommandHandler;
import ua.com.charnoir.telegram_bot.persistense.entity.user.User;
import ua.com.charnoir.telegram_bot.persistense.repository.UserRepository;

import java.io.Serializable;
import java.util.List;

import static ua.com.charnoir.telegram_bot.util.TelegramUtil.createMessageTemplate;

@Component
public class ProfileHandler implements CommandHandler {

    public static final String COMMAND = "/profile";

    final UserRepository userRepository;

    public ProfileHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<String> operatedCommand() {
        return List.of(COMMAND);
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        SendMessage sendMessage = createMessageTemplate(user.getChatId().toString());
        sendMessage.setText("Name: " + user.getName()
                + "*%n Language: " + user.getLanguage());
        return List.of(sendMessage);
    }
}
