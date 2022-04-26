package com.javanix.bot.jenkinsBot.database.buildinfo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
interface BuildInfoRepository extends MongoRepository<BuildInfoEntity, String> {

	List<BuildInfoEntity> getByCreatorIdOrIsPublicTrue(@Param("ownerId") Long ownerId);

	List<BuildInfoEntity> getByCreatorId(Long ownerId);

	Optional<BuildInfoEntity> getByRepoNameIgnoreCaseAndCreatorId(String name, Long ownerId);

	@Query("{'repoName': {'$regex' : :#{#repoName}, '$options' : 'i'}, '$or':[ {'isPublic' : true}, {'creatorId' : :#{#ownerId}} ] }")
	Optional<BuildInfoEntity> getByRepoNameIgnoreCaseAndIsPublicTrueOrCreatorId(@Param("repoName") String repoName, @Param("ownerId") Long ownerId);

	Optional<BuildInfoEntity> getByRepoNameIgnoreCase(String repoName);
}
