package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.EntityCommandFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class HealthCheckEntityCommandFactory implements EntityCommandFactory {

    private final Set<HealthCheckSubCommand> commands;
    private final DefaultHealthCheckCommand defaultCommand;

    public HealthCheckSubCommand getCommand(EntityActionType buildType) {
        return commands.stream().filter(command ->
                command.getCommandType() == buildType).findAny().orElse(defaultCommand);
    }

}
