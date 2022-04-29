package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.build.model.BuildType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class BuildCommandFactory {

    private final Set<BuildSubCommand> commands;
    private final DefaultBuildCommand defaultCommand;

    public BuildSubCommand getCommand(BuildType buildType) {
        return commands.stream().filter(command ->
                command.getBuildType() == buildType).findAny().orElse(defaultCommand);
    }

}
