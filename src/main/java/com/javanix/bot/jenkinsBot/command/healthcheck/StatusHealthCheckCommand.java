package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.TelegramBotWrapper;
import com.javanix.bot.jenkinsBot.cli.CliProcessor;
import com.javanix.bot.jenkinsBot.cli.HealthStatus;
import com.javanix.bot.jenkinsBot.command.common.EntityActionType;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class StatusHealthCheckCommand implements HealthCheckSubCommand {

	private final CliProcessor cliProcessor;
	private final HealthCheckService database;
	private final TelegramBotWrapper bot;

	@Override
	public void process(Chat chat, User from, String buildCommandArguments) {

		List<StatusCheckDto> endpoints = database.getAvailableEndpoints(from.id())
				.stream()
				.map(status -> new StatusCheckDto(status, HealthStatus.NA))
				.collect(Collectors.toList());

		SendResponse execute = bot.execute(new SendMessage(chat.id(), buildMessage(from, endpoints)));

		ExecutorService threadPool = Executors.newFixedThreadPool(4);
		for (StatusCheckDto endpoint: endpoints) {
			CompletableFuture.supplyAsync(() -> {
				endpoint.setHealthStatus(cliProcessor.getHealthStatusForUrl(endpoint.getHealthCheckInfoDto().getEndpointUrl()));
				return bot.execute(new EditMessageText(chat.id(), execute.message().messageId(), buildMessage(from, endpoints)));
			}, threadPool);
		}
	}

	private String buildMessage(User from, List<StatusCheckDto> endpoints) {
		StringBuilder message = new StringBuilder();
		message.append(bot.getI18nMessage(from, "message.command.endpoint.common.status.prefix")).append("\n");
		for (StatusCheckDto endpoint: endpoints) {
			message.append(bot.getI18nMessage(from, "message.command.endpoint.common.status.info",
					new Object[] { endpoint.getHealthCheckInfoDto().getEndpointName(),
							bot.getI18nMessage(from, endpoint.getHealthStatus().getMessageKey())}))
					.append("\n");
		}
		return message.toString();
	}

	public EntityActionType getCommandType() {
		return EntityActionType.STATUS;
	}


	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class StatusCheckDto {
		HealthCheckInfoDto healthCheckInfoDto;
		HealthStatus healthStatus;
	}
}
