package com.javanix.bot.jenkinsBot.core.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthCheckInfoDto implements Entity {
	private String endpointName;
	private String endpointUrl;
	private Boolean isPublic;
	private Long creatorId;
	private String creatorFullName;

	public static HealthCheckInfoDto.HealthCheckInfoDtoBuilder emptyEntityBuilder() {
		return HealthCheckInfoDto.builder()
				.endpointName("")
				.endpointUrl("")
				.isPublic(false);
	}

	@Override
	public boolean isPublic() {
		return isPublic;
	}

	@Override
	public String getName() {
		return endpointName;
	}
}
