package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.command.common.EntityState;
import com.javanix.bot.jenkinsBot.command.common.EntitySubCommand;
import com.javanix.bot.jenkinsBot.core.model.EntityType;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;

interface HealthCheckSubCommand extends EntitySubCommand<HealthCheckInfoDto> {

	@Override
	default String getMainCommandName() {
		return "healthcheck";
	}

	@Override
	default EntityState<HealthCheckInfoDto> commandToState(String command) {
		return HealthCheckStateType.of(command);
	}

	@Override
	default EntityType getEntityType() {
		return EntityType.HEALTH_CHECK;
	}
}
