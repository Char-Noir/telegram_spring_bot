package ua.com.charnoir.telegram_bot.handler.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ua.com.charnoir.telegram_bot.handler.CommandHandler;
import ua.com.charnoir.telegram_bot.handler.TextHandler;
import ua.com.charnoir.telegram_bot.persistense.entity.user.User;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.BotStateType;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.Language;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.Status;
import ua.com.charnoir.telegram_bot.persistense.repository.UserRepository;
import ua.com.charnoir.telegram_bot.util.BundleUtil;

import java.io.Serializable;
import java.util.List;

import static ua.com.charnoir.telegram_bot.util.TelegramUtil.createInlineKeyboardButton;
import static ua.com.charnoir.telegram_bot.util.TelegramUtil.createMessageTemplate;


@Component
public class StartHandler implements TextHandler, CommandHandler {

    private static final String COMMAND = "/start";

    private final UserRepository userRepository;

    public StartHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        // Приветствуем пользователя
        SendMessage welcomeMessage = createMessageTemplate(user);
        welcomeMessage.setText(String.format(BundleUtil.getString(user.getLanguage(), "register"), user.getName()));
        ReplyKeyboardMarkup markup = ReplyKeyboardMarkup.builder().build();
        if (user.getStatus() == Status.ADMIN) {
            KeyboardRow keyboardRow1 = new KeyboardRow();
            keyboardRow1.add("/add_category");
            KeyboardRow keyboardRow2 = new KeyboardRow();
            keyboardRow2.add("/categories");
            markup.setKeyboard(List.of(keyboardRow1, keyboardRow2));
            welcomeMessage.setReplyMarkup(markup);
        }
        // Просим назваться
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("English", LanguageHandler.CALLBACK + Language.ENG.getLocale()),
                createInlineKeyboardButton("Русский", LanguageHandler.CALLBACK + Language.RUS.getLocale()),
                createInlineKeyboardButton("Українська", LanguageHandler.CALLBACK + Language.UKR.getLocale())
        );
        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));
        SendMessage languageMessage = createMessageTemplate(user);
        languageMessage.setText(String.format(BundleUtil.getString(user.getLanguage(), "language_chos")));
        languageMessage.setReplyMarkup(inlineKeyboardMarkup);
        // Меняем пользователю статус на - "начальный"
        user.setBotState(BotStateType.NONE);
        userRepository.save(user);
        return List.of(welcomeMessage, languageMessage);
    }

    @Override
    public BotStateType operatedBotState() {
        return BotStateType.START;
    }


    @Override
    public List<String> operatedCommand() {
        return List.of(COMMAND);
    }
}
