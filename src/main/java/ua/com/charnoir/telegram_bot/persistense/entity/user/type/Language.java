package ua.com.charnoir.telegram_bot.persistense.entity.user.type;

public enum Language {
    UKR("uk_ua", "uk"), ENG("en_us", "en"), RUS("ru_ru", "ru");

    private final String locale;
    private final String translatorKey;

    Language(String locale, String translatorKey) {
        this.locale = locale;
        this.translatorKey = translatorKey;
    }

    public String getLocale() {
        return locale;
    }

    public String getTranslator() {
        return translatorKey;
    }
}
