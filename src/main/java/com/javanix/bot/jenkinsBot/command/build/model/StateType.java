package com.javanix.bot.jenkinsBot.command.build.model;


import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;

import java.util.Arrays;

public enum StateType {
	PUBLIC(
			"repo.public",
			BuildInfoDto::getIsPublic,
			(repo, value) -> repo.setIsPublic(Boolean.parseBoolean(value))),
	JOB_NAME(
			"jenkins.job",
			repo -> repo.getJenkinsInfo().getJobName(),
			(repo, value) -> repo.getJenkinsInfo().setJobName(value)),
	PASSWORD(
			"jenkins.password",
			repo -> repo.getJenkinsInfo().getPassword(),
			(repo, value) -> repo.getJenkinsInfo().setPassword(value)),
	USER(
			"jenkins.user",
			repo -> repo.getJenkinsInfo().getUser(),
			(repo, value) -> repo.getJenkinsInfo().setUser(value)),
	DOMAIN(
			"jenkins.domain",
			repo -> repo.getJenkinsInfo().getDomain(),
			(repo, value) -> repo.getJenkinsInfo().setDomain(value)),
	REPO_NAME(
			"repo.name",
			BuildInfoDto::getRepoName,
			BuildInfoDto::setRepoName),
	NA_ADD(
			"common.add",
			repo -> "",
			(repo, value) -> {}),
	NA_EDIT(
			"common.edit",
			repo -> "",
			(repo, value) -> {});

	private final EntityUpdateAction updateAction;
	private final EntityGetFieldValueAction getAction;
	private final String fieldKey;

	StateType(String fieldKey, EntityGetFieldValueAction getAction, EntityUpdateAction updateAction) {
		this.updateAction = updateAction;
		this.getAction = getAction;
		this.fieldKey = fieldKey;
	}

	public String getFieldKey() {
		return fieldKey;
	}

	public Object getValue(BuildInfoDto repo) {
		return getAction.get(repo);
	}

	public void updateField(BuildInfoDto repo, String value) {
		updateAction.updateEntity(repo, value);
	}

	@FunctionalInterface
	private interface EntityUpdateAction {
		void updateEntity(BuildInfoDto repo, String value);
	}

	@FunctionalInterface
	private interface EntityGetFieldValueAction {
		Object get(BuildInfoDto repo);
	}
	public static StateType of(String code, StateType fallbackValue) {
		return Arrays.stream(values())
				.filter(stateType -> stateType.fieldKey.equalsIgnoreCase(code))
				.findAny()
				.orElse(fallbackValue);
	}
}
