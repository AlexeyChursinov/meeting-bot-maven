package ru.chursinov.meetingbot.botapi.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.chursinov.meetingbot.botapi.BotState;
import ru.chursinov.meetingbot.botapi.InputMessageHandler;
import ru.chursinov.meetingbot.cache.UserDataCache;
import ru.chursinov.meetingbot.utils.Emojis;


@Component
public class HelpDescriptionHandler implements InputMessageHandler {
    private UserDataCache userDataCache;

    @Autowired
    public HelpDescriptionHandler(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        final int userId = Math.toIntExact(message.getFrom().getId());
        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);

        return new SendMessage(Long.toString(message.getChatId()),
                String.format("Помощь " + Emojis.INFO + "%n" +
                                "-------------------%n" +
                                "Meeting Bot предназначен для сбора информации о работе сотрудника. %n%n" +
                                "При запуске бота в чат поступит вопрос: Хотите заполнить информацию о своей работе? %n" +
                                "Если ответить \"Да\", бот начнет задавать вопросы. %n" +
                                "Ответы будут сохранены. %n%n" +
                                "Чтобы посмотреть свои ответы воспользуйтесь кнопкой главного меню \"Мои ответы\"%n%n" +
                                "Чтобы повторно заполнить информацию по работе, выберите соответствующую кнопку меню или воспользуйтесь командой /questions %n" +
                                "Отправленные ранее ответы перезапишутся %n%n" +
                                "Бот запоминает ответы на текущую дату. " +
                                "То есть, если в течении дня вы отвечали на вопросы бота несколько раз, бот запомнит только последние ответы."));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_HELP_DESCRIPTION;
    }
}
