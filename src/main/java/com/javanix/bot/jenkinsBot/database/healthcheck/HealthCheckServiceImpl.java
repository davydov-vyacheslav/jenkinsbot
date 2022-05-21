package com.javanix.bot.jenkinsBot.database.healthcheck;

import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
class HealthCheckServiceImpl implements HealthCheckService {

	private final HealthCheckRepository repository;

	@Override
	public void save(HealthCheckInfoDto endpoint) {
		Optional<HealthCheckEntity> endpointInDb = repository.getByEndpointNameIgnoreCaseAndCreatorId(endpoint.getEndpointName(), endpoint.getCreatorId());
		HealthCheckEntity endpointEntity = convertDtoToEntity(endpoint);
		endpointInDb.ifPresent(endpointInfoEntity -> endpointEntity.setId(endpointInfoEntity.getId()));
		repository.save(endpointEntity);
	}

	@Override
	public void removeEntityInternal(Long ownerId, String name) {
		repository.getByEndpointNameIgnoreCaseAndCreatorId(name, ownerId).ifPresent(repository::delete);
	}

	@Override
	public Stream<HealthCheckInfoDto> getOwnedEntities(Long ownerId) {
		return repository.getByCreatorId(ownerId).map(this::convertEntityToDto);
	}

	@Override
	public Stream<HealthCheckInfoDto> getAvailableEntitiesToReference(Long ownerId) {
		return repository.getByIsPublicTrueAndCreatorIdNot(ownerId)
				.filter(entity -> entity.getReferencedByUsers() == null || !entity.getReferencedByUsers().contains(ownerId))
				.map(this::convertEntityToDto);
	}

	@Override
	public Stream<HealthCheckInfoDto> getOwnedOrReferencedEntities(Long ownerId) {
		return repository.getByCreatorIdIsOrReferencedByUsersContains(ownerId, ownerId)
				.map(this::convertEntityToDto);
	}

	@Override
	public boolean hasEntity(String name) {
		return repository.existsByEndpointNameIgnoreCase(name);
	}

	private HealthCheckInfoDto convertEntityToDto(HealthCheckEntity healthCheckEntity) {
		return HealthCheckInfoDto.builder()
				.endpointName(healthCheckEntity.getEndpointName())
				.endpointUrl(healthCheckEntity.getEndpointUrl())
				.creatorFullName(healthCheckEntity.getCreatorFullName())
				.creatorId(healthCheckEntity.getCreatorId())
				.isPublic(healthCheckEntity.getIsPublic())
				.referencedByUsers(healthCheckEntity.getReferencedByUsers())
				.build();
	}

	private HealthCheckEntity convertDtoToEntity(HealthCheckInfoDto healthCheckInfoDto) {
		return HealthCheckEntity.builder()
				.endpointName(healthCheckInfoDto.getEndpointName())
				.endpointUrl(healthCheckInfoDto.getEndpointUrl())
				.creatorFullName(healthCheckInfoDto.getCreatorFullName())
				.creatorId(healthCheckInfoDto.getCreatorId())
				.isPublic(healthCheckInfoDto.isPublic())
				.referencedByUsers(healthCheckInfoDto.getReferencedByUsers())
				.build();
	}

}
