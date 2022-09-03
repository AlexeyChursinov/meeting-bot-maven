package ru.chursinov.meetingbot.cache;


import ru.chursinov.meetingbot.botapi.BotState;
import ru.chursinov.meetingbot.entity.UserProfileData;

public interface DataCache {
    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    UserProfileData getUserProfileData(int userId);

    void saveUserProfileData(int userId, UserProfileData userProfileData);
}
