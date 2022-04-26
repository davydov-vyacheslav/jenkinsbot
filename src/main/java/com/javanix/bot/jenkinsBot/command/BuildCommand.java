package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.command.build.BuildCommandFactory;
import com.javanix.bot.jenkinsBot.command.build.BuildType;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class BuildCommand implements TelegramCommand {

    private final BuildCommandFactory buildCommandFactory;

    @Override
    public void process(TelegramBot bot, Message message) {

        // assuming income message is '@botname /build status teamname'

        String messageString = message.text();

        // assuming to have 'status teamname'
        String buildTypeAndArguments = messageString.substring(messageString.lastIndexOf(getCommandName()) + getCommandName().length()).trim();
        BuildType buildType = getBuildType(buildTypeAndArguments);

        // assuming to have 'teamname'
        String buildArguments = "";
        if (buildType != null) {
            buildArguments = buildTypeAndArguments.substring(buildTypeAndArguments.toUpperCase().indexOf(buildType.toString()) + buildType.toString().length()).trim();
        }

        buildCommandFactory.getCommand(buildType).process(bot, message, buildArguments);
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
