package com.javanix.bot.jenkinsBot.cli.jenkins;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class JenkinsBuildDetails {
	private Integer runTestsCount;
	private Set<String> failedTests;
	private BuildStatus buildStatus;

	public void incrementRunTestCount() {
		runTestsCount++;
	}

	public void addFailedTest(String value) {
		failedTests.add(value);
	}
}
