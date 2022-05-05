package com.javanix.bot.jenkinsBot.database.healthcheck;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
interface HealthCheckRepository extends MongoRepository<HealthCheckEntity, String> {

	Collection<HealthCheckEntity> getByCreatorIdOrIsPublicTrue(Long ownerId);

	Optional<HealthCheckEntity> getByEndpointNameIgnoreCase(String endpointName);

	Optional<HealthCheckEntity> getByEndpointNameIgnoreCaseAndCreatorId(String name, Long ownerId);

	Collection<HealthCheckEntity> getByCreatorId(Long ownerId);
}
