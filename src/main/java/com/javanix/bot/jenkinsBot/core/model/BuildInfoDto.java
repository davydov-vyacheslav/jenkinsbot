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
}
