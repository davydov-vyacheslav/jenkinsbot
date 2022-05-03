package com.javanix.bot.jenkinsBot.command.healthcheck.model;

import com.javanix.bot.jenkinsBot.cli.HealthStatus;
import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusCheckForStatusDto {
	HealthCheckInfoDto healthCheckInfoDto;
	HealthStatus healthStatus;
}
