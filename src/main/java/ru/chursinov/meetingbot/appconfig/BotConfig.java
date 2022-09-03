package ru.chursinov.meetingbot.appconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import ru.chursinov.meetingbot.MeetingTelegramBot;
import ru.chursinov.meetingbot.botapi.TelegramFacade;


@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "telegrambot")
public class BotConfig {
    private String webHookPath;
    private String botUserName;
    private String botToken;

    @Bean
    public MeetingTelegramBot meetingTelegramBot(TelegramFacade telegramFacade) {

        MeetingTelegramBot meetingTelegramBot = new MeetingTelegramBot(telegramFacade);
        meetingTelegramBot.setBotUserName(botUserName);
        meetingTelegramBot.setBotToken(botToken);
        meetingTelegramBot.setWebHookPath(webHookPath);

        return meetingTelegramBot;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
