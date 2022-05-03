package com.javanix.bot.jenkinsBot.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class CommonCommandFactory {

    private final Set<TelegramCommand> commands;
    private final UnknownCommand unknownCommand;
    private final UnhandledTextCommand unhandledTextCommand;

    public TelegramCommand getCommand(String name) {
        return commands.stream()
                .filter(command -> command.getCommandName() != null)
                // not starts with because it can be like `@jenkins_test1_bot /build STATUS`
                .filter(command -> name.contains(command.getCommandName()))
                .findAny().orElseGet(() -> {
                    if (name.startsWith("/")) {
                        return unknownCommand;
                    } else {
                        return unhandledTextCommand;
                    }
                });
    }

}
