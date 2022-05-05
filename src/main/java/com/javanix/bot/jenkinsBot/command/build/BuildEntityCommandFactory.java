package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.EntityCommandFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class BuildEntityCommandFactory implements EntityCommandFactory {

    private final Set<BuildSubCommand> commands;
    private final DefaultBuildCommand defaultCommand;

    public BuildSubCommand getCommand(EntityActionType buildType) {
        return commands.stream().filter(command ->
                command.getCommandType() == buildType).findAny().orElse(defaultCommand);
    }

}
