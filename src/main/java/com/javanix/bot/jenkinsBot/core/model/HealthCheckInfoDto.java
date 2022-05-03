package com.javanix.bot.jenkinsBot.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthCheckInfoDto {
	private String endpointName;
	private String endpointUrl;
	private Boolean isPublic;
	private Long creatorId;
	private String creatorFullName;
}
