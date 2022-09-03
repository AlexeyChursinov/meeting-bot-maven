package ru.chursinov.meetingbot.botapi;

/**
 * Возможные состояния бота
 */

public enum BotState {
    ASK_SEND_INFO,
    ASK_YESTERDAY,
    ASK_TODAY,
    ASK_PROBLEM,
    ASK_PROBLEM_DETAILS,
    FILLING_PROFILE,
    PROFILE_FILLED,
    SHOW_USER_PROFILE,
    SHOW_MAIN_MENU,
    SHOW_HELP_DESCRIPTION,
    SHOW_HELP_MENU;
}
