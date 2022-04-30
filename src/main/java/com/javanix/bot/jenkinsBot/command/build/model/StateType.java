package com.javanix.bot.jenkinsBot.command.build.model;


import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;

import java.util.Arrays;

public enum StateType {
	PUBLIC(
			"Set repo.isPublic field",
			"Publicity",
			"repo.public",
			(repo, value) -> repo.setIsPublic(Boolean.parseBoolean(value))),
	JOB_NAME(
			"Set jobName field",
			"Jenkins Job",
			"jenkins.job",
			(repo, value) -> repo.getJenkinsInfo().setJobName(value)),
	PASSWORD(
			"Set password field",
			"Jenkins Password",
			"jenkins.password",
			(repo, value) -> repo.getJenkinsInfo().setPassword(value)),
	USER(
			"Set user field",
			"Jenkins User",
			"jenkins.user",
			(repo, value) -> repo.getJenkinsInfo().setUser(value)),
	DOMAIN(
			"Set domain field",
			"Jenkins Domain️",
			"jenkins.domain",
			(repo, value) -> repo.getJenkinsInfo().setDomain(value)),
	REPO_NAME(
			"Set repoName field",
			"Repo Name",
			"repo.name",
			BuildInfoDto::setRepoName),
	NA_ADD(
			"Adding the entity",
			"", "not applicable",
			(repo, value) -> {}),
	NA_EDIT(
			"Modifying the entity",
			"", "not applicable",
			(repo, value) -> {});

	private final EntityUpdateAction updateAction;
	private final String info;
	private final String fieldName;
	private final String fieldKey;

	StateType(String info, String fieldName, String fieldKey, EntityUpdateAction updateAction) {
		this.info = info;
		this.updateAction = updateAction;
		this.fieldKey = fieldKey;
		this.fieldName = fieldName;
	}

	public String getFieldKey() {
		return fieldKey;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void updateField(BuildInfoDto repo, String value) {
		updateAction.updateEntity(repo, value);
	}

	public String getInfo() {
		return info;
	}

	@FunctionalInterface
	private interface EntityUpdateAction {
		void updateEntity(BuildInfoDto repo, String value);
	}

	public static StateType of(String code, StateType fallbackValue) {
		return Arrays.stream(values())
				.filter(stateType -> stateType.fieldKey.equalsIgnoreCase(code))
				.findAny()
				.orElse(fallbackValue);
	}
}
