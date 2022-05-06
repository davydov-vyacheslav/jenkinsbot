package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.validation.EmptyValidator;
import com.javanix.bot.jenkinsBot.command.common.validation.EntityValidator;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class BuildInfoValidator implements EntityValidator<BuildInfoDto> {

	private final EmptyValidator emptyValidator;
	private final BuildInfoService database;

	@Override
	public boolean validate(BuildInfoDto target, List<String> errors, EntityActionType actionType) {
		JenkinsInfoDto jenkinsInfo = target.getJenkinsInfo();

		// required fields check
		emptyValidator.validate(target.getRepoName(), errors, "error.command.build.validation.required.repo.name");
		emptyValidator.validate(jenkinsInfo.getJobUrl(), errors, "error.command.build.validation.required.jenkins.jobUrl");

		// TODO: Url validator
		// TODO: unique validator
		if (actionType == EntityActionType.ADD && database.hasEntity(target.getRepoName())) {
			errors.add("error.command.build.validation.invalid.repo.name");
		}
		return errors.isEmpty();
	}
}
