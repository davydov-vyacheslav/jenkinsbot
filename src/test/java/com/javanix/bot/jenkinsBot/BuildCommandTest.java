package com.javanix.bot.jenkinsBot;

import com.javanix.bot.jenkinsBot.command.BuildCommand;
import com.javanix.bot.jenkinsBot.command.CommandFactory;
import com.javanix.bot.jenkinsBot.command.TelegramCommand;
import com.javanix.bot.jenkinsBot.command.build.BuildType;
import com.javanix.bot.jenkinsBot.database.LocalDatabase;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class BuildCommandTest {

    @Autowired
    private CommandFactory factory;

    @MockBean
    private TelegramBot bot;

    @MockBean
    private Message message;

    @Test
    public void buildCommandTest_noParams() {

        Mockito.when(message.text()).thenReturn("/build");
        Mockito.when(message.chat()).thenReturn(new Chat());
        Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
            assertEquals("Wrong operation. Choose one from list", getText(invocation));

            List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
                    new InlineKeyboardButton(BuildType.ADD.toString()).switchInlineQueryCurrentChat("/build ADD"),
                    new InlineKeyboardButton(BuildType.DELETE.toString()).switchInlineQueryCurrentChat("/build DELETE"),
                    new InlineKeyboardButton(BuildType.STATUS.toString()).switchInlineQueryCurrentChat("/build STATUS")
            );
            List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
            assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

            return null;
        });

        TelegramCommand command = factory.getCommand(message.text());
        assertTrue(command instanceof BuildCommand);
        command.process(bot, message);
        Mockito.verify(bot).execute(any(SendMessage.class));
    }

    @Test
    public void buildCommandTest_wrongParams() {

        Mockito.when(message.text()).thenReturn("/build xxx");
        Mockito.when(message.chat()).thenReturn(new Chat());
        Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
            assertEquals("Wrong operation. Choose one from list", getText(invocation));

            List<InlineKeyboardButton> expectedInlineButtons = Arrays.asList(
                    new InlineKeyboardButton(BuildType.ADD.toString()).switchInlineQueryCurrentChat("/build ADD"),
                    new InlineKeyboardButton(BuildType.DELETE.toString()).switchInlineQueryCurrentChat("/build DELETE"),
                    new InlineKeyboardButton(BuildType.STATUS.toString()).switchInlineQueryCurrentChat("/build STATUS")
            );
            List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
            assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

            return null;
        });

        TelegramCommand command = factory.getCommand(message.text());
        assertTrue(command instanceof BuildCommand);
        command.process(bot, message);
        Mockito.verify(bot).execute(any(SendMessage.class));
    }


    @Test
    public void buildCommandTest_delete_noParams() {

        Mockito.when(message.text()).thenReturn("/build delete");
        Mockito.when(message.chat()).thenReturn(new Chat());
        Mockito.when(message.from()).thenReturn(new User(123L));
        Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
            assertEquals("Wrong repo. You can delete only owned repository.", getText(invocation));

            List<InlineKeyboardButton> expectedInlineButtons = Collections.emptyList();
            List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
            assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

            return null;
        });

        TelegramCommand command = factory.getCommand(message.text());
        assertTrue(command instanceof BuildCommand);
        command.process(bot, message);
        Mockito.verify(bot).execute(any(SendMessage.class));
    }

    @Test
    public void buildCommandTest_delete_wrongParams() {
// TODO: add owned repos
        // TODO: mock database
        Mockito.when(message.text()).thenReturn("/build delete xmen");
        Mockito.when(message.chat()).thenReturn(new Chat());
        Mockito.when(message.from()).thenReturn(new User(123L));
        Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
            assertEquals("Wrong repo. You can delete only owned repository.", getText(invocation));

            List<InlineKeyboardButton> expectedInlineButtons = Collections.emptyList();
            List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
            assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

            return null;
        });

        TelegramCommand command = factory.getCommand(message.text());
        assertTrue(command instanceof BuildCommand);
        command.process(bot, message);
        Mockito.verify(bot).execute(any(SendMessage.class));
    }

    @Test
    public void buildCommandTest_delete_okParams() {
// TODO: mock database
        Mockito.when(message.text()).thenReturn("/build delete xmen");
        Mockito.when(message.chat()).thenReturn(new Chat());
        Mockito.when(message.from()).thenReturn(new User(LocalDatabase.CREATOR_ID));
        Mockito.when(bot.execute(any(SendMessage.class))).then(invocation -> {
            assertEquals("Repository xmen has been removed", getText(invocation));

            List<InlineKeyboardButton> expectedInlineButtons = Collections.emptyList();
            List<InlineKeyboardButton> actualInlineButtons = getInlineKeyboardButtons(invocation);
            assertThat(expectedInlineButtons).containsExactlyInAnyOrderElementsOf(actualInlineButtons);

            return null;
        });

        TelegramCommand command = factory.getCommand(message.text());
        assertTrue(command instanceof BuildCommand);
        command.process(bot, message);
        Mockito.verify(bot).execute(any(SendMessage.class));
    }

    // buildCommandTest_status_noParam
    // buildCommandTest_status_wrongParam
    // buildCommandTest_status_okParam..
    //  TODO + other private repo
    // TODO: convertFailedTestsOutputToLinks test



    private Object getText(InvocationOnMock invocation) {
        return ((SendMessage) invocation.getArgument(0)).getParameters().get("text");
    }

    private List<InlineKeyboardButton> getInlineKeyboardButtons(InvocationOnMock invocation) {
        SendMessage message = invocation.getArgument(0);
        InlineKeyboardMarkup reply_markup = (InlineKeyboardMarkup) message.getParameters().get("reply_markup");
        InlineKeyboardButton[][] buttons = reply_markup == null ? new InlineKeyboardButton[0][0] : reply_markup.inlineKeyboard();
        return Arrays.stream(buttons).flatMap(Arrays::stream).collect(Collectors.toList());
    }
}
