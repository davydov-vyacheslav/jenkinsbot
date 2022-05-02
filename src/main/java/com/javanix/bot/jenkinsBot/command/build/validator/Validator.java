package com.javanix.bot.jenkinsBot.command.build.validator;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;

import java.util.List;

public interface Validator {

	// FIXME: is that valid way?
	default boolean validate(BuildInfoDto target, List<String> errors) {
		JenkinsInfoDto jenkinsInfo = target.getJenkinsInfo();

		// TODO: error.command.build.validation.{required,invalid}. + field.getFieldKey

		// required fields check
		validateEmptiness(target.getRepoName(), errors, "error.command.build.validation.required.repo.name");
		validateEmptiness(jenkinsInfo.getJobName(), errors, "error.command.build.validation.required.jenkins.job");
		validateEmptiness(jenkinsInfo.getDomain(), errors, "error.command.build.validation.required.domain");

		// other checks
		validateSpaces(jenkinsInfo.getJobName(), errors, "error.command.build.validation.invalid.jenkins.job");
		validateSpaces(jenkinsInfo.getUser(), errors, "error.command.build.validation.invalid.jenkins.user");
		validateSpaces(jenkinsInfo.getPassword(), errors, "error.command.build.validation.invalid.jenkins.password");
		validateSpaces(jenkinsInfo.getDomain(), errors, "error.command.build.validation.invalid.jenkins.domain");

		return errors.isEmpty();
	}

	default void validateEmptiness(String fieldValue, List<String> errors, String validationMessage) {
		if (fieldValue == null || fieldValue.isEmpty()) {
			errors.add(validationMessage);
		}
	}

	default void validateSpaces(String fieldValue, List<String> errors, String validationMessage) {
		if (fieldValue != null && fieldValue.contains(" ")) {
			errors.add(validationMessage);
		}
	}
}
