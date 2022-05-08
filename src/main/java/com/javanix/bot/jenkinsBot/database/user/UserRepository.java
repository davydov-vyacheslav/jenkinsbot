package com.javanix.bot.jenkinsBot.database.user;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

interface UserRepository extends MongoRepository<UserEntity, String> {

	Optional<UserEntity> findByUserId(Long userId);
}
