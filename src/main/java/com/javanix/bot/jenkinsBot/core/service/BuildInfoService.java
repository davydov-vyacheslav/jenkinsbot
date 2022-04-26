package com.javanix.bot.jenkinsBot.core.service;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;

import java.util.List;

public interface BuildInfoService {
	long DEFAULT_CREATOR_ID = -1L;

	/**
	 * List of repositories user able to see:
	 * - Owned repositories
	 * - Public repositories
	 */
	List<BuildInfoDto> getAvailableRepositories(Long ownerId);

	void removeRepo(String repoName);

	List<BuildInfoDto> getOwnedRepositories(Long ownerId);

	void addRepository(BuildInfoDto repo);

	void updateRepository(BuildInfoDto repo);

	boolean isDatabaseEmpty();

	BuildInfoDto getOwnedRepository(String name, Long ownerId);

	/**
	 * Get repository by name, case-insensitive, available for specific user for view (e.g. public or owned)
	 */
	BuildInfoDto getAvailableRepository(String name, Long ownerId);

	boolean hasRepository(String name);
}
