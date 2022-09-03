package ru.chursinov.meetingbot.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public enum Emojis {
    LIKE(EmojiParser.parseToUnicode("\uD83D\uDC4D\uD83C\uDFFC")),
    CHECK(EmojiParser.parseToUnicode("✅")),
    NOPE(EmojiParser.parseToUnicode("❌")),
    CALENDAR(EmojiParser.parseToUnicode("\uD83D\uDDD3")),
    MEMO(EmojiParser.parseToUnicode(":memo:")),
    SUNGLASSES(EmojiParser.parseToUnicode(":sunglasses:")),
    INFO(EmojiParser.parseToUnicode(":information_source:")),
    POINT_UP(EmojiParser.parseToUnicode(":point_up:")),
    HMM(EmojiParser.parseToUnicode("\uD83E\uDD14"));

    private String emojiName;

    @Override
    public String toString() {
        return emojiName;
    }
}
