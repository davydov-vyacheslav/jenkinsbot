package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.Processable;
import com.javanix.bot.jenkinsBot.command.build.model.BuildType;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface BuildSubCommand extends Processable {

	String ICON_PUBLIC = "\uD83C\uDF0E ";
	String ICON_PRIVATE = "\uD83D\uDD12 ";

	BuildType getBuildType();

	default <T> Stream<List<T>> splitListByNElements(int pageSize, List<T> fields) {
		return IntStream.range(0, (fields.size() + pageSize - 1) / pageSize)
				.mapToObj(i -> fields.subList(i * pageSize, Math.min(pageSize * (i + 1), fields.size())));
	}

	default void groupRepositoriesBy(List<BuildInfoDto> repositories, int pageSize, InlineKeyboardMarkup inlineKeyboardMarkup, String callbackPrefix) {
		splitListByNElements(pageSize, repositories)
				.forEach(buildInfoDtos -> inlineKeyboardMarkup.addRow(
						buildInfoDtos.stream()
								.map(buildInfoDto -> {
									String repoName = (buildInfoDto.getIsPublic() ? ICON_PUBLIC : ICON_PRIVATE) + buildInfoDto.getRepoName();
									return new InlineKeyboardButton(repoName).callbackData(callbackPrefix + buildInfoDto.getRepoName());
								})
								.toArray(InlineKeyboardButton[]::new)));
	}

}
