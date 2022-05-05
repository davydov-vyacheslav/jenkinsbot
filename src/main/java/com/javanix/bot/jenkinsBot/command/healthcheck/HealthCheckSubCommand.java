package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.command.common.EntityState;
import com.javanix.bot.jenkinsBot.command.common.EntitySubCommand;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;

public interface HealthCheckSubCommand extends EntitySubCommand<HealthCheckInfoDto> {

	@Override
	default String getMainCommandName() {
		return "healthcheck";
	}

	@Override
	default EntityState<HealthCheckInfoDto> commandToState(String command) {
		return HealthCheckStateType.of(command);
	}
}
