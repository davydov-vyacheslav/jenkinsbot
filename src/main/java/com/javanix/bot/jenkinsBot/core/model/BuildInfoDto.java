package com.javanix.bot.jenkinsBot.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuildInfoDto {
	private String repoName;
	private JenkinsInfoDto jenkinsInfo;
	private Boolean isPublic;
	private Long creatorId;
	private String creatorFullName;

	public static BuildInfoDto.BuildInfoDtoBuilder emptyEntityBuilder() {
		return BuildInfoDto.builder()
				.repoName("")
				.jenkinsInfo(JenkinsInfoDto.builder()
						.jobName("")
						.domain("")
						.password("")
						.user("")
						.build())
				.isPublic(false);
	}
}
