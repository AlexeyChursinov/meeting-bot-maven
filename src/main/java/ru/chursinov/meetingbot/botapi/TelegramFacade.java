package ru.chursinov.meetingbot.botapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.chursinov.meetingbot.cache.UserDataCache;
import ru.chursinov.meetingbot.entity.UserProfileData;
import ru.chursinov.meetingbot.service.MainMenuService;
import ru.chursinov.meetingbot.utils.Emojis;


/**
 * Этот bean будет обрабатывать объект update
 * определять, есть ли в нём сообщения
 * есть ли запрос от кнопок и т.д.
 */
@Component
@Slf4j
public class TelegramFacade {
    private final BotStateContext botStateContext;
    private final UserDataCache userDataCache;
    private final MainMenuService mainMenuService;

    @Autowired
    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache, MainMenuService mainMenuService) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;


        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return processCallbackQuery(callbackQuery);
        }

        Message message = update.getMessage();

        if (message != null && message.hasText()) {
            int userId = Math.toIntExact(message.getFrom().getId());
            BotState botState = userDataCache.getUsersCurrentBotState(userId);
            log.info("New message from User:{}, userId: {}, chatId: {},  with text: {}, botState: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText(), botState);

            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = Math.toIntExact(message.getFrom().getId());
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                botState = BotState.ASK_SEND_INFO;
                break;
            case "/questions":
                botState = BotState.FILLING_PROFILE;
                break;
            case "/menu":
                botState = BotState.SHOW_HELP_MENU;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        if (inputMsg.equals(Emojis.MEMO + " Заполнить информацию о работе")) {
            botState = BotState.ASK_SEND_INFO;
        }
        if (inputMsg.equals(Emojis.SUNGLASSES + " Мои ответы")) {
            botState = BotState.SHOW_USER_PROFILE;
        }
        if (inputMsg.equals(Emojis.INFO + " Помощь")) {
            botState = BotState.SHOW_HELP_DESCRIPTION;
        }


        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }

    /**
     * Метод для обрабоки callbackQuery
     *
     * @param buttonQuery
     * @return
     */
    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = Math.toIntExact(buttonQuery.getFrom().getId());
        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Воспользуйтесь главным меню");


        //From send info choose buttons
        if (buttonQuery.getData().equals("buttonYes")) {
            callBackAnswer = setEditMessageText(Long.toString(chatId),
                    buttonQuery.getMessage().getMessageId(),
                    "Что было сделано вчера?");
//            callBackAnswer = new SendMessage(Long.toString(chatId), "Что было сделано вчера?");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_PROBLEM);
        } else if (buttonQuery.getData().equals("buttonNo")) {
            callBackAnswer = sendAnswerCallbackQuery("Возвращайся, когда будешь готов", false, buttonQuery);
        }

        //From problems choose buttons
        else if (buttonQuery.getData().equals("buttonProblemYes")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setProblem("Да");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TODAY);
            callBackAnswer = setEditMessageText(Long.toString(chatId),
                    buttonQuery.getMessage().getMessageId(),
                    "Опишите проблемы.");
//            callBackAnswer = new SendMessage(Long.toString(chatId), "Опишите проблемы.");
        } else if (buttonQuery.getData().equals("buttonProblemNo")) {
            UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
            userProfileData.setProblem("Нет");
            userProfileData.setProblem_details("-");
            userDataCache.saveUserProfileData(userId, userProfileData);
            userDataCache.setUsersCurrentBotState(userId, BotState.PROFILE_FILLED);
            callBackAnswer = setEditMessageText(Long.toString(chatId),
                    buttonQuery.getMessage().getMessageId(),
                    "Чем будешь заниматься сегодня?");
            //            callBackAnswer = new SendMessage(Long.toString(chatId), "Информация сохранена! Хорошего дня!");
        } else {
            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        }

        return callBackAnswer;

    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);

        return answerCallbackQuery;
    }

    private EditMessageText setEditMessageText(String chatId, int messageId, String text) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(messageId);
        editMessageText.setText(text);
        editMessageText.setReplyMarkup(null);

        return editMessageText;
    }

}
