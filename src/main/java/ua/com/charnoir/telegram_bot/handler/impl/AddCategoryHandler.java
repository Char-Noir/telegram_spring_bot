package ua.com.charnoir.telegram_bot.handler.impl;


import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ua.com.charnoir.telegram_bot.handler.TextHandler;
import ua.com.charnoir.telegram_bot.persistense.entity.post.Category;
import ua.com.charnoir.telegram_bot.persistense.entity.user.User;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.BotStateType;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.Status;
import ua.com.charnoir.telegram_bot.persistense.repository.CategoryRepository;
import ua.com.charnoir.telegram_bot.persistense.repository.UserRepository;
import ua.com.charnoir.telegram_bot.util.BundleUtil;
import ua.com.charnoir.telegram_bot.util.TelegramUtil;

import java.io.Serializable;
import java.util.List;

@Component
public class AddCategoryHandler implements TextHandler {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public AddCategoryHandler(CategoryRepository categoryRepository, UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (user.getStatus() != Status.ADMIN) {
            throw new RuntimeException("user need to be admin to access this");
        }
        Category category = new Category();
        category.setName(message);
        categoryRepository.save(category);
        user.setBotState(BotStateType.NONE);
        userRepository.save(user);
        SendMessage sendMessage = TelegramUtil.createMessageTemplateMarkDownV2(user.getChatId().toString());
        sendMessage.setText(String.format(BundleUtil.getString(user.getLanguage(), "choose_parent"), category.getName()));
        TelegramUtil.escapeChars(sendMessage);
        return List.of(sendMessage);
    }

    @Override
    public BotStateType operatedBotState() {
        return BotStateType.ADD_CATEGORY;
    }
}
