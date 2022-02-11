package ua.com.charnoir.telegram_bot.persistense.entity.user.type;

public enum Language {
    UKR("uk_ua"), ENG("en_us"), RUS("ru_ru");

    private final String locale;

    Language(String locale) {
        this.locale = locale;
    }

    public String getLocale() {
        return locale;
    }
}
