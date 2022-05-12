package com.javanix.bot.jenkinsBot.cli.jenkins;

import java.util.Arrays;

public enum BuildStatus {
	IN_PROGRESS("", "label.command.build.status.type.in_progress"),
	COMPLETED_FAIL("Finished: FAILURE", "label.command.build.status.type.failed"),
	COMPLETED_UNSTABLE("Finished: UNSTABLE", "label.command.build.status.type.unstable"),
	COMPLETED_ABORTED("Finished: ABORTED", "label.command.build.status.type.aborted"),
	COMPLETED_OK("Finished: SUCCESS", "label.command.build.status.type.success");

	private final String finalMessage;
	private final String messageKey;

	BuildStatus(String finalMessage, String messageKey) {
		this.finalMessage = finalMessage;
		this.messageKey = messageKey;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public static BuildStatus of(String message) {
		return Arrays.stream(values()).filter(buildStatus -> buildStatus.finalMessage.equals(message)).findAny().orElse(IN_PROGRESS);
	}
}
