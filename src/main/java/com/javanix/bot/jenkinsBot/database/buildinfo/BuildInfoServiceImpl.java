package com.javanix.bot.jenkinsBot.database.buildinfo;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.ConsoleOutputInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.javanix.bot.jenkinsBot.core.service.ConsoleOutputConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
class BuildInfoServiceImpl implements BuildInfoService {

	private final BuildInfoRepository repository;
	private final ConsoleOutputConfigService consoleOutputConfigService;

	@Override
	public void save(BuildInfoDto repo) {
		Optional<BuildInfoEntity> repoInDb = repository.getByRepoNameIgnoreCaseAndCreatorId(repo.getRepoName(), repo.getCreatorId());
		BuildInfoEntity repoEntity = convertDtoToEntity(repo);

		ConsoleOutputInfoDto repoConsoleInfo = repoEntity.getJenkinsInfo().getConsoleOutputInfo();

		if (repoInDb.isPresent()) {
			BuildInfoEntity buildInfoEntity = repoInDb.get();
			repoEntity.setId(buildInfoEntity.getId());
			ConsoleOutputInfoDto consoleOutputInfo = buildInfoEntity.getJenkinsInfo().getConsoleOutputInfo();
			if (consoleOutputInfo != null && !Objects.equals(consoleOutputInfo.getName(),
					repoConsoleInfo.getName())) {
				repoEntity.getJenkinsInfo().setConsoleOutputInfo(
						consoleOutputConfigService.findByName(repoConsoleInfo.getName())
				);
			}
		} else {
			repoEntity.getJenkinsInfo().setConsoleOutputInfo(
					consoleOutputConfigService.findByName(repoConsoleInfo == null ? "" : repoConsoleInfo.getName())
			);
		}

		repository.save(repoEntity);
	}

	@Override
	public void removeEntityInternal(Long ownerId, String name) {
		repository.getByRepoNameIgnoreCaseAndCreatorId(name, ownerId).ifPresent(repository::delete);
	}

	@Override
	public Stream<BuildInfoDto> getOwnedEntities(Long ownerId) {
		return repository.getByCreatorId(ownerId).map(this::convertEntityToDto);
	}

	@Override
	public Stream<BuildInfoDto> getAvailableEntitiesToReference(Long ownerId) {
		return repository.getByIsPublicTrueAndCreatorIdNot(ownerId)
				.filter(buildInfoEntity -> buildInfoEntity.getReferencedByUsers() == null || !buildInfoEntity.getReferencedByUsers().contains(ownerId))
				.map(this::convertEntityToDto);
	}

	@Override
	public Stream<BuildInfoDto> getOwnedOrReferencedEntities(Long ownerId) {
		return repository.getByCreatorIdIsOrReferencedByUsersContains(ownerId, ownerId)
				.map(this::convertEntityToDto);
	}

	@Override
	public boolean hasEntity(String name) {
		return repository.existsByRepoNameIgnoreCase(name);
	}

	private BuildInfoDto convertEntityToDto(BuildInfoEntity buildInfoEntity) {
		BuildInfoDto buildInfo = BuildInfoDto.builder()
				.repoName(buildInfoEntity.getRepoName())
				.creatorFullName(buildInfoEntity.getCreatorFullName())
				.jenkinsInfo(buildInfoEntity.getJenkinsInfo())
				.creatorId(buildInfoEntity.getCreatorId())
				.isPublic(buildInfoEntity.getIsPublic())
				.referencedByUsers(buildInfoEntity.getReferencedByUsers())
				.build();
		if (buildInfo.getJenkinsInfo().getConsoleOutputInfo() == null) {
			buildInfo.getJenkinsInfo().setConsoleOutputInfo(consoleOutputConfigService.findByName(ConsoleOutputInfoDto.DEFAULT_RESOLVER_NAME));
		}
		return buildInfo;
	}

	private BuildInfoEntity convertDtoToEntity(BuildInfoDto buildInfoEntity) {
		return BuildInfoEntity.builder()
				.repoName(buildInfoEntity.getRepoName())
				.creatorFullName(buildInfoEntity.getCreatorFullName())
				.jenkinsInfo(buildInfoEntity.getJenkinsInfo())
				.creatorId(buildInfoEntity.getCreatorId())
				.isPublic(buildInfoEntity.isPublic())
				.referencedByUsers(buildInfoEntity.getReferencedByUsers())
				.build();
	}

}
