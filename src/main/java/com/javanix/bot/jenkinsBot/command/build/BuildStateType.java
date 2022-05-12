package com.javanix.bot.jenkinsBot.command.build;


import com.javanix.bot.jenkinsBot.command.common.EntityState;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;

import java.util.Arrays;

enum BuildStateType implements EntityState<BuildInfoDto> {
	PUBLIC(
			"repo.public",
			BuildInfoDto::isPublic,
			(repo, value) -> repo.setIsPublic(Boolean.parseBoolean(value))),
	JOB_URL(
			"jenkins.jobUrl",
			repo -> repo.getJenkinsInfo().getJobUrl(),
			(repo, value) -> repo.getJenkinsInfo().setJobUrl(value)),
	PASSWORD(
			"jenkins.password",
			repo -> repo.getJenkinsInfo().getPassword(),
			(repo, value) -> repo.getJenkinsInfo().setPassword(value)),
	USER(
			"jenkins.user",
			repo -> repo.getJenkinsInfo().getUser(),
			(repo, value) -> repo.getJenkinsInfo().setUser(value)),
	CONSOLE_OUTPUT_TYPE(
			"jenkins.console.type",
			repo -> repo.getJenkinsInfo().getConsoleOutputInfo().getName(),
			(repo, value) -> repo.getJenkinsInfo().getConsoleOutputInfo().setName(value)),
	REPO_NAME(
			"repo.name",
			BuildInfoDto::getRepoName,
			BuildInfoDto::setRepoName);

	private final EntityUpdateAction<BuildInfoDto> updateAction;
	private final EntityGetFieldValueAction<BuildInfoDto> getAction;
	private final String fieldKey;

	BuildStateType(String fieldKey, EntityGetFieldValueAction<BuildInfoDto> getAction, EntityUpdateAction<BuildInfoDto> updateAction) {
		this.updateAction = updateAction;
		this.getAction = getAction;
		this.fieldKey = fieldKey;
	}

	@Override
	public String getFieldKey() {
		return fieldKey;
	}

	@Override
	public Object getValue(BuildInfoDto repo) {
		return getAction.get(repo);
	}

	@Override
	public void updateField(BuildInfoDto repo, String value) {
		updateAction.updateEntity(repo, value);
	}

	public static BuildStateType of(String code) {
		return Arrays.stream(values())
				.filter(stateType -> stateType.fieldKey.equalsIgnoreCase(code))
				.findAny()
				.orElse(null);
	}

}
