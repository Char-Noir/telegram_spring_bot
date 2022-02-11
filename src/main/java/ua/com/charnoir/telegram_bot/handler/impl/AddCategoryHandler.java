package ua.com.charnoir.telegram_bot.handler.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ua.com.charnoir.telegram_bot.handler.TextHandler;
import ua.com.charnoir.telegram_bot.persistense.entity.post.Category;
import ua.com.charnoir.telegram_bot.persistense.entity.user.User;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.BotStateType;
import ua.com.charnoir.telegram_bot.persistense.repository.CategoryRepository;
import ua.com.charnoir.telegram_bot.util.BundleUtil;
import ua.com.charnoir.telegram_bot.util.TelegramPageUtil;
import ua.com.charnoir.telegram_bot.util.TelegramUtil;

import java.io.Serializable;
import java.util.List;
@Component
public class AddCategoryHandler implements TextHandler {

    private final CategoryRepository categoryRepository;

    public AddCategoryHandler(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        Category category = new Category();
        category.setName(message);
        Page<Category> categories = categoryRepository.findAll(PageRequest.of(0, 9));
        categoryRepository.save(category);
        SendMessage sendMessage = TelegramUtil.createMessageTemplate(user.getChatId().toString());
        sendMessage.setText(String.format(BundleUtil.getString(user.getLanguage(), "choose_parent")));
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(TelegramPageUtil.createCategoryPageableList("setParentCategory:" + category.getId() + ":", categories));
        System.out.println(categories.getTotalElements());
        sendMessage.setReplyMarkup(markup);
        return List.of(sendMessage);
    }

    @Override
    public BotStateType operatedBotState() {
        return BotStateType.ADD_CATEGORY;
    }
}
