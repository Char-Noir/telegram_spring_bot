package ua.com.charnoir.telegram_bot.handler.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.com.charnoir.telegram_bot.handler.CallBackHandler;
import ua.com.charnoir.telegram_bot.handler.CommandHandler;
import ua.com.charnoir.telegram_bot.persistense.entity.post.Category;
import ua.com.charnoir.telegram_bot.persistense.entity.user.User;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.Status;
import ua.com.charnoir.telegram_bot.persistense.repository.CategoryRepository;
import ua.com.charnoir.telegram_bot.util.BundleUtil;
import ua.com.charnoir.telegram_bot.util.TelegramUtil;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

@Component
public class CategoryListHandler implements CommandHandler, CallBackHandler {

    public static final String COMMAND_CAT = "/categories";
    public static final String CALLBACK_CAT = "page:categories:";

    private static final int SIZE = 10;

    private final CategoryRepository categoryRepository;

    public CategoryListHandler(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<String> operatedCommand() {
        return List.of(COMMAND_CAT);
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, String message) {
        if (user.getStatus() != Status.ADMIN) {
            throw new RuntimeException("user need to be admin to access this");
        }
        int page = 0;
        StringBuilder stringBuilder = new StringBuilder(String.format(BundleUtil.getString(user.getLanguage(), "categories.list.name")));
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        initMessage(page, stringBuilder, buttons);
        SendMessage sendMessage = TelegramUtil.createMessageTemplate(user.getChatId().toString());
        sendMessage.setText(stringBuilder.toString());
        sendMessage.setReplyMarkup(new InlineKeyboardMarkup(List.of(buttons)));
        return List.of(sendMessage);
    }

    private void initMessage(int page, StringBuilder stringBuilder, List<InlineKeyboardButton> buttons) {
        Page<Category> categoryPage = categoryRepository.findAll(PageRequest.of(page, SIZE));
        stringBuilder.append(":\n");
        int offset = (categoryPage.getPageable().getPageNumber()) * SIZE + 1;
        List<Category> categories = categoryPage.getContent();
        for (int i = 0; i < categories.size(); i++) {
            stringBuilder.append(i + offset).append(")");
            stringBuilder.append(categories.get(i).getName());
            stringBuilder.append("\n");
        }
        if (!categoryPage.isFirst()) {
            buttons.add(TelegramUtil.createInlineKeyboardButton("<-", CALLBACK_CAT + (page - 1)));
        }
        buttons.add(TelegramUtil.createInlineKeyboardButton(String.valueOf((page + 1)), "show:" + (page + 1)));
        if (!categoryPage.isLast()) {
            buttons.add(TelegramUtil.createInlineKeyboardButton("->", CALLBACK_CAT + (page + 1)));
        }
    }

    @Override
    public List<String> operatedCallBackQuery() {
        return List.of(CALLBACK_CAT);
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handle(User user, Update update) {
        String message = update.getCallbackQuery().getData();
        List<PartialBotApiMethod<? extends Serializable>> list = new ArrayList<>();
        int page;
        String[] strings = message.split(":");
        if (strings.length != 3) {
            throw new IllegalArgumentException("Pattern need to be " + CALLBACK_CAT + "page number");
        }
        page = Integer.parseInt(strings[2]);
        StringBuilder stringBuilder = new StringBuilder(String.format(BundleUtil.getString(user.getLanguage(), "categories.list.name")));
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        initMessage(page, stringBuilder, buttons);
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(update.getCallbackQuery().getMessage().getDate()),
                TimeZone.getDefault().toZoneId());
        if (dateTime.isBefore(LocalDateTime.now().minus(2, ChronoUnit.DAYS))) {
            SendMessage sendMessage = TelegramUtil.createMessageTemplate(user.getChatId().toString());
            sendMessage.setText(stringBuilder.toString());
            sendMessage.setReplyMarkup(new InlineKeyboardMarkup(List.of(buttons)));
            list.add(sendMessage);
        } else {
            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setText(stringBuilder.toString());
            editMessageText.setReplyMarkup(new InlineKeyboardMarkup(List.of(buttons)));
            editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
            editMessageText.setInlineMessageId(update.getCallbackQuery().getInlineMessageId());
            editMessageText.setChatId(update.getCallbackQuery().getFrom().getId().toString());
            list.add(editMessageText);
        }
        AnswerCallbackQuery callbackQuery = new AnswerCallbackQuery();
        callbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
        callbackQuery.setShowAlert(false);
        list.add(callbackQuery);
        return list;
    }

}
