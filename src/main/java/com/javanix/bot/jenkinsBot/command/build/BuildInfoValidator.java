package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.validation.EmptyValidator;
import com.javanix.bot.jenkinsBot.command.common.validation.UniqueValidator;
import com.javanix.bot.jenkinsBot.command.common.validation.UrlValidator;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.validation.EntityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class BuildInfoValidator implements EntityValidator<BuildInfoDto> {

	private final EmptyValidator emptyValidator;
	private final UniqueValidator uniqueValidator;
	private final UrlValidator urlValidator;

	@Override
	public boolean validate(BuildInfoDto target, List<String> errors, EntityActionType actionType) {
		emptyValidator.validate(target.getRepoName(), errors, "error.command.build.validation.required.repo.name");
		emptyValidator.validate(target.getJenkinsInfo().getJobUrl(), errors, "error.command.build.validation.required.jenkins.jobUrl");
		urlValidator.validate(target.getJenkinsInfo().getJobUrl(), errors, "error.command.build.validation.invalid.jenkins.jobUrl");

		if (actionType == EntityActionType.ADD) {
			uniqueValidator.validate(target, errors, "error.command.build.validation.invalid.repo.name");
		}
		return errors.isEmpty();
	}
}
