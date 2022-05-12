package com.javanix.bot.jenkinsBot.database.buildinfo;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.model.ConsoleOutputInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import com.javanix.bot.jenkinsBot.core.service.ConsoleOutputConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class BuildInfoServiceImpl implements BuildInfoService {

	private final BuildInfoRepository repository;
	private final ConsoleOutputConfigService consoleOutputConfigService;

	@Override
	public List<BuildInfoDto> getAvailableRepositories(Long ownerId) {
		return repository.getByCreatorIdOrIsPublicTrue(ownerId).stream()
				.map(this::convertEntityToDto)
				.collect(Collectors.toList());
	}

	@Override
	public void removeEntity(String repoName) {
		repository.getByRepoNameIgnoreCase(repoName).ifPresent(repository::delete);
	}

	@Override
	public List<BuildInfoDto> getOwnedEntities(Long ownerId) {
		return repository.getByCreatorId(ownerId).stream()
				.map(this::convertEntityToDto)
				.collect(Collectors.toList());
	}

	@Override
	public void save(BuildInfoDto repo) {
		Optional<BuildInfoEntity> repoInDb = repository.getByRepoNameIgnoreCase(repo.getRepoName());
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
	public Optional<BuildInfoDto> getOwnedEntityByName(String name, Long ownerId) {
		return repository.getByRepoNameIgnoreCaseAndCreatorId(name, ownerId)
				.map(this::convertEntityToDto);
	}

	@Override
	public BuildInfoDto getAvailableRepository(String name, Long ownerId) {
		if (name.isEmpty()) {
			return null;
		}
		return repository.getByRepoNameIgnoreCaseAndIsPublicTrueOrCreatorId(name, ownerId)
				.map(this::convertEntityToDto).orElse(null);
	}

	@Override
	public boolean hasEntity(String name) {
		return repository.getByRepoNameIgnoreCase(name).isPresent();
	}

	private BuildInfoDto convertEntityToDto(BuildInfoEntity buildInfoEntity) {
		BuildInfoDto buildInfo = BuildInfoDto.builder()
				.repoName(buildInfoEntity.getRepoName())
				.creatorFullName(buildInfoEntity.getCreatorFullName())
				.jenkinsInfo(buildInfoEntity.getJenkinsInfo())
				.creatorId(buildInfoEntity.getCreatorId())
				.isPublic(buildInfoEntity.getIsPublic())
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
				.build();
	}

}
