package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface Processable {
	void process(Chat chat, User from, String message);

	default <T> Stream<List<T>> splitListByNElements(int pageSize, List<T> fields) {
		return IntStream.range(0, (fields.size() + pageSize - 1) / pageSize)
				.mapToObj(i -> fields.subList(i * pageSize, Math.min(pageSize * (i + 1), fields.size())));
	}

}
