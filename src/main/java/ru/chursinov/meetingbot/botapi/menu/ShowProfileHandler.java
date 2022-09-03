package ru.chursinov.meetingbot.botapi.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.chursinov.meetingbot.botapi.BotState;
import ru.chursinov.meetingbot.botapi.InputMessageHandler;
import ru.chursinov.meetingbot.cache.UserDataCache;
import ru.chursinov.meetingbot.entity.UserProfileData;
import ru.chursinov.meetingbot.service.UsersProfileDataService;
import ru.chursinov.meetingbot.utils.Emojis;
import ru.chursinov.meetingbot.utils.GetCurrentDate;


@Component
public class ShowProfileHandler implements InputMessageHandler {
    private UserDataCache userDataCache;
    private GetCurrentDate getCurrentDate;
    private UsersProfileDataService usersProfileDataService;

    @Autowired
    public ShowProfileHandler(UserDataCache userDataCache, GetCurrentDate getCurrentDate, UsersProfileDataService usersProfileDataService) {
        this.userDataCache = userDataCache;
        this.getCurrentDate = getCurrentDate;
        this.usersProfileDataService = usersProfileDataService;
    }

    @Override
    public SendMessage handle(Message message) {
        final int userId = Math.toIntExact(message.getFrom().getId());
//        final UserProfileData profileData = userDataCache.getUserProfileData(userId);

        UserProfileData answer = usersProfileDataService.getUserAnswer(userId, getCurrentDate.getDate());

        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        if (answer != null) {
            return new SendMessage(Long.toString(message.getChatId()), String.format("%s%n --------------------------------------%nСделано вчера: %n%s%n %nПланы на сегодня: %n%s%n %nЕсть ли проблемы: %n%s%n %nОписание проблем: %n%s%n",
                    "Ваши ответы " + Emojis.CALENDAR + " " + answer.getDate(), answer.getYesterday(), answer.getToday(), answer.getProblem(), answer.getProblem_details()));
        } else {
            return new SendMessage(Long.toString(message.getChatId()), Emojis.POINT_UP + " Вы сегодня ещё не отвечали на вопросы бота");
        }
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_USER_PROFILE;
    }
}
