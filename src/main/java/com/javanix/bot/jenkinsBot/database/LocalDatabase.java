package com.javanix.bot.jenkinsBot.database;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class LocalDatabase implements DatabaseSource {

    public static final long CREATOR_ID = -1L;
    private final List<BuildRepository> repos = new ArrayList<>();

    @PostConstruct
    public void setup() {
        repos.add(BuildRepository.builder()
                .creatorFullName("auto")
                .creatorId(CREATOR_ID)
                .isPublic(true)
                .repoName("front_runners")
                .jenkinsDomain("dev-rim-fr01")
                .jenkinsPassword("#FrTeam@JenkinsAdmin22!")
                .jenkinsUser("admin")
                .jobName("Insight")
                .build());

        repos.add(BuildRepository.builder()
                .creatorFullName("auto")
                .creatorId(CREATOR_ID)
                .isPublic(true)
                .repoName("finely_blended")
                .jenkinsDomain("dev-rim-fb01")
                .jenkinsPassword("#FbTeam@JenkinsAdmin22!")
                .jenkinsUser("admin")
                .jobName("Insight")
                .build());

        repos.add(BuildRepository.builder()
                .creatorFullName("auto")
                .creatorId(CREATOR_ID)
                .isPublic(true)
                .repoName("xmen")
                .jenkinsDomain("dev-rim-xm01")
                .jenkinsPassword("#XmTeam@JenkinsAdmin22!")
                .jenkinsUser("admin")
                .jobName("Insight")
                .build());

        repos.add(BuildRepository.builder()
                .creatorFullName("auto")
                .creatorId(CREATOR_ID)
                .isPublic(true)
                .repoName("wizards")
                .jenkinsDomain("dev-rim-wiz01")
                .jenkinsPassword("#WizTeam@JenkinsAdmin22!")
                .jenkinsUser("admin")
                .jobName("Insight")
                .build());

        repos.add(BuildRepository.builder()
                .creatorFullName("auto")
                .creatorId(CREATOR_ID)
                .isPublic(true)
                .repoName("gunbros")
                .jenkinsDomain("dev-rim-gb01")
                .jenkinsPassword("#GbTeam@JenkinsAdmin22!")
                .jenkinsUser("admin")
                .jobName("Insight")
                .build());

        repos.add(BuildRepository.builder()
                .creatorFullName("auto")
                .creatorId(CREATOR_ID)
                .isPublic(true)
                .repoName("chefs")
                .jenkinsDomain("dev-rim-chf01")
                .jenkinsPassword("#ChfTeam@JenkinsAdmin22!")
                .jenkinsUser("admin")
                .jobName("Insight")
                .build());
    }

    @Override
    public BuildRepository getRepositoryByNameIgnoreCase(String repoName) {
        return repos.stream()
                .filter(buildRepository -> buildRepository.getRepoName().equalsIgnoreCase(repoName))
                .findAny().orElse(null);
    }

    @Override
    public List<BuildRepository> getAvailableRepositories(Long ownerId) {
        return repos.stream()
                .filter(buildRepository -> buildRepository.getIsPublic() || buildRepository.getCreatorId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public void removeRepo(BuildRepository repo) {
        repos.remove(repo);
    }

    @Override
    public List<BuildRepository> getOwnedRepositories(Long ownerId) {
        return repos.stream()
                .filter(buildRepository -> buildRepository.getCreatorId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public void addRepository(BuildRepository repo) {
        repos.add(repo);
    }
}
