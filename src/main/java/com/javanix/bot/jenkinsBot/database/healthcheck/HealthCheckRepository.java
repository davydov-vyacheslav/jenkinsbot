package com.javanix.bot.jenkinsBot.database.healthcheck;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.stream.Stream;

interface HealthCheckRepository extends MongoRepository<HealthCheckEntity, String> {

	Optional<HealthCheckEntity> getByEndpointNameIgnoreCaseAndCreatorId(String name, Long ownerId);

	Stream<HealthCheckEntity> getByCreatorId(Long ownerId);

	Stream<HealthCheckEntity> getByCreatorIdIsOrReferencedByUsersContains(Long ownerId, Long ownerIdSame);

	boolean existsByEndpointNameIgnoreCase(String name);

	Stream<HealthCheckEntity> getByIsPublicTrueAndCreatorIdNot(Long ownerId);
}
