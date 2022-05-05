package com.javanix.bot.jenkinsBot.core.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class UserInfoDto {
	private Long userId;
	private String userName;
	private Map<EntityType, Integer> lastMessageIdMap;
	private LocaleType locale;
	private List<String> subscribedBuildRepositories;

	public static UserInfoDto.UserInfoDtoBuilder emptyEntityBuilder() {
		return UserInfoDto.builder()
				.userName("")
				.locale(LocaleType.EN)
				.lastMessageIdMap(new HashMap<>())
				.subscribedBuildRepositories(new ArrayList<>());
	}
}
