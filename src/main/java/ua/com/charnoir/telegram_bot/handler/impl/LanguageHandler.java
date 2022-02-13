package ua.com.charnoir.telegram_bot.handler.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.com.charnoir.telegram_bot.handler.CallBackHandler;
import ua.com.charnoir.telegram_bot.handler.CommandHandler;
import ua.com.charnoir.telegram_bot.persistense.entity.user.User;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.Language;
import ua.com.charnoir.telegram_bot.persistense.repository.UserRepository;
import ua.com.charnoir.telegram_bot.util.BundleUtil;

import java.io.Serializable;
import java.util.List;

import static ua.com.charnoir.telegram_bot.util.TelegramUtil.createInlineKeyboardButton;
import static ua.com.charnoir.telegram_bot.util.TelegramUtil.createMessageTemplate;

@Component
public class LanguageHandler implements CommandHandler, CallBackHandler {

    public static final String COMMAND = "/language";
    public static final String CALLBACK = "language:";

    private final UserRepository userRepository;

    public LanguageHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(CALLBACK);
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, Update update) {
        String message = update.getCallbackQuery().getData();
        String language = message.split(":")[1].strip();
        for (Language language1 :
                Language.values()) {
            if (language1.getLocale().equals(language)) {
                return setLanguage(user, language1, update);
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    public List<String> operatedCommand() {
        return List.of(COMMAND);
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> inlineKeyboardButtonsRowOne = List.of(
                createInlineKeyboardButton("English", CALLBACK + Language.ENG.getLocale()),
                createInlineKeyboardButton("Русский", CALLBACK + Language.RUS.getLocale()),
                createInlineKeyboardButton("Українська", CALLBACK + Language.UKR.getLocale())
        );
        inlineKeyboardMarkup.setKeyboard(List.of(inlineKeyboardButtonsRowOne));
        SendMessage languageMessage = createMessageTemplate(user);
        languageMessage.setText(String.format(BundleUtil.getString(user.getLanguage(), "language_chos")));
        languageMessage.setReplyMarkup(inlineKeyboardMarkup);
        return List.of(languageMessage);


    }

    private List<PartialBotApiMethod<? extends Serializable>> setLanguage(User user, Language language, Update update) {
        user.setLanguage(language);
        userRepository.save(user);
        AnswerCallbackQuery welcomeMessage = new AnswerCallbackQuery();
        welcomeMessage.setShowAlert(true);
        welcomeMessage.setCallbackQueryId(update.getCallbackQuery().getId());
        welcomeMessage.setText((BundleUtil.getString(user.getLanguage(), "language_set")));
        return List.of(welcomeMessage);
    }
}
