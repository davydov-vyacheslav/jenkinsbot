package com.javanix.bot.jenkinsBot.database;

import java.util.List;

// TODO: make real database
public interface DatabaseSource {
    BuildRepository getRepositoryByNameIgnoreCase(String repoName);

    List<BuildRepository> getAvailableRepositories(Long ownerId);

    void removeRepo(BuildRepository repo);

    List<BuildRepository> getOwnedRepositories(Long ownerId);

    void addRepository(BuildRepository repo);
}
