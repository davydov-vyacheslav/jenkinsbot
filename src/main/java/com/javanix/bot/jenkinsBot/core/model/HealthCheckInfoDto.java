package com.javanix.bot.jenkinsBot.core.model;

import lombok.Builder;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class HealthCheckInfoDto implements Entity {
	private String endpointName;
	private String endpointUrl;
	private Boolean isPublic;
	private Long creatorId;
	private String creatorFullName;
	private Set<Long> referencedByUsers;

	public static HealthCheckInfoDto.HealthCheckInfoDtoBuilder emptyEntityBuilder() {
		return HealthCheckInfoDto.builder()
				.endpointName("")
				.endpointUrl("")
				.referencedByUsers(new HashSet<>())
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

	@Override
	public Set<Long> getReferences() {
		return referencedByUsers;
	}
}
