package com.javanix.bot.jenkinsBot.core.service;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;

import java.util.List;

public interface BuildInfoService extends EntityService<BuildInfoDto> {

	/**
	 * List of repositories user able to see:
	 * - Owned repositories
	 * - Public repositories
	 */
	List<BuildInfoDto> getAvailableRepositories(Long ownerId);

	/**
	 * Get repository by name, case-insensitive, available for specific user for view (e.g. public or owned)
	 */
	BuildInfoDto getAvailableRepository(String name, Long ownerId);

}
