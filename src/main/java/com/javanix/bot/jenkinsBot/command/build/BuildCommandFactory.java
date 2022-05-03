package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.common.CommandFactory;
import com.javanix.bot.jenkinsBot.command.common.CommonEntityActionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class BuildCommandFactory implements CommandFactory {

    private final Set<BuildSubCommand> commands;
    private final DefaultBuildCommand defaultCommand;

    public BuildSubCommand getCommand(CommonEntityActionType buildType) {
        return commands.stream().filter(command ->
                command.getBuildType() == buildType).findAny().orElse(defaultCommand);
    }

}
