package com.javanix.bot.jenkinsBot.command.build.validator;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;

import java.util.List;

public interface Validator {

	// FIXME: is that valid way?
	default boolean validate(BuildInfoDto target, List<String> errors) {
		JenkinsInfoDto jenkinsInfo = target.getJenkinsInfo();

		// required fields check
		validateEmptiness(target.getRepoName(), errors, "Repo Name is required");
		validateEmptiness(jenkinsInfo.getJobName(), errors, "Jenkins Job Name is required");
		validateEmptiness(jenkinsInfo.getDomain(), errors, "Jenkins Domain Name is required");

		// other checks
		validateSpaces(jenkinsInfo.getJobName(), errors, "Jenkins Job Name is invalid");
		validateSpaces(jenkinsInfo.getUser(), errors, "Jenkins User Name is invalid");
		validateSpaces(jenkinsInfo.getPassword(), errors, "Jenkins Password is invalid");
		validateSpaces(jenkinsInfo.getDomain(), errors, "Jenkins Domain Name is invalid");

		return errors.isEmpty();
	}

	default void validateEmptiness(String fieldValue, List<String> errors, String validationMessage) {
		if (fieldValue == null || fieldValue.isEmpty()) {
			errors.add(validationMessage);
		}
	}

	default void validateSpaces(String fieldValue, List<String> errors, String validationMessage) {
		if (fieldValue.contains(" ")) {
			errors.add(validationMessage);
		}
	}
}
