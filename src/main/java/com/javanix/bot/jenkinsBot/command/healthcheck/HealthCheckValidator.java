package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.validation.EmptyValidator;
import com.javanix.bot.jenkinsBot.command.common.validation.UniqueValidator;
import com.javanix.bot.jenkinsBot.command.common.validation.UrlValidator;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.validation.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class HealthCheckValidator implements EntityValidator<HealthCheckInfoDto> {

	private final EmptyValidator emptyValidator;
	private final UniqueValidator uniqueValidator;
	private final UrlValidator urlValidator;

	@Override
	public boolean validate(HealthCheckInfoDto target, List<String> errors, EntityActionType actionType) {

		emptyValidator.validate(target.getEndpointName(), errors, "error.command.healthcheck.validation.required.name");
		emptyValidator.validate(target.getEndpointUrl(), errors, "error.command.healthcheck.validation.required.url");
		urlValidator.validate(target.getEndpointUrl(), errors, "error.command.healthcheck.validation.invalid.url");

		if (actionType == EntityActionType.ADD) {
			uniqueValidator.validate(target, errors, "error.command.healthcheck.validation.invalid.name");
		}
		return errors.isEmpty();
	}
}
