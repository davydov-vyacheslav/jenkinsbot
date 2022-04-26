package com.javanix.bot.jenkinsBot.command.build.add;


import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;

public enum StateType {
    COMPLETED(
            null,
            "You are done. Congratulations",
            (database, value) -> true,
            (repo, value) -> {}),
    PUBLIC(
            COMPLETED,
            "Please enter whether this repository is public ('true' / 'false')",
            (database, value) -> true,
            (repo, value) -> repo.setIsPublic(Boolean.parseBoolean(value))),
    JOB_NAME(
            PUBLIC,
            "Please enter Jenkins job name (e.g. 'Insight')",
            (database, value) -> !value.contains(" "),
            (repo, value) -> repo.getJenkinsInfo().setJobName(value)),
    PASSWORD(
            JOB_NAME,
            "Please enter Jenkins password. Enter '!' value if no credentials required",
            (database, value) -> !value.contains(" "),
            (repo, value) -> repo.getJenkinsInfo().setPassword(value)),
    USER(
            PASSWORD,
            "Please enter Jenkins user (e.g. 'admin'). Enter '!' value if no credentials required",
            (database, value) -> !value.contains(" "),
            (repo, value) -> repo.getJenkinsInfo().setUser(value)),
    DOMAIN(
            USER,
            "Please enter Jenkins domain name (e.g. 'dev-rim-chf01')",
            (database, value) -> !value.contains(" "),
            (repo, value) -> repo.getJenkinsInfo().setDomain(value)),
    REPO_NAME(
            DOMAIN,
            "Please enter repository name (unique, without spaces)",
            (database, value) -> !value.contains(" ") && !database.hasRepository(value),
            BuildInfoDto::setRepoName),
    INITIAL(
            REPO_NAME,
            "",
            (database, value) -> true,
            (repo, value) -> {});

    private final StateType nextState;
    private final String message;
    private final Validator validator;
    private final EntityUpdateAction updateAction;

    StateType(StateType nextState, String message, Validator validator, EntityUpdateAction updateAction) {
        this.nextState = nextState;
        this.message = message;
        this.validator = validator;
        this.updateAction = updateAction;
    }

    public StateType getNextState() {
        return nextState;
    }

    public String getMessage() {
        return message;
    }

    public boolean isValid(BuildInfoService database, String value) {
        return validator.isValid(database, value);
    }

    public void performUpdate(BuildInfoDto repo, String value) {
        updateAction.updateEntity(repo, value);
    }

    @FunctionalInterface
    private interface Validator {
        boolean isValid(BuildInfoService database, String value);
    }

    @FunctionalInterface
    private interface EntityUpdateAction {
        void updateEntity(BuildInfoDto repo, String value);
    }

}
