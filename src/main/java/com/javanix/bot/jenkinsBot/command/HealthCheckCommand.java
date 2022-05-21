package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.command.common.EntityCommandFactory;
import com.javanix.bot.jenkinsBot.command.healthcheck.HealthCheckEntityCommandFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component
@RequiredArgsConstructor
class HealthCheckCommand extends AbstractEntityCommand {

	private final HealthCheckEntityCommandFactory healthCheckCommandFactory;
	private static final Pattern HEALTH_CHECK_COMMAND_PATTERN = Pattern.compile(".*/healthcheck.?(add_reference|add|edit|status|delete|my_list).?(.*)", CASE_INSENSITIVE);

	@Override
	public String getCommandName() {
		return "/healthcheck";
	}

	@Override
	protected Pattern getCommandPattern() {
		return HEALTH_CHECK_COMMAND_PATTERN;
	}

	@Override
	protected EntityCommandFactory getCommandFactory() {
		return healthCheckCommandFactory;
	}

}
