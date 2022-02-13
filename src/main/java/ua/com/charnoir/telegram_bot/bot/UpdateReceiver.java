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

import static ua.com.charnoir.telegram_bot.util.UpdateUtil.*;

@Log4j2
@Component
public class UpdateReceiver {
    // Храним доступные хендлеры в списке
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

        log.info("Received " + getMessageType(update) + " from " + getChatId(update) + " aka " + getUserName(update) + " with message" + getMessage(update));

        // try-catch, чтобы обрабатывать незнакомые сообщения
        try {
            // Проверяем, если Update - сообщение с текстом
            if (isMessageWithText(update)) {
                log.info("update from " + getChatId(update) + " aka " + getUserName(update) + " mapped to text receiver");
                // Получаем Message из Update
                final Message message = update.getMessage();
                // Получаем айди чата с пользователем
                final long chatId = message.getFrom().getId();

                // Просим у репозитория пользователя. Если такого пользователя нет - создаем нового и возвращаем его.
                final User user = userRepository.getByChatId(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId)));
                user.setName(update.getMessage().getFrom().getFirstName());
                // Ищем нужный обработчик и возвращаем результат его работы
                return getHandlerByState(user.getBotState()).handle(user, message.getText());

                // Проверяем, если Update - сообщение с командой
            } else if (isMessageWithCommand(update)) {
                log.info("update from " + getChatId(update) + " aka " + getUserName(update) + " mapped to command receiver");
                // Получаем Message из Update
                final Message message = update.getMessage();
                // Получаем айди чата с пользователем
                final long chatId = message.getFrom().getId();

                // Просим у репозитория пользователя. Если такого пользователя нет - создаем нового и возвращаем его.
                final User user = userRepository.getByChatId(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId)));
                user.setName(update.getMessage().getFrom().getFirstName());
                // Ищем нужный обработчик и возвращаем результат его работы
                return getHandlerByCommand(update.getMessage().getText()).handle(user, message.getText());
                // Проверяем, если Update - каллбек
            } else if (update.hasCallbackQuery()) {
                log.info("update from " + getChatId(update) + " aka " + getUserName(update) + " mapped to callback receiver");
                // Получаем CallbackQuery из Update
                final CallbackQuery callbackQuery = update.getCallbackQuery();
                // Получаем айди чата с пользователем
                final long chatId = callbackQuery.getFrom().getId();
                // Просим у репозитория пользователя. Если такого пользователя нет - создаем нового и возвращаем его.
                final User user = userRepository.getByChatId(chatId)
                        .orElseGet(() -> userRepository.save(new User(chatId)));
                // Ищем нужный обработчик и возвращаем результат его работы
                return getHandlerByCallBackQuery(callbackQuery.getData()).handle(user, callbackQuery.getData());
            }
            //Если Update не подходит не под один тип "знакомых" сообщений, то бросаем ошибку
            throw new UnsupportedOperationException();
        } catch (Exception e) {
            // Получаем универсально айди чата с пользователем
            final long chatId = getChatId(update);
            log.error("Catch error while working with " + getMessageType(update) + " from " + getChatId(update) + " aka " + getUserName(update) + "with string" + getMessage(update) + "and error" + e.getMessage());
            // Просим у репозитория пользователя. Если такого пользователя нет - создаем нового и возвращаем его.
            final User user = userRepository.getByChatId(chatId)
                    .orElseGet(() -> userRepository.save(new User(chatId)));
            //Возвращаем пользователю сообщение об ошибке
            //Возможно позже сделаю более подробную ошибку пользователю в зависимости от проблемы
            return (TelegramUtil.error(user, e, update));
        }
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
