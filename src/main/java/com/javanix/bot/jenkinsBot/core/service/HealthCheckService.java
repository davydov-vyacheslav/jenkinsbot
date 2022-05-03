package com.javanix.bot.jenkinsBot.core.service;

import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;

import java.util.Collection;
import java.util.List;

public interface HealthCheckService {
	Collection<HealthCheckInfoDto> getAvailableEndpoints(Long ownerId);

	void addEndpoint(HealthCheckInfoDto endpoint);

	List<HealthCheckInfoDto> allEndpoints();

	boolean isDatabaseEmpty();
}
