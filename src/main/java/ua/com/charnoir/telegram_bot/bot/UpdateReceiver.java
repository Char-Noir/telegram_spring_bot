package ua.com.charnoir.telegram_bot.bot;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.com.charnoir.telegram_bot.handler.CallBackHandler;
import ua.com.charnoir.telegram_bot.handler.CommandHandler;
import ua.com.charnoir.telegram_bot.handler.Handler;
import ua.com.charnoir.telegram_bot.handler.TextHandler;
import ua.com.charnoir.telegram_bot.persistense.entity.user.type.BotStateType;
import ua.com.charnoir.telegram_bot.persistense.entity.user.User;
import ua.com.charnoir.telegram_bot.persistense.repository.UserRepository;
import ua.com.charnoir.telegram_bot.util.TelegramUtil;

import java.io.Serializable;
import java.util.List;

@Log4j2
@Component
public class UpdateReceiver {
    // Храним доступные хендлеры в списке (подсмотрел у Miroha)
    private final List<TextHandler> textHandlers;
    private final List<CallBackHandler> callbackHandlers;
    private final List<CommandHandler> commandHandlers;
    // Имеем доступ в базу пользователей
    private final UserRepository userRepository;

    public UpdateReceiver(List<TextHandler> textHandlers, List<CallBackHandler> callbackHandlers, List<CommandHandler> commandHandlers, UserRepository userRepository) {
        this.textHandlers = textHandlers;
        this.callbackHandlers = callbackHandlers;
        this.commandHandlers = commandHandlers;
        this.userRepository = userRepository;
    }

    // Обрабатываем полученный Update
    public List<PartialBotApiMethod<? extends Serializable>> handle(Update update) {

        log.info("Received "+getMessageType(update)+" from " + getChatId(update) + " aka " + getUserName(update) + "with message" + getMessage(update));

        // try-catch, чтобы при несуществующей команде просто возвращать пустой список
        try {
            // Проверяем, если Update - сообщение с текстом
            if (isMessageWithText(update)) {
                log.info("update from "+ getChatId(update) + " aka " + getUserName(update) + " mapped to text receiver");
                // Получаем Message из Update
                final Message message = update.getMessage();
                // Получаем айди чата с пользователем
                final long chatId = message.getFrom().getId();

                // Просим у репозитория пользователя. Если такого пользователя нет - создаем нового и возвращаем его.
                // Как раз на случай нового пользователя мы и сделали конструктор с одним параметром в классе User
                final User user = userRepository.getByChatId(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId)));
                user.setName(update.getMessage().getFrom().getFirstName());
                // Ищем нужный обработчик и возвращаем результат его работы
                return getHandlerByState(user.getBotState()).handle(user, message.getText());

            } else if (isMessageWithCommand(update)) {
                log.info("update from "+ getChatId(update) + " aka " + getUserName(update) + " mapped to command receiver");
                // Получаем Message из Update
                final Message message = update.getMessage();
                // Получаем айди чата с пользователем
                final long chatId = message.getFrom().getId();

                // Просим у репозитория пользователя. Если такого пользователя нет - создаем нового и возвращаем его.
                // Как раз на случай нового пользователя мы и сделали конструктор с одним параметром в классе User
                final User user = userRepository.getByChatId(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId)));
                user.setName(update.getMessage().getFrom().getFirstName());
                // Ищем нужный обработчик и возвращаем результат его работы
                return getHandlerByCommand(update.getMessage().getText()).handle(user, message.getText());
            } else if (update.hasCallbackQuery()) {
                log.info("update from "+ getChatId(update) + " aka " + getUserName(update) + " mapped to callback receiver");
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                final long chatId = callbackQuery.getFrom().getId();
                final User user = userRepository.getByChatId(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId)));

                return getHandlerByCallBackQuery(callbackQuery.getData()).handle(user, callbackQuery.getData());
            }

            throw new UnsupportedOperationException();
        } catch (Exception e) {
            final long chatId = getChatId(update);
            log.error(e.getMessage());
            final User user = userRepository.getByChatId(chatId)
                    .orElseGet(() -> userRepository.save(new User(chatId)));
            return List.of(TelegramUtil.error(user));
        }
    }

    private String getMessageType(Update update) {
        if (update.hasMessage()) {
            return "message";
        } else if (update.hasCallbackQuery()) {
            return "callback";
        }
        throw new IllegalArgumentException();
    }

    private String getMessage(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getText();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getData();
        }
        throw new IllegalArgumentException();
    }

    private long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId();
        }
        throw new IllegalArgumentException();
    }

    private String getUserName(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChat().getUserName();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getUserName();
        }
        throw new IllegalArgumentException();
    }


    private Handler getHandlerByState(BotStateType state) {
        return textHandlers.stream()
                .filter(h -> h.operatedBotState() != null)
                .filter(h -> h.operatedBotState().equals(state))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private Handler getHandlerByCallBackQuery(String query) {
        return callbackHandlers.stream()
                .filter(h -> h.operatedCallBackQuery().stream()
                        .anyMatch(query::startsWith))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private Handler getHandlerByCommand(String command) {
        return commandHandlers.stream()
                .filter(h -> h.operatedCommand().stream()
                        .anyMatch(command::startsWith))
                .findAny()
                .orElseThrow(UnsupportedOperationException::new);
    }

    private boolean isMessageWithText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText() && !update.getMessage().getText().startsWith("/");
    }

    private boolean isMessageWithCommand(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText() && update.getMessage().getText().startsWith("/");
    }
}
