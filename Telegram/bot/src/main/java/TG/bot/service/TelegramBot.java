package TG.bot.service;

import TG.bot.config.botConfig;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Component

public class TelegramBot extends TelegramLongPollingBot {
    private static final String HELP_TEXT = EmojiParser.parseToUnicode("Напиши любой запрос, и получи моментальный ответ \n" +
            " с поисковой информацией  :star_struck:. ");
    final botConfig congfig;



    public TelegramBot(botConfig congfig) {
        this.congfig = congfig;
        List<BotCommand> listofCommand = new ArrayList<>();
        listofCommand.add(new BotCommand("/start", "Включить бота"));
        listofCommand.add(new BotCommand("/myparent", "Мой создатель"));
        listofCommand.add(new BotCommand("/help", "Помощь в боте"));
        try {
            this.execute(new SetMyCommands(listofCommand, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error in upload listCommand : " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return congfig.getBotName();
    }

    @Override
    public String getBotToken() {
        return congfig.getToken();
    }

    public void register(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Do you relly want register?");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> rowInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInlineButton = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Yes");
        yesButton.setCallbackData("YES_BUTTON");

        var noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData("NO_BUTTON");
        rowInlineButton.add(yesButton);
        rowInlineButton.add(noButton);

        rowInline.add(rowInlineButton);
        inlineKeyboardMarkup.setKeyboard(rowInline);
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred" + e.getMessage());
        }

    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String name = update.getMessage().getChat().getFirstName();
            String account = update.getMessage().getChat().getUserName();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;

                case "/myparent":
                    sendMessage(chatId, "Мой создатель:  @coffecuthe");
                    break;
                case "/help":
                    sendMessage(chatId, HELP_TEXT);
                    break;
                case "/register":
                    register(chatId);
                    break;
                default:
                    String Browser = "Вот что мне удалось найти в интернете по твоему запросу: " + "https://yandex.ru/search/?from=chromesearch&clid=2224314&text=" + messageText.replace(" ", "+") + "&lr=213";
                    sendMessage(chatId, Browser);
                    log.info("[Username: ] " + account + " [Message :] " + name + " is typing :  " + messageText);
            }

        } else if(update.hasCallbackQuery()){
            String callbackData = update.getCallbackQuery().getData();
            long messageId= update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if(callbackData.equals("YES_BUTTON")){

            } else if (callbackData.equals("NO_BUTTON")) {
                
            }
        }

    }

    private void startCommandReceived(long chatId, String name) {

        String answer = EmojiParser.parseToUnicode("Привет :wave:   " + name + " \n" +
                "Данный бот создан в качестве поисковой системы.\n" +
                "Напиши ниже что хочешь найти: ");
        sendMessage(chatId, answer);

    }

    private void sendMessage(long chatId, String textToSend) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);


        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred" + e.getMessage());
        }
    }
}
