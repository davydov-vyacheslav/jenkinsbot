package com.javanix.bot.jenkinsBot.database.user;

import com.javanix.bot.jenkinsBot.CacheService;
import com.javanix.bot.jenkinsBot.core.model.LocaleType;
import com.javanix.bot.jenkinsBot.core.model.UserInfoDto;
import com.javanix.bot.jenkinsBot.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

	private final UserRepository repository;
	private final CacheService cacheService;

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

	@Override
	public Locale getUserLocale(long telegramId) {
		Locale result = cacheService.getUserLocale(telegramId);
		if (result == null) {
			result = getUser(telegramId).getLocale().getLocale();
			cacheService.updateUserLocale(telegramId, result);
		}
		return result;
	}

	@Override
	public void updateUserLocale(long telegramId, LocaleType locale) {
		Optional<UserEntity> foundUser = repository.findByUserId(telegramId);
		foundUser.ifPresent(userEntity -> {
			foundUser.get().setLocale(locale);
			repository.save(foundUser.get());
			cacheService.updateUserLocale(telegramId, locale.getLocale());
		});
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
