package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UnhandledTextCommand implements TelegramCommand {

    private final Set<ProgressableCommand> progressableCommands;
    private final DefaultInProgressCommand defaultInProgressCommand;

    @Override
    public void process(TelegramBot bot, Message message) {
        progressableCommands.stream()
                .filter(progressableCommand -> progressableCommand.isInProgress(message.from().id()))
                .findFirst()
                .orElse(defaultInProgressCommand).process(bot, message);
    }

    @Override
    public String getCommandName() {
        return null;
    }
}
