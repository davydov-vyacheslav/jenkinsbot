package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.command.common.CommandFactory;
import com.javanix.bot.jenkinsBot.command.common.CommonEntityActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class HealthCheckCommandFactory implements CommandFactory {

    private final Set<HealthCheckSubCommand> commands;
    private final DefaultHealthCheckCommand defaultCommand;

    public HealthCheckSubCommand getCommand(CommonEntityActionType buildType) {
        return commands.stream().filter(command ->
                command.getBuildType() == buildType).findAny().orElse(defaultCommand);
    }

}
