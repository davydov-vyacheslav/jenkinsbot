package com.javanix.bot.jenkinsBot.core.service;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;

import java.util.List;

public interface BuildInfoService extends EntityService<BuildInfoDto> {
	long DEFAULT_CREATOR_ID = -1L;

	/**
	 * List of repositories user able to see:
	 * - Owned repositories
	 * - Public repositories
	 */
	List<BuildInfoDto> getAvailableRepositories(Long ownerId);

	void removeRepo(String repoName);

	List<BuildInfoDto> getOwnedRepositories(Long ownerId);

	boolean isDatabaseEmpty();

	/**
	 * Get repository by name, case-insensitive, available for specific user for view (e.g. public or owned)
	 */
	BuildInfoDto getAvailableRepository(String name, Long ownerId);

	boolean hasRepository(String name);

	List<BuildInfoDto> allRepositories();
}
