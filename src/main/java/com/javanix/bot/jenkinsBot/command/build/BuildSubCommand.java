package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.common.EntityState;
import com.javanix.bot.jenkinsBot.command.common.EntitySubCommand;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.EntityType;

interface BuildSubCommand extends EntitySubCommand<BuildInfoDto> {

	@Override
	default String getMainCommandName() {
		return "build";
	}

	@Override
	default EntityState<BuildInfoDto> commandToState(String command) {
		return BuildStateType.of(command);
	}

	@Override
	default EntityType getEntityType() {
		return EntityType.BUILD_INFO;
	}
}
