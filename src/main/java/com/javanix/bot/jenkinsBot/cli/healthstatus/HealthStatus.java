package com.javanix.bot.jenkinsBot.cli.healthstatus;

public enum HealthStatus {
	SUCCESS("label.command.healthcheck.status.type.success"),
	NA("label.command.healthcheck.status.type.na"),
	UNSTABLE("label.command.healthcheck.status.type.unstable"),
	DOWN("label.command.healthcheck.status.type.down");

	private final String messageKey;

	HealthStatus(String messageKey) {
		this.messageKey = messageKey;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public static HealthStatus of(int code) {
		HealthStatus result = UNSTABLE;
		if (code >= 200 && code <= 299) {
			result = SUCCESS;
		}
		return result;
	}

}
