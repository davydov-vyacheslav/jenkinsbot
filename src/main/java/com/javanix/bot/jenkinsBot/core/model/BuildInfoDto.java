package com.javanix.bot.jenkinsBot.core.model;

import lombok.Builder;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
public class BuildInfoDto implements Entity {
	private String repoName;
	private JenkinsInfoDto jenkinsInfo;
	private Boolean isPublic;
	private Long creatorId;
	private String creatorFullName;
	private Set<Long> referencedByUsers;

	public static BuildInfoDto.BuildInfoDtoBuilder emptyEntityBuilder() {
		return BuildInfoDto.builder()
				.repoName("")
				.referencedByUsers(new LinkedHashSet<>())
				.jenkinsInfo(JenkinsInfoDto.emptyEntityBuilder().build())
				.isPublic(false);
	}

	@Override
	public boolean isPublic() {
		return isPublic;
	}

	@Override
	public String getName() {
		return repoName;
	}

	@Override
	public Set<Long> getReferences() {
		return referencedByUsers;
	}

}
