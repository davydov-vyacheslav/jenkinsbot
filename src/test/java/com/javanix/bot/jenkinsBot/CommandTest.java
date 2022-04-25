package com.javanix.bot.jenkinsBot;

import com.javanix.bot.jenkinsBot.command.CommandFactory;
import com.javanix.bot.jenkinsBot.command.HelpCommand;
import com.javanix.bot.jenkinsBot.command.TelegramCommand;
import com.javanix.bot.jenkinsBot.command.UnknownCommand;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class CommandTest {

    @Autowired
    private CommandFactory factory;

    @MockBean
    private TelegramBot bot;

    @MockBean
    private Message message;

    @Test
    public void helpCommandTest() {

        Mockito.when(message.text()).thenReturn("/help");
        Mockito.when(message.chat()).thenReturn(new Chat());
        Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
            assertEquals("List of commands: \n" +
                    "* /help - This help message\n" +
                    "* /build <type={add,delete,status}> - build management and getting actual info", getText(invocation));
            return null;
        });

        TelegramCommand command = factory.getCommand(message.text());
        assertTrue(command instanceof HelpCommand);
        command.process(bot, message);
        Mockito.verify(bot).execute(any(SendMessage.class));
    }

    @Test
    public void fooCommandTest() {

        Mockito.when(message.text()).thenReturn("/blablabla asd asd");
        Mockito.when(message.chat()).thenReturn(new Chat());
        Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
            assertEquals("Unknown command. Press /help to see list of all commands", getText(invocation));
            return null;
        });

        TelegramCommand command = factory.getCommand(message.text());
        assertTrue(command instanceof UnknownCommand);
        command.process(bot, message);
        Mockito.verify(bot).execute(any(SendMessage.class));
    }

    private Object getText(InvocationOnMock invocation) {
        return ((SendMessage) invocation.getArgument(0)).getParameters().get("text");
    }
}
