package ua.com.charnoir.telegram_bot.persistense.entity.user;

import ua.com.charnoir.telegram_bot.persistense.entity.AbstractBaseEntity;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.BotStateType;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.Language;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.Status;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class User extends AbstractBaseEntity {

    @Transient
    String name;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Enumerated(EnumType.STRING)
    @Column(name = "bot_state", nullable = false)
    private BotStateType botState;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private Language language;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false)
    private Status status;

    public User(long chatId) {
        this.chatId = chatId;
        this.botState = BotStateType.START;
        this.language = Language.RUS;
        this.status = Status.USER;
    }
    public User() {

    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public User setStatus(Status status) {
        this.status = status;
        return this;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public BotStateType getBotState() {
        return botState;
    }

    public void setBotState(BotStateType botState) {
        this.botState = botState;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

}