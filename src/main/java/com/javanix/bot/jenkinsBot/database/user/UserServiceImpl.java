package com.javanix.bot.jenkinsBot.database.user;

import com.javanix.bot.jenkinsBot.core.model.UserInfoDto;
import com.javanix.bot.jenkinsBot.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

	private final UserRepository repository;

	@Override
	public UserInfoDto getUser(Long telegramId) {
		return repository.findByUserId(telegramId).map(this::convertEntityToDto).orElse(UserInfoDto
				.emptyEntityBuilder()
						.userId(telegramId)
				.build());
	}

	@Override
	public void saveUser(UserInfoDto user) {
		Optional<UserEntity> foundUser = repository.findByUserId(user.getUserId());
		UserEntity entity = convertDtoToEntity(user);
		foundUser.ifPresent(userEntity -> entity.setId(userEntity.getId()));
		repository.save(entity);
	}

	private UserInfoDto convertEntityToDto(UserEntity userEntity) {
		return UserInfoDto.builder()
				.userId(userEntity.getUserId())
				.userName(userEntity.getUserName())
				.lastMessageIdMap(userEntity.getLastMessageIdMap())
				.subscribedBuildRepositories(userEntity.getSubscribedBuildRepositories())
				.locale(userEntity.getLocale())
				.build();
	}

	private UserEntity convertDtoToEntity(UserInfoDto userInfoDto) {
		return UserEntity.builder()
				.userId(userInfoDto.getUserId())
				.userName(userInfoDto.getUserName())
				.lastMessageIdMap(userInfoDto.getLastMessageIdMap())
				.subscribedBuildRepositories(userInfoDto.getSubscribedBuildRepositories())
				.locale(userInfoDto.getLocale())
				.build();
	}
}
