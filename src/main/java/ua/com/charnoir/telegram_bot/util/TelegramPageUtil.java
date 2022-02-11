package ua.com.charnoir.telegram_bot.util;

import org.springframework.data.domain.Page;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ua.com.charnoir.telegram_bot.persistense.entity.post.Category;

import java.util.ArrayList;
import java.util.List;

public class TelegramPageUtil {

    public static  List<List<InlineKeyboardButton>> createCategoryPageableList(String who, Page<Category> page) {
        List<List<InlineKeyboardButton>> markup = new ArrayList<>();
        for (Category category:
             page.getContent()) {
            markup.add(List.of(TelegramUtil.createInlineKeyboardButton(category.getName(),who+category.getId())));
        }
        if(page.getTotalPages()>1){
            List<InlineKeyboardButton> end = new ArrayList<>();
            if (!page.isFirst()) {
                end.add(TelegramUtil.createInlineKeyboardButton("<-", "page:categories:" + (page.getPageable().getPageNumber() - 1)));
            }
            if (!page.isLast()) {
                end.add(TelegramUtil.createInlineKeyboardButton("->", "page:categories:" + (page.getPageable().getPageNumber() + 1)));
            }
            markup.add(end);
        }

        return markup;
    }
}
