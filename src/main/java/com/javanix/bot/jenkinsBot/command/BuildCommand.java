package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.command.build.BuildCommandFactory;
import com.javanix.bot.jenkinsBot.command.build.BuildType;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component
@RequiredArgsConstructor
class BuildCommand implements TelegramCommand {

    private final BuildCommandFactory buildCommandFactory;

    private static final Pattern buildCommandPattern = Pattern.compile(".*/build.?(add|edit|status|delete|my_list).?(.*)", CASE_INSENSITIVE);

    @Override
    public void process(TelegramBot bot, Chat chat, User from, String message) {

        // assuming income message is '@botname /build status teamname'
        Matcher m = buildCommandPattern.matcher(message);
        BuildType buildType = null;
        String buildArguments = "";
        if (m.find()) {
            buildType = getBuildType(m.group(1));
            buildArguments = m.group(2);
        }

        buildCommandFactory.getCommand(buildType).process(bot, chat, from, buildArguments);
    }


    private BuildType getBuildType(String message) {
        BuildType type = null;
        try {
            type = BuildType.valueOf(message.trim().split(" ")[0].toUpperCase());
        } catch (IllegalArgumentException iae) {
            // no op
        }
        return type;
    }

    @Override
    public String getCommandName() {
        return "/build";
    }
}
