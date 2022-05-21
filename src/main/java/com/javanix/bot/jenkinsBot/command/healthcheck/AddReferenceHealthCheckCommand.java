package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.command.common.AbstractAddReferenceCommand;
import com.javanix.bot.jenkinsBot.command.common.UserEntityContext;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddReferenceHealthCheckCommand extends AbstractAddReferenceCommand<HealthCheckInfoDto> implements HealthCheckSubCommand {

	public AddReferenceHealthCheckCommand(HealthCheckService database, UserEntityContext userContext, DefaultHealthCheckCommand defaultCommand, TelegramBotWrapper bot) {
		super(database, userContext, defaultCommand, bot);
	}

	@Override
	protected InlineKeyboardMarkup buildRepoListMarkup(User from, List<HealthCheckInfoDto> availableEndpoints) {

		InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
		groupEntitiesBy(availableEndpoints, from.id(), 2, inlineKeyboardMarkup, "/healthcheck add_reference ");
		inlineKeyboardMarkup.addRow(
				new InlineKeyboardButton(bot.getI18nMessage(from, "button.common.backToActionList")).callbackData("/healthcheck my_list")
		);

		return inlineKeyboardMarkup;
	}

}
