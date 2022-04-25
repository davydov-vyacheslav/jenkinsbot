package com.javanix.bot.jenkinsBot.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class CommandFactory {

    private final Set<TelegramCommand> commands;
    private final UnknownCommand defaultCommand;

    public TelegramCommand getCommand(String name) {
        return commands.stream().filter(command ->
                name.contains(command.getCommandName())).findAny().orElse(defaultCommand);
    }

    // TODO: support emoji aliases

    // TODO: bypass this chain when AddBuildCommand in progress

}
