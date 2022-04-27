package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.command.Processable;
import com.javanix.bot.jenkinsBot.command.build.add.StateType;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface BuildSubCommand extends Processable {

	String ICON_PUBLIC = "\uD83C\uDF0E ";
	String ICON_PRIVATE = "\uD83D\uDD12 ";
	String ICON_NA = "\uD83D\uDEAB";

	BuildType getBuildType();

	// TODO: to enum
	Map<String, StateType> fieldStatesMap = new HashMap<String, StateType>() {{
		put("repo.name", StateType.REPO_NAME);
		put("repo.public", StateType.PUBLIC);
		put("jenkins.domain", StateType.DOMAIN);
		put("jenkins.user", StateType.USER);
		put("jenkins.password", StateType.PASSWORD);
		put("jenkins.job", StateType.JOB_NAME);
	}};

	default InlineKeyboardMarkup buildMainMenuMarkup(List<BuildInfoDto> availableRepositories) {
		InlineKeyboardButton[][] buttons = new InlineKeyboardButton[(int) Math.ceil(availableRepositories.size() / 2.0) + 1][2];
		for (int i = 0; i < availableRepositories.size(); i++) {
			BuildInfoDto buildInfoDto = availableRepositories.get(i);
			String repoName = (buildInfoDto.getIsPublic() ? ICON_PUBLIC : ICON_PRIVATE) + buildInfoDto.getRepoName();
			buttons[i / 2][i % 2] = new InlineKeyboardButton(repoName).callbackData("/build status " + buildInfoDto.getRepoName());
		}
		if (availableRepositories.size() % 2 == 1) {
			buttons[availableRepositories.size() / 2][1] = new InlineKeyboardButton("").switchInlineQueryCurrentChat("");
		}
		buttons[buttons.length - 1][0] = new InlineKeyboardButton("Modify My Items ➡️").callbackData("/build my_list");
		buttons[buttons.length - 1][1] = new InlineKeyboardButton("").switchInlineQueryCurrentChat("");
		return new InlineKeyboardMarkup(buttons);
	}

	default InlineKeyboardMarkup buildMyRepoListMarkup(List<BuildInfoDto> availableRepositories) {
		InlineKeyboardButton[][] buttons = new InlineKeyboardButton[(int) Math.ceil(availableRepositories.size() / 2.0) + 1][2];
		for (int i = 0; i < availableRepositories.size(); i++) {
			BuildInfoDto buildInfoDto = availableRepositories.get(i);
			String repoName = (buildInfoDto.getIsPublic() ? ICON_PUBLIC : ICON_PRIVATE) + buildInfoDto.getRepoName();
			buttons[i / 2][i % 2] = new InlineKeyboardButton(repoName).callbackData("/build edit " + buildInfoDto.getRepoName());
		}
		if (availableRepositories.size() % 2 == 1) {
			buttons[availableRepositories.size() / 2][1] = new InlineKeyboardButton("").switchInlineQueryCurrentChat("");
		}
		buttons[buttons.length - 1][0] = new InlineKeyboardButton("⬅️ Back to action list️").callbackData("/build");
		buttons[buttons.length - 1][1] = new InlineKeyboardButton("Add New ✅").callbackData("/build add");
		return new InlineKeyboardMarkup(buttons);
	}

	default InlineKeyboardMarkup buildModifyRepoMarkup(BuildInfoDto repository) {
		InlineKeyboardButton[][] buttons = new InlineKeyboardButton[4][2];

		buttons[0][0] = new InlineKeyboardButton("Edit Publicity").callbackData("/build edit " + repository.getRepoName() + " repo.public");
		buttons[0][1] = new InlineKeyboardButton("Edit Jenkins Domain").callbackData("/build edit " + repository.getRepoName() + " jenkins.domain");

		buttons[1][0] = new InlineKeyboardButton("Edit Jenkins User️").callbackData("/build edit " + repository.getRepoName() + " jenkins.user");
		buttons[1][1] = new InlineKeyboardButton("Edit Jenkins Password").callbackData("/build edit " + repository.getRepoName() + " jenkins.password");

		buttons[2][0] = new InlineKeyboardButton("Edit Jenkins Job").callbackData("/build edit " + repository.getRepoName() + " jenkins.job");
		buttons[2][1] = new InlineKeyboardButton("").switchInlineQueryCurrentChat("");

		buttons[3][0] = new InlineKeyboardButton("⬅ Back to Modify My Items").callbackData("/build my_list");
		buttons[3][1] = new InlineKeyboardButton("Delete ❌️").callbackData("/build delete " + repository.getRepoName());
		return new InlineKeyboardMarkup(buttons);
	}

	default InlineKeyboardMarkup buildCreateRepoMarkup() {
		InlineKeyboardButton[][] buttons = new InlineKeyboardButton[4][2];

		buttons[0][0] = new InlineKeyboardButton("Set Repo Name").callbackData("/build add repo.name");
		buttons[0][1] = new InlineKeyboardButton("Set Publicity").callbackData("/build add repo.public");

		buttons[1][0] = new InlineKeyboardButton("Set Jenkins Domain️").callbackData("/build add jenkins.domain");
		buttons[1][1] = new InlineKeyboardButton("Set Jenkins User").callbackData("/build add jenkins.user");

		buttons[2][0] = new InlineKeyboardButton("Set Jenkins Password").callbackData("/build add jenkins.password");
		buttons[2][1] = new InlineKeyboardButton("Set Jenkins Job").callbackData("/build add jenkins.job");

		buttons[3][0] = new InlineKeyboardButton("⬅ Back to Modify My Items").callbackData("/build my_list");
		buttons[3][1] = new InlineKeyboardButton("Complete creation ✅").callbackData("/build add /done");
		return new InlineKeyboardMarkup(buttons);
	}

	default String getRepositoryDetails(BuildInfoDto repo) {
		return "Current repository info:" +
				"\n- repoName: " + getStringOrIcon(repo.getRepoName()) +
				"\n- jenkinsDomain: " + getStringOrIcon(repo.getJenkinsInfo().getDomain()) +
				"\n- jenkinsUser: " + getStringOrIcon(repo.getJenkinsInfo().getUser()) +
				"\n- jenkinsPassword: " + getStringOrIcon(repo.getJenkinsInfo().getPassword()) +
				"\n- jobName: " + getStringOrIcon(repo.getJenkinsInfo().getJobName()) +
				"\n- isPublic: " + repo.getIsPublic();
	}

	default String getStringOrIcon(String value) {
		return (value == null || value.isEmpty()) ? ICON_NA : value;
	}
}
