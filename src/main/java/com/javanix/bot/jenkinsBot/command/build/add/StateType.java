package com.javanix.bot.jenkinsBot.command.build.add;


import com.javanix.bot.jenkinsBot.database.BuildRepository;
import com.javanix.bot.jenkinsBot.database.DatabaseSource;

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
            BuildRepository::setJobName),
    PASSWORD(
            JOB_NAME,
            "Please enter Jenkins password. Leave empty value if no credentials required",
            (database, value) -> !value.contains(" "),
            BuildRepository::setJenkinsPassword),
    USER(
            PASSWORD,
            "Please enter Jenkins user (e.g. 'admin'). Leave empty value if no credentials required",
            (database, value) -> !value.contains(" "),
            BuildRepository::setJenkinsUser),
    DOMAIN(
            USER,
            "Please enter Jenkins domain name (e.g. 'dev-rim-chf01')",
            (database, value) -> !value.contains(" "),
            BuildRepository::setJenkinsDomain),
    REPO_NAME(
            DOMAIN,
            "Please enter repository name (unique, without spaces)",
            (database, value) -> !value.contains(" ") && database.getRepositoryByNameIgnoreCase(value) == null,
            BuildRepository::setRepoName),
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

    public boolean isValid(DatabaseSource database, String value) {
        return validator.isValid(database, value);
    }

    public void performUpdate(BuildRepository repo, String value) {
        updateAction.updateEntity(repo, value);
    }

    @FunctionalInterface
    private interface Validator {
        boolean isValid(DatabaseSource database, String value);
    }

    @FunctionalInterface
    private interface EntityUpdateAction {
        void updateEntity(BuildRepository repo, String value);
    }

}
