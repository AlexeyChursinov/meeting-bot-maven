package ru.chursinov.meetingbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.chursinov.meetingbot.botapi.TelegramFacade;

public class MeetingTelegramBot extends TelegramWebhookBot {

    private String webHookPath;
    private String botUserName;
    private String botToken;

    private TelegramFacade telegramFacade;

    @Autowired
    public MeetingTelegramBot(TelegramFacade telegramFacade) {
        this.telegramFacade = telegramFacade;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        final BotApiMethod<?> replyMessageToUser = telegramFacade.handleUpdate(update);

        return replyMessageToUser;
    }


    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }

}
