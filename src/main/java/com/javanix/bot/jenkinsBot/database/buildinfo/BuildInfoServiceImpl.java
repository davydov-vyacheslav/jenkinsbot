package com.javanix.bot.jenkinsBot.database.buildinfo;

import com.javanix.bot.jenkinsBot.core.model.BuildInfoDto;
import com.javanix.bot.jenkinsBot.core.service.BuildInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class BuildInfoServiceImpl implements BuildInfoService {

	private final BuildInfoRepository repository;

	@Override
	public List<BuildInfoDto> getAvailableRepositories(Long ownerId) {
		return repository.getByCreatorIdOrIsPublicTrue(ownerId).stream()
				.map(this::convertEntityToDto)
				.collect(Collectors.toList());
	}

	@Override
	public void removeRepo(String repoName) {
		repository.getByRepoNameIgnoreCase(repoName).ifPresent(repository::delete);
	}

	@Override
	public List<BuildInfoDto> getOwnedRepositories(Long ownerId) {
		return repository.getByCreatorId(ownerId).stream()
				.map(this::convertEntityToDto)
				.collect(Collectors.toList());
	}

	@Override
	public void save(BuildInfoDto repo) {
		Optional<BuildInfoEntity> repoInDb = repository.getByRepoNameIgnoreCase(repo.getRepoName());
		BuildInfoEntity repoEntity = convertDtoToEntity(repo);
		repoInDb.ifPresent(buildInfoEntity -> repoEntity.setId(buildInfoEntity.getId()));
		repository.save(repoEntity);
	}

	@Override
	public boolean isDatabaseEmpty() {
		return repository.count() == 0;
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
	public boolean hasRepository(String name) {
		return repository.getByRepoNameIgnoreCase(name).isPresent();
	}

	@Override
	public List<BuildInfoDto> allRepositories() {
		return repository.findAll()
				.stream()
				.map(this::convertEntityToDto)
				.collect(Collectors.toList());
	}

	private BuildInfoDto convertEntityToDto(BuildInfoEntity buildInfoEntity) {
		return BuildInfoDto.builder()
				.repoName(buildInfoEntity.getRepoName())
				.creatorFullName(buildInfoEntity.getCreatorFullName())
				.jenkinsInfo(buildInfoEntity.getJenkinsInfo())
				.creatorId(buildInfoEntity.getCreatorId())
				.isPublic(buildInfoEntity.getIsPublic())
				.build();
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
