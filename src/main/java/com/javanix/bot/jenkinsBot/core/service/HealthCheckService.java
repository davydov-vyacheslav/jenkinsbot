package com.javanix.bot.jenkinsBot.core.service;

import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;

import java.util.Collection;

public interface HealthCheckService extends EntityService<HealthCheckInfoDto> {
	Collection<HealthCheckInfoDto> getAvailableEndpoints(Long ownerId);

}
