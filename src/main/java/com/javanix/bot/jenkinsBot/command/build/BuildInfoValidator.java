package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.command.common.validation.EmptyValidator;
import com.javanix.bot.jenkinsBot.command.common.validation.EntityValidator;
import com.javanix.bot.jenkinsBot.command.common.validation.SpaceValidator;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BuildInfoValidator implements EntityValidator<BuildInfoDto> {

	private final EmptyValidator emptyValidator;
	private final SpaceValidator spaceValidator;
	private final BuildInfoService database;

	@Override
	public boolean validate(BuildInfoDto target, List<String> errors, EntityActionType actionType) {
		JenkinsInfoDto jenkinsInfo = target.getJenkinsInfo();

		// TODO: error.command.build.validation.{required,invalid}. + field.getFieldKey

		// required fields check
		emptyValidator.validate(target.getRepoName(), errors, "error.command.build.validation.required.repo.name");
		emptyValidator.validate(jenkinsInfo.getJobName(), errors, "error.command.build.validation.required.jenkins.job");
		emptyValidator.validate(jenkinsInfo.getDomain(), errors, "error.command.build.validation.required.domain");

		// other checks
		spaceValidator.validate(jenkinsInfo.getJobName(), errors, "error.command.build.validation.invalid.jenkins.job");
		spaceValidator.validate(jenkinsInfo.getUser(), errors, "error.command.build.validation.invalid.jenkins.user");
		spaceValidator.validate(jenkinsInfo.getPassword(), errors, "error.command.build.validation.invalid.jenkins.password");
		spaceValidator.validate(jenkinsInfo.getDomain(), errors, "error.command.build.validation.invalid.jenkins.domain");

		// TODO: another validator
		if (actionType == EntityActionType.ADD && database.hasEntity(target.getRepoName())) {
			errors.add("error.command.build.validation.invalid.repo.name");
		}
		return errors.isEmpty();
	}
}
