package com.javanix.bot.jenkinsBot.command.build.model;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.JenkinsInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BuildInfoValidator {

	private final BuildInfoService database;


// FIXME: find gracier way to do that
	public boolean validate(BuildInfoDto target, BuildType type, List<String> errors) {
		JenkinsInfoDto jenkinsInfo = target.getJenkinsInfo();

		// required fields check

		if (target.getRepoName().isEmpty()) {
			errors.add("Repo Name is required");
		}

		if (jenkinsInfo.getJobName().isEmpty()) {
			errors.add("Jenkins Job Name is required");
		}

		if (jenkinsInfo.getDomain().isEmpty()) {
			errors.add("Jenkins Domain Name is required");
		}

		if (target.getRepoName().isEmpty()) {
			errors.add("Repo Name is required");
		}


		// other checks
		if (jenkinsInfo.getJobName().contains(" ")) {
			errors.add("Jenkins Job Name is invalid");
		}

		if (jenkinsInfo.getUser().contains(" ")) {
			errors.add("Jenkins User Name is invalid");
		}

		if (jenkinsInfo.getPassword().contains(" ")) {
			errors.add("Jenkins Password is invalid");
		}

		if (jenkinsInfo.getDomain().contains(" ")) {
			errors.add("Jenkins Domain Name is invalid");
		}

		if (type == BuildType.ADD && (target.getRepoName().contains(" ") || database.hasRepository(target.getRepoName()))) {
			errors.add("Repo Name is invalid (or not unique)");
		}

		return errors.isEmpty();
	}


}
