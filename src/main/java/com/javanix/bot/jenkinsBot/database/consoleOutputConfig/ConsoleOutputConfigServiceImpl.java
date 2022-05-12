package com.javanix.bot.jenkinsBot.database.consoleOutputConfig;

import com.javanix.bot.jenkinsBot.core.model.ConsoleOutputInfoDto;
import com.javanix.bot.jenkinsBot.core.service.ConsoleOutputConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class ConsoleOutputConfigServiceImpl implements ConsoleOutputConfigService {

	private final ConsoleOutputConfigRepository repository;

	@Override
	public ConsoleOutputInfoDto findByName(String name) {
		return convertEntityToDto(Objects.requireNonNull(repository.findByNameIgnoreCase(name)
				.orElse(repository.findByNameIgnoreCase(ConsoleOutputInfoDto.DEFAULT_RESOLVER_NAME).orElse(null))));
	}

	@Override
	public void save(ConsoleOutputInfoDto consoleOutputInfoDto) {
		Optional<ConsoleOutputConfigEntity> dbEntity = repository.findByNameIgnoreCase(consoleOutputInfoDto.getName());
		ConsoleOutputConfigEntity prePersistEntity = convertDtoToEntity(consoleOutputInfoDto);
		dbEntity.ifPresent(endpointInfoEntity -> prePersistEntity.setId(endpointInfoEntity.getId()));
		repository.save(prePersistEntity);
	}

	private ConsoleOutputConfigEntity convertDtoToEntity(ConsoleOutputInfoDto consoleOutputInfoDto) {
		return ConsoleOutputConfigEntity.builder()
				.executedTestPattern(consoleOutputInfoDto.getExecutedTestPattern())
				.failedTestPattern(consoleOutputInfoDto.getFailedTestPattern())
				.fileEncoding(consoleOutputInfoDto.getFileEncoding())
				.name(consoleOutputInfoDto.getName())
				.unitTestsResultFilepathPrefix(consoleOutputInfoDto.getUnitTestsResultFilepathPrefix())
				.build();
	}

	private ConsoleOutputInfoDto convertEntityToDto(ConsoleOutputConfigEntity entity) {
		return ConsoleOutputInfoDto.builder()
				.fileEncoding(entity.getFileEncoding())
				.executedTestPattern(entity.getExecutedTestPattern())
				.failedTestPattern(entity.getFailedTestPattern())
				.unitTestsResultFilepathPrefix(entity.getUnitTestsResultFilepathPrefix())
				.name(entity.getName())
				.build();
	}

}
