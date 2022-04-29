package com.javanix.bot.jenkinsBot.command.build.model;


import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;

public enum StateType {
    PUBLIC(
            "Set repo.isPublic field",
            (repo, value) -> repo.setIsPublic(Boolean.parseBoolean(value))),
    JOB_NAME(
            "Set jobName field",
            (repo, value) -> repo.getJenkinsInfo().setJobName(value)),
    PASSWORD(
            "Set password field",
            (repo, value) -> repo.getJenkinsInfo().setPassword(value)),
    USER(
            "Set user field",
            (repo, value) -> repo.getJenkinsInfo().setUser(value)),
    DOMAIN(
            "Set domain field",
            (repo, value) -> repo.getJenkinsInfo().setDomain(value)),
    REPO_NAME(
            "Set repoName field",
            BuildInfoDto::setRepoName),
    NA_ADD(
            "Adding the entity",
            (repo, value) -> {}),
    NA_EDIT(
            "Modifying the entity",
            (repo, value) -> {});

    private final EntityUpdateAction updateAction;
    private final String info;

    StateType(String info, EntityUpdateAction updateAction) {
        this.info = info;
        this.updateAction = updateAction;
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
        StateType result = fallbackValue;
        switch (code) {
            case "repo.name": result = StateType.REPO_NAME; break;
            case "repo.public": result = StateType.PUBLIC; break;
            case "jenkins.domain": result = StateType.DOMAIN; break;
            case "jenkins.user": result = StateType.USER; break;
            case "jenkins.password": result = StateType.PASSWORD; break;
            case "jenkins.job": result = StateType.JOB_NAME; break;
        }
        return result;
    }
}