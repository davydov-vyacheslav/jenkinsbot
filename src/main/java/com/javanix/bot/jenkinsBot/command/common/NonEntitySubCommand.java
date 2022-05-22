package com.javanix.bot.jenkinsBot.command.common;

import com.javanix.bot.jenkinsBot.command.Processable;

public interface NonEntitySubCommand extends Processable {
	String getSubCommandName();
}
