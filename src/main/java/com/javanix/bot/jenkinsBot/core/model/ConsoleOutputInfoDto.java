package com.javanix.bot.jenkinsBot.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsoleOutputInfoDto {

	public static final String DEFAULT_RESOLVER_NAME = "default";

	private String name;
	private String failedTestPattern;
	private String executedTestPattern;
	private String unitTestsResultFilepathPrefix;
	private String fileEncoding;

	public static ConsoleOutputInfoDto.ConsoleOutputInfoDtoBuilder emptyEntityBuilder() {
		return ConsoleOutputInfoDto.builder()
				.unitTestsResultFilepathPrefix("")
				.failedTestPattern("")
				.executedTestPattern("")
				.fileEncoding("")
				.name(DEFAULT_RESOLVER_NAME);
	}
}
