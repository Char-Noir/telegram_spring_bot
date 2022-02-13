package ua.com.charnoir.telegram_bot.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ValueUtil {

    @Value("${bot.creator}")
    private static String creator;


    public static String getCreator(){
        return creator;
    }
}
