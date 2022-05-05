package com.javanix.bot.jenkinsBot.database.healthcheck;

import com.javanix.bot.jenkinsBot.core.model.HealthCheckInfoDto;
import com.javanix.bot.jenkinsBot.core.service.HealthCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class HealthCheckServiceImpl implements HealthCheckService {

	private final HealthCheckRepository repository;

	@Override
	public Collection<HealthCheckInfoDto> getAvailableEndpoints(Long ownerId) {
		return repository.getByCreatorIdOrIsPublicTrue(ownerId).stream()
				.map(this::convertEntityToDto)
				.collect(Collectors.toList());
	}

	@Override
	public void save(HealthCheckInfoDto endpoint) {
		Optional<HealthCheckEntity> endpointInDb = repository.getByEndpointNameIgnoreCase(endpoint.getEndpointName());
		HealthCheckEntity endpointEntity = convertDtoToEntity(endpoint);
		endpointInDb.ifPresent(endpointInfoEntity -> endpointEntity.setId(endpointInfoEntity.getId()));
		repository.save(endpointEntity);
	}

	@Override
	public List<HealthCheckInfoDto> allEndpoints() {
		return repository.findAll()
				.stream()
				.map(this::convertEntityToDto)
				.collect(Collectors.toList());
	}

	@Override
	public Optional<HealthCheckInfoDto> getOwnedEntityByName(String name, Long ownerId) {
		return repository.getByEndpointNameIgnoreCaseAndCreatorId(name, ownerId)
				.map(this::convertEntityToDto);
	}

	@Override
	public boolean isDatabaseEmpty() {
		return repository.count() == 0;
	}

	private HealthCheckInfoDto convertEntityToDto(HealthCheckEntity healthCheckEntity) {
		return HealthCheckInfoDto.builder()
				.endpointName(healthCheckEntity.getEndpointName())
				.endpointUrl(healthCheckEntity.getEndpointUrl())
				.creatorFullName(healthCheckEntity.getCreatorFullName())
				.creatorId(healthCheckEntity.getCreatorId())
				.isPublic(healthCheckEntity.getIsPublic())
				.build();
	}

	private HealthCheckEntity convertDtoToEntity(HealthCheckInfoDto healthCheckInfoDto) {
		return HealthCheckEntity.builder()
				.endpointName(healthCheckInfoDto.getEndpointName())
				.endpointUrl(healthCheckInfoDto.getEndpointUrl())
				.creatorFullName(healthCheckInfoDto.getCreatorFullName())
				.creatorId(healthCheckInfoDto.getCreatorId())
				.isPublic(healthCheckInfoDto.isPublic())
				.build();
	}

}
