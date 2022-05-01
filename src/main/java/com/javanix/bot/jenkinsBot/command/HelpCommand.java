package com.javanix.bot.jenkinsBot.command;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;

@Component
class HelpCommand implements TelegramCommand {

	@Override
	public void process(TelegramBot bot, Chat chat, User from, String message) {

		bot.execute(new SendMessage(chat.id(),
				"JenkinsBot. Основная задача - получение статуса билдов от Дженкиса (состояние билда и кол-во упавших тестов). \n" +
						"Текущая версия заточена на работу с Java/jUnit проектами. \n\n" +
						"Смежные команды:\n" +
						"* /history - Показ содержимого файла с изменениями\n\n" +
						"Остальные команды доступны в меню ;)\n\n" +
						"Авторы:\n" +
						"* Viacheslav Davydov <davs@javanix.com>\n\n" +
						"Со-авторы:\n" +
						"* N/A\n\n"
				));
	}

	@Override
	public String getCommandName() {
		return "/help";
	}
}
