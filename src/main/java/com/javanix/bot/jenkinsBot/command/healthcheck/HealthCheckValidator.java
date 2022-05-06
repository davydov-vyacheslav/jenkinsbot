package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.validation.EmptyValidator;
import com.javanix.bot.jenkinsBot.command.common.validation.EntityValidator;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class HealthCheckValidator implements EntityValidator<HealthCheckInfoDto> {

	private final EmptyValidator emptyValidator;
	private final HealthCheckService database;

	@Override
	public boolean validate(HealthCheckInfoDto target, List<String> errors, EntityActionType actionType) {

		// required fields check
		emptyValidator.validate(target.getEndpointName(), errors, "error.command.healthcheck.validation.required.name");
		emptyValidator.validate(target.getEndpointUrl(), errors, "error.command.healthcheck.validation.required.url");

		// TODO: Url validator
		// TODO: unique validator
		if (actionType == EntityActionType.ADD && database.hasEntity(target.getEndpointName())) {
			errors.add("error.command.healthcheck.validation.invalid.name");
		}
		return errors.isEmpty();
	}
}
