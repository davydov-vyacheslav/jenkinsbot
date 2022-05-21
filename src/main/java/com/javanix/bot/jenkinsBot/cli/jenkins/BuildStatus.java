package com.javanix.bot.jenkinsBot.cli.jenkins;

public enum BuildStatus {
	NA("label.command.build.status.type.na"),
	BROKEN("label.command.build.status.type.broken"),

	IN_PROGRESS("label.command.build.status.type.in_progress"),
	COMPLETED_FAIL("label.command.build.status.type.failed"),
	COMPLETED_UNSTABLE("label.command.build.status.type.unstable"),
	COMPLETED_ABORTED("label.command.build.status.type.aborted"),
	COMPLETED_OK("label.command.build.status.type.success");

	private final String messageKey;

	BuildStatus(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getMessageKey() {
		return messageKey;
	}

}
