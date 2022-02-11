package ua.com.charnoir.telegram_bot.util;

import ua.com.charnoir.telegram_bot.persistense.entity.user.type.Language;

import java.util.Locale;
import java.util.ResourceBundle;

public class BundleUtil {

    public static String getString(Language language, String str){
        Locale locale = new Locale(language.getLocale());
        ResourceBundle bundle = ResourceBundle.getBundle("strings",locale);
        return bundle.getString(str);
    }

}
