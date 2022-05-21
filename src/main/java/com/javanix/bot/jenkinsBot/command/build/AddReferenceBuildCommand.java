package com.javanix.bot.jenkinsBot.command.build;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.common.AbstractAddReferenceCommand;
import com.javanix.bot.jenkinsBot.command.common.UserEntityContext;
import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddReferenceBuildCommand extends AbstractAddReferenceCommand<BuildInfoDto> implements BuildSubCommand {

	public AddReferenceBuildCommand(BuildInfoService database, UserEntityContext userContext, DefaultBuildCommand defaultCommand, TelegramBotWrapper bot) {
		super(database, userContext, defaultCommand, bot);
	}

	@Override
	protected InlineKeyboardMarkup buildRepoListMarkup(User from, List<BuildInfoDto> availableRepositories) {

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		groupEntitiesBy(availableRepositories, from.id(), 2, inlineKeyboardMarkup, "/build add_reference ");
		inlineKeyboardMarkup.addRow(
				new InlineKeyboardButton(bot.getI18nMessage(from, "button.common.backToActionList")).callbackData("/build")
		);

		return inlineKeyboardMarkup;
	}

}
