package com.javanix.bot.jenkinsBot.command.healthcheck;

import com.javanix.bot.jenkinsBot.command.Processable;
import com.javanix.bot.jenkinsBot.command.common.CommonEntityActionType;

public interface HealthCheckSubCommand extends Processable {

	CommonEntityActionType getBuildType();

}
