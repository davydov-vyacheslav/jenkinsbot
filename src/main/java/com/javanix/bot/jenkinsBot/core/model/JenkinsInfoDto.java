package com.javanix.bot.jenkinsBot.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JenkinsInfoDto {
	private String domain;
	private String user;
	private String password;
	private String jobName;
}
