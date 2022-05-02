package com.javanix.bot.jenkinsBot.cli;

import java.util.Arrays;

public enum BuildStatus {
	IN_PROGRESS(""),
	COMPLETED_FAIL("Finished: FAILURE"),
	COMPLETED_UNSTABLE("Finished: UNSTABLE"),
	COMPLETED_ABORTED("Finished: ABORTED"),
	COMPLETED_OK("Finished: SUCCESS");

	private final String finalMessage;

	BuildStatus(String finalMessage) {
		this.finalMessage = finalMessage;
	}

	public static BuildStatus of(String message) {
		return Arrays.stream(values()).filter(buildStatus -> buildStatus.finalMessage.equals(message)).findAny().orElse(IN_PROGRESS);
	}
}
