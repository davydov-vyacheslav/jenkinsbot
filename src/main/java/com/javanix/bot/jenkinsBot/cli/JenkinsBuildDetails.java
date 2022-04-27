package com.javanix.bot.jenkinsBot.cli;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class JenkinsBuildDetails {
	private Long runTestsCount;
	private Long failedTestsCount;
	private List<String> topFailedTests;
	private Integer failedTestsCapacity;

	public void incrementRunTestCount() {
		runTestsCount++;
	}

	public void addFailedTest(String value) {
		failedTestsCount++;
		if (topFailedTests.size() < failedTestsCapacity) {
			topFailedTests.add(value);
		}
	}
}
