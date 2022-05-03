package com.javanix.bot.jenkinsBot.database.healthcheck;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
interface HealthCheckRepository extends MongoRepository<HealthCheckEntity, String> {

	Collection<HealthCheckEntity> getByCreatorIdOrIsPublicTrue(Long ownerId);
}
