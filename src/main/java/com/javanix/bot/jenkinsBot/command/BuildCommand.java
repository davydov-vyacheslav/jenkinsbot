package com.javanix.bot.jenkinsBot.command;

import com.javanix.bot.jenkinsBot.command.build.BuildEntityCommandFactory;
import com.javanix.bot.jenkinsBot.command.common.EntityCommandFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

import static java.util.regex.Pattern.CASE_INSENSITIVE;

@Component
@RequiredArgsConstructor
class BuildCommand extends AbstractEntityCommand {

	private final BuildEntityCommandFactory buildCommandFactory;

	private static final Pattern BUILD_COMMAND_PATTERN = Pattern.compile(".*/build.?(add|edit|status|delete|my_list).?(.*)", CASE_INSENSITIVE);

	@Override
	public String getCommandName() {
		return "/build";
	}

	@Override
	protected Pattern getCommandPattern() {
		return BUILD_COMMAND_PATTERN;
	}

	@Override
	protected EntityCommandFactory getCommandFactory() {
		return buildCommandFactory;
	}
}
