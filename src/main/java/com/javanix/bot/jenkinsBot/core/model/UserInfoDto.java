package com.javanix.bot.jenkinsBot.core.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class UserInfoDto {
	private Long userId;
	private String userName;
	private Integer buildMenuLastMessageId;
	private LocaleType locale;
	private List<String> subscribedBuildRepositories;

	public static UserInfoDto.UserInfoDtoBuilder emptyEntityBuilder() {
		return UserInfoDto.builder()
				.userName("")
				.locale(LocaleType.EN)
				.subscribedBuildRepositories(new ArrayList<>());
	}
}
