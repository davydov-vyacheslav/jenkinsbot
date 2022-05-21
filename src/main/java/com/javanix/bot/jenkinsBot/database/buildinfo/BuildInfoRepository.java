package com.javanix.bot.jenkinsBot.database.buildinfo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.stream.Stream;

interface BuildInfoRepository extends MongoRepository<BuildInfoEntity, String> {

	Stream<BuildInfoEntity> getByCreatorId(Long ownerId);

	Stream<BuildInfoEntity> getByIsPublicTrueAndCreatorIdNot(Long ownerId);

	Optional<BuildInfoEntity> getByRepoNameIgnoreCaseAndCreatorId(String name, Long ownerId);

	Stream<BuildInfoEntity> getByCreatorIdIsOrReferencedByUsersContains(Long ownerId, Long ownerIdSame);

	boolean existsByRepoNameIgnoreCase(String name);
}
