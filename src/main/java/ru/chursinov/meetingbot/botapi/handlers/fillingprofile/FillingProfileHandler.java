package ru.chursinov.meetingbot.botapi.handlers.fillingprofile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.chursinov.meetingbot.botapi.BotState;
import ru.chursinov.meetingbot.botapi.InputMessageHandler;
import ru.chursinov.meetingbot.cache.UserDataCache;
import ru.chursinov.meetingbot.entity.UserProfileData;
import ru.chursinov.meetingbot.service.ReplyMessagesService;
import ru.chursinov.meetingbot.service.UsersProfileDataService;
import ru.chursinov.meetingbot.utils.Emojis;
import ru.chursinov.meetingbot.utils.GetCurrentDate;

import java.util.ArrayList;
import java.util.List;


/**
 * Формирует анкету пользователя.
 */

@Slf4j
@Component
public class FillingProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final ReplyMessagesService messagesService;
    private final GetCurrentDate getCurrentDate;
    private final UsersProfileDataService usersProfileDataService;

    @Autowired
    public FillingProfileHandler(UserDataCache userDataCache,
                                 ReplyMessagesService messagesService,
                                 GetCurrentDate getCurrentDate,
                                 UsersProfileDataService usersProfileDataService) {
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.getCurrentDate = getCurrentDate;
        this.usersProfileDataService = usersProfileDataService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(Math.toIntExact(message.getFrom().getId())).equals(BotState.FILLING_PROFILE)) {
            userDataCache.setUsersCurrentBotState(Math.toIntExact(message.getFrom().getId()), BotState.ASK_YESTERDAY);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.FILLING_PROFILE;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = Math.toIntExact(inputMsg.getFrom().getId());
        long chatId = inputMsg.getChatId();

        UserProfileData profileData = userDataCache.getUserProfileData(userId);
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        SendMessage replyToUser = null;

        if (botState.equals(BotState.ASK_YESTERDAY)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askYesterday");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_PROBLEM);
        }

        if (botState.equals(BotState.ASK_PROBLEM)) {
            profileData.setYesterday(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askProblem");
            replyToUser.setReplyMarkup(getProblemButtonsMarkup());
        }


        if (botState.equals(BotState.ASK_PROBLEM_DETAILS)) {
            profileData.setProblem_details(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askProblemDetails");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_TODAY);
        }

        if (botState.equals(BotState.ASK_TODAY)) {
            profileData.setProblem_details(usersAnswer);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.askToday");
            userDataCache.setUsersCurrentBotState(userId, BotState.PROFILE_FILLED);
        }

        if (botState.equals(BotState.PROFILE_FILLED)) {

            profileData.setToday(usersAnswer);

            UserProfileData answer = usersProfileDataService.getUserAnswer(userId, getCurrentDate.getDate());

            if (answer != null) {
                profileData.setId(answer.getId());
            }

            profileData.setDate(getCurrentDate.getDate());
            profileData.setUserid(Math.toIntExact(inputMsg.getFrom().getId()));
            profileData.setUsername(inputMsg.getFrom().getUserName());

            usersProfileDataService.saveUserProfileData(profileData);

            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_SEND_INFO);
//            replyToUser = new SendMessage(Long.toString(chatId), String.format("%s %s", "Данные по вашей анкете", profileData));
//            userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.profileFilled", Emojis.LIKE);
        }

        userDataCache.saveUserProfileData(userId, profileData);

        return replyToUser;
    }

    private InlineKeyboardMarkup getProblemButtonsMarkup() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        InlineKeyboardButton buttonProblemYes = new InlineKeyboardButton();
        InlineKeyboardButton buttonProblemNo = new InlineKeyboardButton();

        buttonProblemYes.setText("Да");
        buttonProblemNo.setText("Нет");

        //Every button must have callBackData, or else not work !
        buttonProblemYes.setCallbackData("buttonProblemYes");
        buttonProblemNo.setCallbackData("buttonProblemNo");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(buttonProblemYes);
        keyboardButtonsRow1.add(buttonProblemNo);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);

        return inlineKeyboardMarkup;
    }


}



